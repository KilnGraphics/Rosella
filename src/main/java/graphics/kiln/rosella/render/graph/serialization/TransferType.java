package graphics.kiln.rosella.render.graph.serialization;

public enum TransferType {
    COMPLETE_INCOMPLETE(true, true),
    COMPLETE_ONLY(true, false),
    INCOMPLETE_ONLY(false, true);

    public final boolean allowComplete;
    public final boolean allowIncomplete;

    TransferType(boolean allowComplete, boolean allowIncomplete) {
        this.allowComplete = allowComplete;
        this.allowIncomplete = allowIncomplete;
    }
}
