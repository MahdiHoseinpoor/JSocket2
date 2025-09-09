package JSocket2.Protocol;

import java.util.UUID;

/**
 * Represents the header of a {@link Message}.
 * It contains metadata about the message, such as its type, ID, and length of its parts.
 * This class provides static factory methods for building headers for specific message types.
 */
public class MessageHeader {
    /**
     * A unique identifier for the message, used for request-response matching.
     */
    public final UUID uuid;
    /**
     * The type of the message, indicating its purpose.
     */
    public final MessageType type;
    /**
     * A flag indicating if an acknowledgment is required for this message.
     */
    public final boolean is_need_ack;
    /**
     * A flag indicating if the message payload and metadata are encrypted.
     */
    public final boolean is_encrypted;
    /**
     * The timestamp when the message was created.
     */
    public final long timestamp;
    /**
     * The length of the metadata in bytes.
     */
    public int metadata_length;
    /**
     * The length of the payload in bytes.
     */
    public int payload_length;

    /**
     * Constructs a MessageHeader with a new timestamp.
     *
     * @param uuid           The unique identifier.
     * @param type           The message type.
     * @param isNeedAck      True if acknowledgment is needed.
     * @param isEncrypted    True if the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength  The length of the payload.
     */
    public MessageHeader(UUID uuid,MessageType type, boolean isNeedAck, boolean isEncrypted, int metadataLength, int payloadLength){
        this.uuid = uuid;
        this.type = type;
        is_need_ack = isNeedAck;
        is_encrypted = isEncrypted;
        this.timestamp = System.currentTimeMillis();
        metadata_length = metadataLength;
        payload_length = payloadLength;
    }

    /**
     * Constructs a MessageHeader with a specified timestamp.
     *
     * @param uuid           The unique identifier.
     * @param type           The message type.
     * @param isNeedAck      True if acknowledgment is needed.
     * @param isEncrypted    True if the message is encrypted.
     * @param timestamp      The creation timestamp.
     * @param metadataLength The length of the metadata.
     * @param payloadLength  The length of the payload.
     */
    public MessageHeader(UUID uuid,MessageType type, boolean isNeedAck, boolean isEncrypted, long timestamp,int metadataLength, int payloadLength){
        this.uuid = uuid;
        this.type = type;
        is_need_ack = isNeedAck;
        is_encrypted = isEncrypted;
        this.timestamp = timestamp;
        metadata_length = metadataLength;
        payload_length = payloadLength;
    }

    /**
     * Constructs a MessageHeader with zero-length metadata and payload.
     *
     * @param uuid        The unique identifier.
     * @param type        The message type.
     * @param isNeedAck   True if acknowledgment is needed.
     * @param isEncrypted True if the message is encrypted.
     */
    public MessageHeader(UUID uuid,MessageType type, boolean isNeedAck, boolean isEncrypted){
        this.uuid = uuid;
        this.type = type;
        is_need_ack = isNeedAck;
        is_encrypted = isEncrypted;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Builds a header for an RPC response message.
     * @param uuid The UUID of the original RPC call.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildRpcResponseHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength){
        return new MessageHeader(uuid,MessageType.RPC_RESPONSE,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for an RPC call message.
     * @param uuid A unique UUID for the call.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildRpcCallHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength){
        return new MessageHeader(uuid,MessageType.RPC_CALL,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for a file upload acknowledgment.
     * @param uuid The UUID of the original upload request.
     * @param metadataLength The length of the metadata.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildUploadAckHeader(UUID uuid,int metadataLength) {
        return new MessageHeader(uuid,MessageType.UPLOAD_ACK,false,false,metadataLength,4);
    }

    /**
     * Builds a header for a file upload request.
     * @param uuid A unique UUID for the upload.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildUploadRequestHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength){
        return new MessageHeader(uuid,MessageType.UPLOAD_REQUEST,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for a file download request.
     * @param uuid A unique UUID for the download.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildDownloadRequestHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength){
        return new MessageHeader(uuid,MessageType.DOWNLOAD_REQUEST,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header to start a file download.
     * @param uuid The UUID of the download request.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildStartDownloadRequestHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength){
        return new MessageHeader(uuid,MessageType.DOWNLOAD_START,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for a file upload chunk.
     * @param uuid The UUID of the file upload.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildUploadChunkHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength) {
        return new MessageHeader(uuid,MessageType.UPLOAD_CHUNK,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for sending a file chunk (for downloads).
     * @param uuid The UUID of the file download.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildSendChunkHeader(UUID uuid,boolean is_encrypted,int metadataLength, int payloadLength) {
        return new MessageHeader(uuid,MessageType.SEND_CHUNK,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header to acknowledge the receipt of a file chunk.
     * @param uuid The UUID of the file transfer.
     * @param metadataLength The length of the metadata.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildReceiveChunkAckHeader(UUID uuid,int metadataLength) {
        return new MessageHeader(uuid,MessageType.RECEIVE_CHUNK_ACK,false,false,metadataLength,4);
    }

    /**
     * Builds a header for a resume upload request acknowledgment.
     * @param uuid The UUID of the upload resume request.
     * @param is_encrypted Whether the message is encrypted.
     * @param metadataLength The length of the metadata.
     * @param payloadLength The length of the payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildResumeUploadRequestAckHeader(UUID uuid, boolean is_encrypted, int metadataLength, int payloadLength) {
        return new MessageHeader(uuid,MessageType.UPLOAD_RESUME_REQUEST,false,is_encrypted,metadataLength,payloadLength);
    }

    /**
     * Builds a header for sending an RSA public key.
     * @param uuid A unique UUID for the key exchange.
     * @param payloadLength The length of the key.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildRsaPublicKeyHeader(UUID uuid, int payloadLength) {
        return new MessageHeader(uuid, MessageType.RSA_PUBLIC_KEY, true, false, 0, payloadLength);
    }

    /**
     * Builds a header for sending an AES key.
     * @param uuid The UUID of the key exchange request.
     * @param payloadLength The length of the encrypted key.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildAesKeyHeader(UUID uuid, int payloadLength) {
        return new MessageHeader(uuid,MessageType.AES_KEY,true,true,0,payloadLength);
    }

    /**
     * Builds a header for an authentication message.
     * @param uuid A unique UUID for the authentication request.
     * @param payloadLength The length of the authentication payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildAuthHeader(UUID uuid, int payloadLength) {
        return new MessageHeader(uuid,MessageType.AUTH,true,true,0,payloadLength);
    }

    /**
     * Builds a header for an event message.
     * @param uuid A unique UUID for the event.
     * @param metadataLength The length of the event metadata.
     * @param payloadLength The length of the event payload.
     * @return A new MessageHeader instance.
     */
    public static MessageHeader BuildEventHeader(UUID uuid,int metadataLength, int payloadLength) {
        return new MessageHeader(uuid,MessageType.EVENT,true,true,metadataLength,payloadLength);
    }
}