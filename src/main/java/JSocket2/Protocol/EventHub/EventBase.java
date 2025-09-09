package JSocket2.Protocol.EventHub;

import JSocket2.Core.Server.ServerSessionManager;
import JSocket2.Protocol.Message;
import JSocket2.Protocol.MessageHeader;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * An abstract base class for defining events that can be sent to clients.
 * It provides the core functionality to create an event message.
 */
public abstract class EventBase {
    /**
     * An instance of {@link Gson} for JSON serialization and deserialization.
     */
    protected Gson gson = new Gson();

    /**
     * When implemented in a derived class, this method sends the event to a specific client.
     *
     * @param serverSessionManager The manager responsible for handling client sessions.
     * @param receiverId           The unique identifier of the client who will receive the event.
     * @param args                 The payload data for the event.
     * @throws IOException if a network error occurs during sending.
     */
    public abstract void Invoke(ServerSessionManager serverSessionManager, String receiverId, Object... args) throws IOException;

    /**
     * Creates a {@link Message} object representing the event.
     * The message is structured with a header, metadata (containing the event name), and the payload.
     *
     * @param eventName     The name of the event.
     * @param payloadObject The data to be sent with the event.
     * @return A {@link Message} object ready to be sent.
     */
    protected Message createEventMessage(String eventName, Object[] payloadObject) {
        EventMetadata metadata = new EventMetadata(eventName);
        String metadataJson = gson.toJson(metadata);
        String payloadJson = gson.toJson(payloadObject);
        byte[] metadataBytes = metadataJson.getBytes(StandardCharsets.UTF_8);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        MessageHeader header = MessageHeader.BuildEventHeader(UUID.randomUUID(), metadataBytes.length, payloadBytes.length);
        return new Message(header, metadataBytes, payloadBytes);
    }
}