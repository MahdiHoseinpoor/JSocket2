package JSocket2.Protocol;

/**
 * Defines status codes used in responses, similar to HTTP status codes.
 */
public enum StatusCode {
    // 2xx Success
    /** Indicates that the request has succeeded. */
    OK(200),
    /** Indicates that the request has been fulfilled and has resulted in one or more new resources being created. */
    CREATED(201),
    /** Indicates that the request has been accepted for processing, but the processing is not yet complete. */
    ACCEPTED(202),
    /** Indicates that the server has successfully fulfilled the request and there is no additional content to send in the response payload body. */
    NO_CONTENT(204),

    // 4xx Client Error
    /** Indicates that the server cannot or will not process the request due to something that is perceived to be a client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing). */
    BAD_REQUEST(400),
    /** Indicates that the request requires user authentication. */
    UNAUTHORIZED(401),
    /** Indicates that the client does not have access rights to the content; i.e., it is unauthorized, so the server is refusing to give the requested resource. Unlike 401, the client's identity is known to the server. */
    FORBIDDEN(403),
    /** Indicates that the server can't find the requested resource. */
    NOT_FOUND(404),
    /** Indicates a request conflict with the current state of the target resource. This code is used in situations where the user might be able to resolve the conflict and resubmit the request. */
    CONFLICT(409),
    /** Indicates that the client has provided conditions in its request headers that the server could not meet. */
    PRECONDITION_FAILED(412),

    // 5xx Server Error
    /** Indicates that the server encountered an unexpected condition that prevented it from fulfilling the request. */
    INTERNAL_SERVER_ERROR(500),
    /** Indicates that the server does not support the functionality required to fulfill the request. */
    NOT_IMPLEMENTED(501),
    /** Indicates that the server is currently unable to handle the request due to a temporary overload or scheduled maintenance, which will likely be alleviated after some delay. */
    SERVICE_UNAVAILABLE(503);

    /**
     * The integer value of the status code.
     */
    public final int code;

    StatusCode(int code) {
        this.code = code;
    }

    /**
     * Converts an integer code to its corresponding StatusCode enum constant.
     *
     * @param code The integer code.
     * @return The matching StatusCode.
     * @throws IllegalArgumentException if the code is unknown.
     */
    public static StatusCode fromCode(int code) {
        for (StatusCode status : values()) { // Changed 'type' to 'status' for clarity
            if (status.code == code)
                return status;
        }
        throw new IllegalArgumentException("Unknown StatusCode code: " + code); // Changed MessageType to StatusCode
    }
}