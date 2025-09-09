package JSocket2.Protocol.Rpc;

import JSocket2.Protocol.StatusCode;

/**
 * Represents the standard response for an RPC call. It encapsulates a status code,
 * an optional message, and a generic payload.
 *
 * @param <T> The type of the payload data.
 */
public class RpcResponse<T> {
    private StatusCode statusCode;
    private String message;
    private T payload;

    /**
     * Constructs a new {@code RpcResponse}.
     *
     * @param statusCode The status of the response.
     * @param message    An optional descriptive message.
     * @param payload    The data payload of the response.
     */
    public RpcResponse(StatusCode statusCode, String message, T payload) {
        this.statusCode = statusCode;
        this.payload = payload;
        this.message = message;
    }

    /**
     * Gets the status code.
     *
     * @return The {@link StatusCode}.
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode The {@link StatusCode}.
     */
    public void setStatusCode(StatusCode statusCode) {
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

    /**
     * Gets the payload.
     *
     * @return The response payload.
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Sets the payload.
     *
     * @param payload The response payload.
     */
    public void setPayload(T payload) {
        this.payload = payload;
    }
}