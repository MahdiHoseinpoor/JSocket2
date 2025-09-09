package JSocket2.Protocol.Authentication;

/**
 * Defines the contract for an authentication service.
 * Implementations of this interface are responsible for validating credentials and providing user identity information.
 */
public interface IAuthService {
    /**
     * Attempts to log in a user with the provided access key.
     *
     * @param key The access key used for authentication.
     * @return A {@link UserIdentity} object if the login is successful.
     * @throws AuthException if the login fails.
     */
    UserIdentity Login(String key);

    /**
     * Checks if a given access key is valid.
     *
     * @param key The access key to validate.
     * @return {@code true} if the key is valid, {@code false} otherwise.
     */
    boolean IsKeyValid(String key);
}