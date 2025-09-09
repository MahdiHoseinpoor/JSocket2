package Protocol.EventHub;

import JSocket2.Core.Server.ServerSessionManager;
import JSocket2.Protocol.EventHub.EventBase;

import java.io.IOException;

/**
 * A test implementation of a server-side event definition ({@link EventBase}).
 * This class is intended for tests related to the server's event publishing logic,
 * specifically for events that do not use a predefined model.
 */
public class withoutModelEvent extends EventBase {
    /**
     * The invocation logic for the event. This implementation is empty as it is for testing purposes only.
     * @param serverSessionManager The manager to access client sessions.
     * @param receiverId The ID of the client or group to receive the event.
     * @param args The payload data for the event.
     * @throws IOException If a communication error occurs.
     */
    @Override
    public void Invoke(ServerSessionManager serverSessionManager, String receiverId, Object... args) throws IOException {
        // Intentionally empty for testing.
    }
}