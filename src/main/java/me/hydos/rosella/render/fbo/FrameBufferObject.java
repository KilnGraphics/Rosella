package me.hydos.rosella.render.fbo;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.swapchain.DepthBuffer;
import me.hydos.rosella.render.swapchain.RenderPass;
import me.hydos.rosella.render.swapchain.Swapchain;
import me.hydos.rosella.render.texture.TextureImage;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.util.VkUtils;
import me.hydos.rosella.vkobjects.VkCommon;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static me.hydos.rosella.util.VkUtils.ok;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Represents a FBO.
 */
public class FrameBufferObject {

    public final DepthBuffer depthBuffer = new DepthBuffer();
    public boolean isSwapchainBased;
    public List<Long> imageViews;
    public List<TextureImage> images;
    public List<Long> frameBuffers;
    public VkCommandBuffer[] commandBuffers;
    public SimpleObjectManager objectManager;

    public FrameBufferObject(boolean useSwapchainImages, Swapchain swapchain, VkCommon common, RenderPass renderPass, Renderer renderer, SimpleObjectManager objectManager) {
        this.objectManager = objectManager.duplicate();
        this.frameBuffers = new LongArrayList(swapchain.getImageCount());
        this.isSwapchainBased = useSwapchainImages;
        if (useSwapchainImages) {
            setSwapchainImages(swapchain, common);
        } else {
            setBlankImages(swapchain, common, renderer);
        }

        depthBuffer.createDepthResources(common.device, common.memory, swapchain, renderer);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer attachments = stack.longs(VK_NULL_HANDLE, depthBuffer.getDepthImage().getView());
            LongBuffer pFramebuffer = stack.mallocLong(1);
            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .renderPass(renderPass.getRawRenderPass())
                    .width(swapchain.getSwapChainExtent().width())
                    .height(swapchain.getSwapChainExtent().height())
                    .layers(1);
            for (long imageView : imageViews) {
                attachments.put(0, imageView);
                framebufferInfo.pAttachments(attachments);
                ok(vkCreateFramebuffer(common.device.getRawDevice(), framebufferInfo, null, pFramebuffer));
                frameBuffers.add(pFramebuffer.get(0));
            }
        }
    }

    protected void setSwapchainImages(Swapchain swapchain, VkCommon common) {
        imageViews = new ArrayList<>(swapchain.getImageCount());
        for (long swapChainImage : swapchain.getSwapChainImages()) {
            imageViews.add(
                    VkUtils.createImageView(
                            common.device,
                            swapChainImage,
                            swapchain.getSwapChainImageFormat(),
                            VK_IMAGE_ASPECT_COLOR_BIT
                    )
            );
        }
    }

    protected void setBlankImages(Swapchain swapchain, VkCommon common, Renderer renderer) {
        imageViews = new ArrayList<>(swapchain.getImageCount());
        this.images = new ArrayList<>();
        for (int i = 0; i < swapchain.getImageCount(); i++) {
            TextureImage image = VkUtils.createImage(
                    common.memory,
                    swapchain.getSwapChainExtent().width(),
                    swapchain.getSwapChainExtent().height(),
                    swapchain.getSwapChainImageFormat(),
                    VK_IMAGE_TILING_OPTIMAL,
                    VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                    Vma.VMA_MEMORY_USAGE_UNKNOWN // FIXME
            );
            VkUtils.transitionImageLayout(
                    renderer,
                    common.device,
                    image.pointer(),
                    swapchain.getSwapChainImageFormat(),
                    VK_IMAGE_LAYOUT_UNDEFINED,
                    VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
            );

            images.add(image);
        }

        for (TextureImage image : images) {
            imageViews.add(
                    VkUtils.createImageView(
                            common.device,
                            image.pointer(),
                            swapchain.getSwapChainImageFormat(),
                            VK_IMAGE_ASPECT_COLOR_BIT
                    )
            );
        }
    }

    public void free(VkCommon common) {
        depthBuffer.free(common.device, common.memory);
        for (long framebuffer : frameBuffers) {
            vkDestroyFramebuffer(
                    common.device.getRawDevice(),
                    framebuffer,
                    null
            );
        }

        for (Long imageView : imageViews) {
            vkDestroyImageView(
                    common.device.getRawDevice(),
                    imageView,
                    null
            );
        }
    }

    public void clearCommandBuffers(VulkanDevice device, long commandPool) {
        if (commandBuffers != null) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                vkFreeCommandBuffers(device.getRawDevice(), commandPool, stack.pointers(commandBuffers));
            }
            commandBuffers = null;
        }
    }
}

