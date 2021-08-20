package me.hydos.rosella.render.fbo;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.swapchain.DepthBuffer;
import me.hydos.rosella.render.swapchain.RenderPass;
import me.hydos.rosella.render.swapchain.Swapchain;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.util.VkUtils;
import me.hydos.rosella.vkobjects.VkCommon;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static me.hydos.rosella.util.VkUtils.ok;
import static org.lwjgl.system.JNI.callPPPPI;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.vulkan.VK10.*;

/**
 * Represents a FBO.
 */
public class FrameBufferObject {

    public final DepthBuffer depthBuffer = new DepthBuffer();
    public boolean isSwapchainBased;
    public List<Long> imageViews;
    public List<Long> frameBuffers;
    public VkCommandBuffer[] commandBuffers;
    public SimpleObjectManager objectManager;

    public FrameBufferObject(boolean useSwapchainImages, Swapchain swapchain, VkCommon common, RenderPass renderPass, Renderer renderer, SimpleObjectManager objectManager) {
        this.objectManager = objectManager.duplicate();
        this.frameBuffers = new LongArrayList(swapchain.getImageCount());
        this.isSwapchainBased = useSwapchainImages;
        if (useSwapchainImages) {
            setSwapchainImages(swapchain, common);
        }

        depthBuffer.createDepthResources(common.device, common.memory, swapchain, renderer);
        if (useSwapchainImages) {
            setupFboBasedOnImageViews(swapchain, common, renderPass);
        } else {
            setupFboUsingExperimentalStuff(swapchain, common, renderPass);
        }
    }

    private void setupFboBasedOnImageViews(Swapchain swapchain, VkCommon common, RenderPass renderPass) {
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

    private void setupFboUsingExperimentalStuff(Swapchain swapchain, VkCommon common, RenderPass renderPass) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pFramebuffer = stack.mallocLong(1);

            VkFramebufferAttachmentImageInfo.Buffer attachmentImageInfos = VkFramebufferAttachmentImageInfo.callocStack(2, stack);

            attachmentImageInfos.get(0)
                    .sType(VK12.VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENT_IMAGE_INFO)
                    .width(swapchain.getSwapChainExtent().width())
                    .height(swapchain.getSwapChainExtent().height())
                    .layerCount(1)
                    .pViewFormats(stack.ints(swapchain.getSwapChainImageFormat()))
                    .usage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT);

            attachmentImageInfos.get(1)
                    .sType(VK12.VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENT_IMAGE_INFO)
                    .width(swapchain.getSwapChainExtent().width())
                    .height(swapchain.getSwapChainExtent().height())
                    .layerCount(1)
                    .pViewFormats(stack.ints(DepthBuffer.findDepthFormat(common.device)))
                    .usage(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT);

            VkFramebufferAttachmentsCreateInfo.Buffer attachmentCreateInfo = VkFramebufferAttachmentsCreateInfo.callocStack(1, stack)
                    .sType(VK12.VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENTS_CREATE_INFO)
                    .pAttachmentImageInfos(attachmentImageInfos);

            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pNext(attachmentCreateInfo.address())
                    .flags(VK12.VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT)
                    .renderPass(renderPass.getRawRenderPass())
                    .width(swapchain.getSwapChainExtent().width())
                    .height(swapchain.getSwapChainExtent().height())
                    .layers(1);

            // FIXME: Very big LWJGL3 Bug https://github.com/LWJGL/lwjgl3/issues/673
            // FIXME: Everything below this line is a hack minus the last line
            VkFramebufferCreateInfo.nattachmentCount(framebufferInfo.address(), 2);

//            ok(vkCreateFramebuffer(common.device.getRawDevice(), framebufferInfo, null, pFramebuffer));
            ok(callPPPPI(common.device.getRawDevice().address(), framebufferInfo.address(), null, memAddress(pFramebuffer), common.device.getRawDevice().getCapabilities().vkCreateFramebuffer));
            frameBuffers.add(pFramebuffer.get(0));
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

    public void free(VkCommon common) {
        depthBuffer.free(common.device, common.memory);
        for (long framebuffer : frameBuffers) {
            vkDestroyFramebuffer(
                    common.device.getRawDevice(),
                    framebuffer,
                    null
            );
        }

        if (isSwapchainBased) {
            for (Long imageView : imageViews) {
                vkDestroyImageView(
                        common.device.getRawDevice(),
                        imageView,
                        null
                );
            }
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

