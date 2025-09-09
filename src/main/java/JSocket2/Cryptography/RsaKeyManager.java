package JSocket2.Cryptography;

import JSocket2.DI.Inject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaKeyManager {

    private final KeyPair rsaKeyPair;
    @Inject
    public RsaKeyManager() {
        rsaKeyPair = EncryptionUtil.generateRSAkeyPair();
    }
    public PublicKey getRSAPublicKey() {
        return rsaKeyPair.getPublic();
    }
    public PrivateKey getRSAPrivateKey() {
        return rsaKeyPair.getPrivate();
    }

}
