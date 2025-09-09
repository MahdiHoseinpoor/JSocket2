package JSocket2.Protocol;

/**
 * Defines a listener for connection-related events.
 * Implement this interface to handle events like connection loss.
 */
public interface IConnectionEventListener {
    /**
     * Called when the connection to the remote endpoint is lost.
     */
    void onConnectionLost();
}