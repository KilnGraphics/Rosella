package graphics.kiln.rosella.render.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.render.Topology;
import graphics.kiln.rosella.render.renderer.Renderer;
import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.vkobjects.VkCommon;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Map;

import static graphics.kiln.rosella.util.VkUtils.ok;

// TODO: figure out how to deal with renderpasses and abstraction so they can still be referenced after recreation
// EX: a pipeline wants to use the main renderpass, but the main renderpass has been replaced due to a
// swapchain recreation.
public class PipelineManager {
    private final VkCommon common;
    private final Renderer renderer;

    private final Map<Pipeline, Pipeline> pipelines = new Object2ObjectOpenHashMap<>();

    public PipelineManager(VkCommon common, Renderer renderer) {
        this.common = common;
        this.renderer = renderer;
    }

    public Pipeline registerPipeline(Pipeline pipeline) {
        // deduplicates pipelines and avoids excess initialization
        return pipelines.computeIfAbsent(pipeline, newPipeline -> initializePipeline(common.device, renderer.swapchain, newPipeline));
    }

    public void rebuildPipelines() {
        for (Pipeline pipeline : pipelines.keySet()) {
            pipeline.free(common.device, common.memory);
            initializePipeline(common.device, renderer.swapchain, pipeline);
        }
    }

    public void free() {
        for (Pipeline pipeline : pipelines.keySet()) {
            pipeline.free(common.device, common.memory);
        }
        pipelines.clear();
    }

    /**
     * Initializes an existing pipeline with the creation data stored inside it.
     */
    private Pipeline initializePipeline(VulkanDevice device, Swapchain swapchain, Pipeline pipeline) {
        long pipelineLayout;
        long graphicsPipeline;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long vertShaderModule = pipeline.getShaderProgram().getVertShaderModule();
            long fragShaderModule = pipeline.getShaderProgram().getFragShaderModule();

            ByteBuffer entryPoint = stack.UTF8("main");
            VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.callocStack(2, stack);

            shaderStages.get(0)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(VK10.VK_SHADER_STAGE_VERTEX_BIT)
                    .module(vertShaderModule)
                    .pName(entryPoint);

            shaderStages.get(1)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                    .stage(VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
                    .module(fragShaderModule)
                    .pName(entryPoint);

            VkPipelineVertexInputStateCreateInfo vertexInputInfo = pipeline.getVertexFormat().getPipelineVertexInputStateCreateInfo();

            VkPipelineInputAssemblyStateCreateInfo inputAssembly = getPipelineInputAssemblyStateCreateInfo(pipeline.getTopology());

            VkViewport.Buffer viewport = getViewport(swapchain);

            VkRect2D.Buffer scissor = pipeline.getStateInfo().isScissorEnabled() ? pipeline.getStateInfo().getExtent() : getDefaultScissor(swapchain);

            VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                    .pViewports(viewport)
                    .pScissors(scissor);

            VkPipelineRasterizationStateCreateInfo rasterizer = pipeline.getStateInfo().getRasterizationStateCreateInfo(
                    pipeline.getStateInfo().getPolygonMode().getVkId(),
                    pipeline.getStateInfo().isDepthBiasEnabled(),
                    pipeline.getStateInfo().getDepthBiasConstantFactor(),
                    pipeline.getStateInfo().getDepthBiasSlopeFactor()
            );

            VkPipelineMultisampleStateCreateInfo multisampling = VkPipelineMultisampleStateCreateInfo.callocStack()
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                    .sampleShadingEnable(false)
                    .rasterizationSamples(VK10.VK_SAMPLE_COUNT_1_BIT);

            VkPipelineDepthStencilStateCreateInfo depthStencil = pipeline.getStateInfo().getPipelineDepthStencilStateCreateInfo();

            VkPipelineColorBlendAttachmentState.Buffer colourBlendAttachment = pipeline.getStateInfo().getPipelineColorBlendAttachmentStates();

            VkPipelineColorBlendStateCreateInfo colourBlending = pipeline.getStateInfo().getPipelineColorBlendStateCreateInfo(colourBlendAttachment);

            VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.callocStack(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                    .pSetLayouts(stack.longs(pipeline.getShaderProgram().getRaw().getDescriptorSetLayout()));

            LongBuffer pPipelineLayout = stack.longs(VK10.VK_NULL_HANDLE);
            ok(VK10.vkCreatePipelineLayout(device.getRawDevice(), pipelineLayoutInfo, null, pPipelineLayout));
            pipelineLayout = pPipelineLayout.get(0);

            VkGraphicsPipelineCreateInfo.Buffer pipelineInfo = VkGraphicsPipelineCreateInfo.callocStack(1, stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                    .pStages(shaderStages)
                    .pVertexInputState(vertexInputInfo)
                    .pInputAssemblyState(inputAssembly)
                    .pViewportState(viewportState)
                    .pRasterizationState(rasterizer)
                    .pMultisampleState(multisampling)
                    .pDepthStencilState(depthStencil)
                    .pColorBlendState(colourBlending)
                    .layout(pipelineLayout)
                    .renderPass(pipeline.getRenderPass().getRawRenderPass())
                    .subpass(0)
                    .basePipelineHandle(VK10.VK_NULL_HANDLE)
                    .basePipelineIndex(-1);

            LongBuffer pGraphicsPipeline = stack.mallocLong(1);
            ok(VK10.vkCreateGraphicsPipelines(device.getRawDevice(), VK10.VK_NULL_HANDLE, pipelineInfo, null, pGraphicsPipeline));
            graphicsPipeline = pGraphicsPipeline.get(0);

            // TODO: do this in memory off thread
            VK10.vkDestroyShaderModule(device.getRawDevice(), vertShaderModule, null);
            VK10.vkDestroyShaderModule(device.getRawDevice(), fragShaderModule, null);

            pipeline.setRawInfo(pipelineLayout, graphicsPipeline);

            return pipeline;
        }
    }
    // TODO: Fix once Topology is rewritten in java

    private VkPipelineInputAssemblyStateCreateInfo getPipelineInputAssemblyStateCreateInfo(Topology topology) {
        MemoryStack stack = MemoryStack.stackGet();
        return VkPipelineInputAssemblyStateCreateInfo.callocStack(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(topology.vkType)
                .primitiveRestartEnable(false);
    }
    // TODO: Fix once Swapchain is rewritten in java

    @NotNull
    private VkRect2D.Buffer getDefaultScissor(Swapchain swapchain) {
        MemoryStack stack = MemoryStack.stackGet();
        return VkRect2D.callocStack(1, stack)
                .offset(VkOffset2D.callocStack(stack).set(0, 0))
                .extent(swapchain.getSwapChainExtent());
    }
    // TODO: Fix once Swapchain is rewritten in java

    @NotNull
    private VkViewport.Buffer getViewport(Swapchain swapchain) {
        MemoryStack stack = MemoryStack.stackGet();
        return VkViewport.callocStack(1, stack)
                .x(0.0f)
                .y(0.0f)
                .width(swapchain.getSwapChainExtent().width())
                .height(swapchain.getSwapChainExtent().height())
                .minDepth(0.0f)
                .maxDepth(1.0f);
    }
}
