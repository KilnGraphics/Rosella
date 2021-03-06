#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 0) uniform UniformBufferObject {
    mat4 model;
    mat4 view;
    mat4 proj;
} ubo;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inColor;
layout(location = 2) in vec2 inTexCoord;

layout(location = 0) out vec3 fragColor;
layout(location = 1) out vec2 fragTexCoord;

void main() {
    // Remove position from the view matrix and model matrix
    mat4 viewMatrix = mat4(ubo.view);
    viewMatrix[3][0] = 0;
    viewMatrix[3][1] = 0;
    viewMatrix[3][2] = 0;

    vec4 worldPosition = ubo.model * vec4(inPosition, 1.0);

    gl_Position = ubo.proj * viewMatrix * worldPosition;
    fragColor = inColor;
    fragTexCoord = inTexCoord;
}