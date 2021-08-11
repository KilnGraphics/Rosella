package me.hydos.rosella.render.fbo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Framebuffer Object. contains everything needed to render to it
 */
public class Framebuffer {

    public List<Long> swapChainImageViews = new ArrayList<>();
    public List<Long> frameBuffers = new ArrayList<>();
    public List<Long> swapChainImages = new ArrayList<>();
}
