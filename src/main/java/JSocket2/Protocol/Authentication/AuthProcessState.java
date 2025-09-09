package JSocket2.Protocol.Authentication;

/**
 * Represents the various states of an authentication process.
 */
public enum AuthProcessState {
    /**
     * The initial state before any authentication attempt has been made.
     */
    NONE,
    /**
     * The state where an authentication attempt is currently in progress.
     */
    PENDING,
    /**
     * The state indicating a successful authentication.
     */
    SUCCESS,
    /**
     * The state indicating a failed authentication attempt.
     */
    FAILED
}