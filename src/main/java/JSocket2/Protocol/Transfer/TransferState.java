package JSocket2.Protocol.Transfer;

/**
 * Represents the current state of a file transfer.
 */
public enum TransferState {
    /** The transfer is currently active. */
    InProcess,
    /** The transfer has been successfully completed. */
    Complete,
    /** The transfer has been paused and can be resumed later. */
    Paused
}