package JSocket2.Core.Client;

import JSocket2.Core.Session;

import javax.crypto.SecretKey;
import java.security.PublicKey;

/**
 * Represents the client-side session state, including cryptographic keys and
 * authorization status. It extends the base {@link Session} with client-specific details.
 */
public class ClientSession extends Session {
    private PublicKey serverPublicKey;
    private boolean isAuthorized = false;

    /**
     * Constructs an empty ClientSession.
     */
    public ClientSession(){

    }

    /**
     * Constructs a ClientSession with the server's public key and the shared AES key.
     *
     * @param serverPublicKey The public key received from the server.
     * @param aesKey          The symmetric AES key for encrypted communication.
     */
    public ClientSession(PublicKey serverPublicKey, SecretKey aesKey) {
        this.serverPublicKey = serverPublicKey;
        super.aesKey = aesKey;
    }

    /**
     * Sets the server's public key.
     *
     * @param serverPublicKey The public key from the server.
     */
    public void setServerPublicKey(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    /**
     * Gets the server's public key.
     *
     * @return The server's {@link PublicKey}.
     */
    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    /**
     * Checks if the client session is authorized.
     *
     * @return {@code true} if authorized, otherwise {@code false}.
     */
    public boolean isAuthorized() {
        return isAuthorized;
    }

    /**
     * Sets the authorization status of the session.
     *
     * @param authorized The new authorization status.
     */
    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }
}