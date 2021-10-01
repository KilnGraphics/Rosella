package graphics.kiln.rosella.render.graph.memory;

public record MemoryTypeRestriction(int memoryTypeOverride, int preferredTypeMask, int requiredTypeMask, int preferredFlagMask, int requiredFlagMask) {

    public static MemoryTypeRestriction makeOverride(int memoryType) {
        return new MemoryTypeRestriction(memoryType, 0, 0, 0, 0);
    }

    public static MemoryTypeRestriction makeTypeRestriction(int preferredTypeMask, int requiredTypeMask) {
        return new MemoryTypeRestriction(-1, preferredTypeMask, requiredTypeMask, 0, 0);
    }

    public static MemoryTypeRestriction makeFlagRestriction(int preferredFlagMask, int requiredFlagMask) {
        return new MemoryTypeRestriction(-1, 0, 0, preferredFlagMask, requiredFlagMask);
    }

    public boolean isOverride() {
        return this.memoryTypeOverride != -1;
    }
}
