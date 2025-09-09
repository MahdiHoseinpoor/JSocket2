package JSocket2.Protocol.Rpc;

/**
 * A data model that holds the metadata for an RPC response,
 * including the status code and an optional message.
 */
public class RpcResponseMetadata {
    private int statusCode;
    private String message;

    /**
     * Constructs an {@code RpcResponseMetadata}.
     *
     * @param statusCode The integer value of the status code.
     * @param message    An optional descriptive message.
     */
    public RpcResponseMetadata(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * Gets the status code.
     *
     * @return The status code as an integer.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode The status code as an integer.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the message.
     *
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message The response message.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}