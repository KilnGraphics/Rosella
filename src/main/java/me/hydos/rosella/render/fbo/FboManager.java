package me.hydos.rosella.render.fbo;

import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.swapchain.Swapchain;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.vkobjects.VkCommon;

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

    public void recreateSwapchainImageViews(Swapchain swapchain, VkCommon common, Renderer renderer) {
        for (FrameBufferObject fbo : fbos) {
            if (fbo.isSwapchainBased) {
                fbo.setSwapchainImages(swapchain, common);
            } else {
                fbo.setBlankImages(swapchain, common, renderer);
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
}
