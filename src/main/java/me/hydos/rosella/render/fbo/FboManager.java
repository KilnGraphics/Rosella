package me.hydos.rosella.render.fbo;

import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.swapchain.Swapchain;
import me.hydos.rosella.vkobjects.VkCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all Frame Buffer Objects
 */
public class FboManager {

    public FrameBufferObject mainFbo; //FIXME: this is useless because we hardcode it in the swapchain. ill get to it soon
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
            fbo.setSwapchainImages(swapchain, common);
        }
    }

    public void setMainFbo(FrameBufferObject mainFbo) {
        if(this.mainFbo != null) {
            throw new RuntimeException("Tried to replace existing Main Frame Buffer Object");
        }
        this.mainFbo = mainFbo;
        fbos.add(mainFbo);
    }
}
