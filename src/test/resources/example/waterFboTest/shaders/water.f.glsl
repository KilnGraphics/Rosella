#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 1) uniform sampler2D texSampler_0;
layout(binding = 2) uniform sampler2D texSampler_1;

layout(location = 0) in vec4 clipSpace;
layout(location = 1) in vec3 toCameraVector;

layout(location = 0) out vec4 outColor;

void main() {
    vec2 normalisedDeviceSpace = (clipSpace.xy/clipSpace.w) / 2.0 + 0.5;
    vec2 reflectTexCoords = vec2(normalisedDeviceSpace.x, -normalisedDeviceSpace.y);
    vec2 refractTexCoords = vec2(normalisedDeviceSpace.x, normalisedDeviceSpace.y);

    vec4 reflectionColour = texture(texSampler_0, reflectTexCoords);
    vec4 refractionColour = texture(texSampler_1, refractTexCoords);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));

    outColor = mix(reflectionColour, refractionColour, refractiveFactor);
}