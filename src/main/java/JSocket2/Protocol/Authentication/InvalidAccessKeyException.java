package JSocket2.Protocol.Authentication;

/**
 * Thrown to indicate that an access key provided for authentication is invalid.
 */
public class InvalidAccessKeyException extends AuthException {
    /**
     * Constructs a new {@code InvalidAccessKeyException} with a specific detail message.
     *
     * @param message The detail message.
     */
    public InvalidAccessKeyException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code InvalidAccessKeyException} with a default message.
     */
    public InvalidAccessKeyException() {
        super("Access Key is not valid");
    }
}