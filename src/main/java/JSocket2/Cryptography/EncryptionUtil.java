package JSocket2.Cryptography;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * A utility class providing cryptographic functions for AES and RSA encryption and decryption.
 */
public class EncryptionUtil {

    /**
     * The key size in bits for AES encryption.
     */
    private static final int AES_KEY_SIZE = 128;

    /**
     * The key size in bits for RSA key pair generation.
     */
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * The size in bytes for the Initialization Vector (IV) used in AES/CBC mode.
     */
    private static final int IV_SIZE = 16;

    /**
     * Decodes a byte array into an RSA {@link PublicKey}.
     *
     * @param rsaPublicKeyBytes The byte array representing the X.509 encoded public key.
     * @return The decoded {@link PublicKey}.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not available.
     * @throws InvalidKeySpecException If the provided key specification is invalid.
     */
    public static PublicKey decodeRsaPublicKey(byte[] rsaPublicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(rsaPublicKeyBytes);
        return keyFactory.generatePublic(spec);
    }

    /**
     * Decodes a byte array into an AES {@link SecretKey}.
     *
     * @param aesKeyBytes The raw byte array of the AES key.
     * @return The decoded {@link SecretKey}.
     */
    public static SecretKey decodeAesKey(byte[] aesKeyBytes){
        return new SecretKeySpec(aesKeyBytes, 0, aesKeyBytes.length, "AES");
    }

    /**
     * Generates a new RSA {@link KeyPair}.
     *
     * @return The generated {@link KeyPair} containing a public and a private key.
     * @throws RuntimeException if an error occurs during key pair generation.
     */
    public static KeyPair generateRSAkeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            System.err.println("ERROR: while generating RSA KeyPair:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts data using the provided RSA public key.
     *
     * @param jsonData The data to be encrypted.
     * @param publicKey The {@link PublicKey} to use for encryption.
     * @return The encrypted data as a byte array.
     * @throws RuntimeException if an error occurs during encryption.
     */
    public static byte[] encryptDataRSA(byte[] jsonData, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(jsonData);
        } catch (Exception e) {
            System.err.println("Error: while encrypting data with RSA:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts data using the provided RSA private key.
     *
     * @param encryptedBytes The encrypted data.
     * @param privateKey The {@link PrivateKey} to use for decryption.
     * @return The decrypted data as a byte array.
     * @throws RuntimeException if an error occurs during decryption.
     */
    public static byte[] decryptDataRSA(byte[] encryptedBytes, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Error: while decrypting data with RSA:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts data using the provided AES secret key and Initialization Vector (IV).
     *
     * @param rawBytes The data to be encrypted.
     * @param aesKey The {@link SecretKey} to use for encryption.
     * @param ivBytes The Initialization Vector.
     * @return The encrypted data as a byte array.
     * @throws RuntimeException if an error occurs during encryption.
     */
    public static byte[] encryptDataAES(byte[] rawBytes, SecretKey aesKey, byte[] ivBytes) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            return cipher.doFinal(rawBytes);
        } catch (Exception e) {
            System.err.println("Error: while encrypting data with AES:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypts data using the provided AES secret key and Initialization Vector (IV).
     *
     * @param cipherBytes The encrypted data.
     * @param aesKey The {@link SecretKey} to use for decryption.
     * @param ivBytes The Initialization Vector.
     * @return The decrypted data as a byte array.
     * @throws RuntimeException if an error occurs during decryption.
     */
    public static byte[] decryptDataAES(byte[] cipherBytes, SecretKey aesKey, byte[] ivBytes) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            System.err.println("Error: while decrypting data with AES:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random Initialization Vector (IV).
     *
     * @return A new byte array containing the IV.
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Generates a new AES secret key.
     *
     * @return The generated {@link SecretKey}.
     * @throws RuntimeException if an error occurs during key generation.
     */
    public static SecretKey generateAESsecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            System.err.println("Error: while generating AES key:");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}