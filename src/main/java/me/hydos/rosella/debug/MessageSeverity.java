package me.hydos.rosella.debug;

import org.lwjgl.vulkan.EXTDebugUtils;

public enum MessageSeverity {
    VERBOSE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
    INFO(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
    WARNING(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
    ERROR(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);

    public final int bits;

    MessageSeverity(int bits) {
        this.bits = bits;
    }

    public boolean isInMask(int mask) {
        return (mask & this.bits) == this.bits;
    }

    public static int allBits() {
        return VERBOSE.bits | INFO.bits | WARNING.bits | ERROR.bits;
    }

    public static MessageSeverity fromBits(int bits) {
        return switch(bits) {
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> VERBOSE;
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> INFO;
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> WARNING;
            case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> ERROR;
            default -> throw new RuntimeException("Bits are either a combination of bits or invalid");
        };
    }
}
