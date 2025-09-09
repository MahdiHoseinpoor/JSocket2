package JSocket2.Protocol;

/**
 * Defines the different types of messages that can be exchanged between client and server.
 * Each message type has a unique integer code for serialization.
 */
public enum MessageType {
    /** Remote Procedure Call */
    RPC_CALL(10),
    /** Response to an RPC */
    RPC_RESPONSE(11),

    /** Request to upload a file */
    UPLOAD_REQUEST(20),
    /** A chunk of a file being uploaded */
    UPLOAD_CHUNK(21),
    /** Acknowledgment of a successful upload */
    UPLOAD_ACK(23),
    /** Request to resume a paused upload */
    UPLOAD_RESUME_REQUEST(24),

    /** Request to download a file */
    DOWNLOAD_REQUEST(30),
    /** A command to start sending download chunks */
    DOWNLOAD_START(34),

    /** A message containing an RSA public key for handshake */
    RSA_PUBLIC_KEY(40),
    /** A message containing an AES key, encrypted with RSA */
    AES_KEY(41),
    /** Request to change the AES key */
    CHANGE_AES_KEY_REQUEST(42),

    /** A chunk of a file being sent (e.g., for downloads) */
    SEND_CHUNK(51),
    /** Acknowledgment of receiving a file chunk */
    RECEIVE_CHUNK_ACK(52),

    /** Authentication message */
    AUTH(60),

    /** A real-time event message pushed from the server */
    EVENT(70);

    /**
     * The unique integer code for the message type.
     */
    public final int code;

    MessageType(int code) {
        this.code = code;
    }

    /**
     * Converts an integer code to its corresponding MessageType enum constant.
     *
     * @param code The integer code.
     * @return The matching MessageType.
     * @throws IllegalArgumentException if the code is unknown.
     */
    public static MessageType fromCode(int code) {
        for (MessageType type : values()) {
            if (type.code == code)
                return type;
        }
        throw new IllegalArgumentException("Unknown MessageType code: " + code);
    }
}