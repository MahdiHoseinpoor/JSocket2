package JSocket2.Core;

import JSocket2.Cryptography.EncryptionUtil;
import javax.crypto.SecretKey;

/**
 * Represents an abstract session, encapsulating a shared secret AES key for encrypted communication.
 * This class serves as a base for specific session implementations.
 */
public abstract class Session {
    /**
     * The AES secret key used for encrypting and decrypting session data.
     */
    protected SecretKey aesKey;

    /**
     * Constructs a new Session and generates a new AES secret key.
     */
    public Session(){
        aesKey = EncryptionUtil.generateAESsecretKey();
    }

    /**
     * Constructs a new Session using a pre-existing AES secret key.
     *
     * @param aesKey The {@link SecretKey} to be used for this session.
     */
    public Session(SecretKey aesKey){
        this.aesKey =aesKey;
    }

    /**
     * Retrieves the AES secret key associated with this session.
     *
     * @return The {@link SecretKey} for this session.
     */
    public SecretKey getAESKey() {
        return aesKey;
    }
}