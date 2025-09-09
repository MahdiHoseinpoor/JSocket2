package JSocket2.Protocol;

/**
 * Defines status codes used in responses, similar to HTTP status codes.
 */
public enum StatusCode {
    /** Indicates that the request has succeeded. */
    OK(200),
    /** Indicates that the server cannot or will not process the request due to something that is perceived to be a client error. */
    BAD_REQUEST(400),
    /** Indicates that the client does not have access rights to the content. */
    FORBIDDEN(403),
    /** Indicates that the server can't find the requested resource. */
    NOT_FOUND(404);

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
        for (StatusCode type : values()) {
            if (type.code == code)
                return type;
        }
        throw new IllegalArgumentException("Unknown MessageType code: " + code);
    }
}