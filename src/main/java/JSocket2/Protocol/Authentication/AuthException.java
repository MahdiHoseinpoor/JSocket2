package JSocket2.Protocol.Authentication;

/**
 * A base exception for errors that occur during the authentication process.
 */
public class AuthException extends RuntimeException {
    /**
     * Constructs a new {@code AuthException} with the specified detail message.
     *
     * @param message The detail message.
     */
    public AuthException(String message) {
        super(message);
    }
}