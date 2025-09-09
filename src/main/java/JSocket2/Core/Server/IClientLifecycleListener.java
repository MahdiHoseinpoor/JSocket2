package JSocket2.Core.Server;

/**
 * Defines a contract for components that need to be notified of client
 * lifecycle events, such as authentication and disconnection. This allows the
 * application layer to react to changes in client state.
 */
public interface IClientLifecycleListener {
    /**
     * Invoked when a client's session has been successfully authenticated.
     *
     * @param session The {@link ServerSession} of the authenticated client.
     */
    void onClientAuthenticated(ServerSession session);

    /**
     * Invoked when a client disconnects or the connection is lost.
     *
     * @param session The {@link ServerSession} of the client that has disconnected.
     */
    void onClientDisconnected(ServerSession session);
}