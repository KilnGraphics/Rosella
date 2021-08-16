#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 1) uniform sampler2D texSampler;

layout(location = 0) in vec3 surfaceNormal;
layout(location = 1) in vec2 fragTexCoord;
layout(location = 2) in vec3 toLightVector;

layout(location = 0) out vec4 outColor;

void main() {
    // CONSTANTS
    vec3 lightColour = vec3(1.0, 1.0, 1.0);
    float intensity = 1.0;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector) * intensity;

    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.2);
    vec3 diffuse = brightness * lightColour;

    outColor = vec4(diffuse, 1.0) * texture(texSampler, fragTexCoord);
}