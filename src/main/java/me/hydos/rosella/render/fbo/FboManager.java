package me.hydos.rosella.render.fbo;

import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.swapchain.Swapchain;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.vkobjects.VkCommon;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all Frame Buffer Objects
 */
public class FboManager {

    private FrameBufferObject mainFbo; //FIXME: this is useless because we hardcode it in the swapchain. ill get to it soon
    public FrameBufferObject activeFbo; // FIXME: same as above
    public List<FrameBufferObject> fbos = new ArrayList<>();

    public void recreateDepthResources(Swapchain swapchain, VkCommon common, Renderer renderer) {
        for (FrameBufferObject fbo : fbos) {
            fbo.depthBuffer.createDepthResources(common.device, common.memory, swapchain, renderer);
        }
    }

    public void free(VkCommon common) {
        for (FrameBufferObject fbo : fbos) {
            fbo.free(common);
        }
    }

    public void recreateSwapchainImageViews(Swapchain swapchain, VkCommon common) {
        for (FrameBufferObject fbo : fbos) {
            if (fbo.isSwapchainBased) {
                fbo.setSwapchainImages(swapchain, common);
            }
        }
    }

    public void setMainFbo(FrameBufferObject mainFbo) {
        if (this.mainFbo != null) {
            throw new RuntimeException("Tried to replace existing Main Frame Buffer Object");
        }
        this.mainFbo = mainFbo;
        fbos.add(mainFbo);
    }

    public SimpleObjectManager getObjectManager() {
        return getActiveFbo().objectManager;
    }

    public FrameBufferObject getActiveFbo() {
        if (this.activeFbo == null) {
            return mainFbo;
        } else {
            return activeFbo;
        }
    }

    public FrameBufferObject addFbo(FrameBufferObject frameBufferObject) {
        this.fbos.add(frameBufferObject);
        return frameBufferObject;
    }

    public FrameBufferObject getPresentingFbo() {
        return mainFbo;
    }

    public PointerBuffer setRenderingCommandBuffers(int imageIndex) {
        VkCommandBuffer[] commandBuffers = new VkCommandBuffer[fbos.size()]; // FIXME: TODO: URGENT: cache this. this code is so hot its hotter than ur mum. (Owned)
        for (int i = 0; i < fbos.size(); i++) { //FIXME: life is pain
            FrameBufferObject fbo = fbos.get(i);
            if (fbo.isSwapchainBased) {
                commandBuffers[i] = fbo.commandBuffers[imageIndex];
            } else {
                //FIXME: i only make 1 fbo for fbo's which are not being presented. i fucking hate myself
                commandBuffers[i] = fbo.commandBuffers[0];
            }
        }
        MemoryStack stack = MemoryStack.stackGet();
        return stack.pointers(commandBuffers);
    }
}
