package JSocket2.Cryptography;

import JSocket2.DI.Inject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Manages an RSA key pair, providing access to the public and private keys.
 * This class is designed to be managed by a Dependency Injection framework.
 */
public class RsaKeyManager {

    /**
     * The RSA key pair, consisting of a public and a private key.
     */
    private final KeyPair rsaKeyPair;

    /**
     * Constructs a new RsaKeyManager and generates a new RSA key pair.
     * The {@code @Inject} annotation suggests that this constructor will be called by a dependency injection framework.
     */
    @Inject
    public RsaKeyManager() {
        rsaKeyPair = EncryptionUtil.generateRSAkeyPair();
    }

    /**
     * Retrieves the RSA public key.
     *
     * @return The {@link PublicKey}.
     */
    public PublicKey getRSAPublicKey() {
        return rsaKeyPair.getPublic();
    }

    /**
     * Retrieves the RSA private key.
     *
     * @return The {@link PrivateKey}.
     */
    public PrivateKey getRSAPrivateKey() {
        return rsaKeyPair.getPrivate();
    }

}