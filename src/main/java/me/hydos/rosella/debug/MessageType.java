package me.hydos.rosella.debug;

import org.lwjgl.vulkan.EXTDebugUtils;

public enum MessageType {
    GENERAL(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT),
    VALIDATION(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
    PERFORMANCE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);

    public final int bits;

    MessageType(int bits) {
        this.bits = bits;
    }

    public boolean isInMask(int mask) {
        return (mask & this.bits) == this.bits;
    }

    public static int allBits() {
        return GENERAL.bits | VALIDATION.bits | PERFORMANCE.bits;
    }

    public static MessageType fromBits(int bits) {
        return switch (bits) {
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT -> GENERAL;
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT -> VALIDATION;
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT -> PERFORMANCE;
            default -> throw new RuntimeException("Bits are either a combination of bits or invalid");
        };
    }
}
