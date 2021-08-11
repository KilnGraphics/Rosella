package me.hydos.rosella.device;

public class QueueFamilyIndices {

    public static final int UNKNOWN_QUEUE_FAMILY_INDEX = Integer.MIN_VALUE;

    public int graphicsFamily;
    public int presentFamily;

    public boolean isComplete() {
        return graphicsFamily != UNKNOWN_QUEUE_FAMILY_INDEX && presentFamily != UNKNOWN_QUEUE_FAMILY_INDEX;
    }
}
