package JSocket2.Protocol;

/**
 * Represents a data packet exchanged between the client and server.
 * A message consists of a header, optional metadata, an optional payload,
 * and an initialization vector (IV) for encrypted messages.
 */
public class Message {
    /**
     * The header of the message containing essential metadata.
     */
    public final MessageHeader header;
    private byte[] ivBytes = new byte[0];
    private byte[] metadata = new byte[0];
    private byte[] payload = new byte[0];

    /**
     * Constructs a message with only a header.
     *
     * @param header The message header.
     */
    public Message(MessageHeader header){
        this.header = header;
    }

    /**
     * Constructs a message with a header, metadata, and payload.
     *
     * @param header   The message header.
     * @param metadata The message metadata.
     * @param payload  The message payload.
     */
    public Message(MessageHeader header, byte[] metadata,byte[] payload){
        this.header = header;
        this.metadata = metadata;
        this.payload = payload;
    }

    /**
     * Constructs a message with a header, metadata, payload, and initialization vector.
     *
     * @param header   The message header.
     * @param metadata The message metadata.
     * @param payload  The message payload.
     * @param ivBytes  The initialization vector for encryption.
     */
    public Message(MessageHeader header, byte[] metadata,byte[] payload,byte[] ivBytes){
        this.header = header;
        this.metadata = metadata;
        this.payload = payload;
        this.ivBytes = ivBytes;
    }

    /**
     * Sets the payload of the message and updates the header's payload length.
     *
     * @param payload The payload data.
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
        header.payload_length = payload.length;
    }

    /**
     * Gets the payload of the message.
     *
     * @return The payload data.
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Gets the metadata of the message.
     *
     * @return The metadata.
     */
    public byte[] getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata of the message and updates the header's metadata length.
     *
     * @param metadata The metadata.
     */
    public void setMetadata(byte[] metadata) {
        this.metadata = metadata;
        header.metadata_length = metadata.length;
    }

    /**
     * Gets the initialization vector (IV) used for encryption.
     *
     * @return The IV bytes.
     */
    public byte[] getIvBytes() {
        return ivBytes;
    }

    /**
     * Sets the initialization vector (IV) used for encryption.
     *
     * @param ivBytes The IV bytes.
     */
    public void setIvBytes(byte[] ivBytes) {
        this.ivBytes = ivBytes;
    }
}