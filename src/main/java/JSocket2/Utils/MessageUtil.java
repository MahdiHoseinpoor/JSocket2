package JSocket2.Utils;

import JSocket2.Cryptography.EncryptionUtil;
import JSocket2.Protocol.Message;

import javax.crypto.SecretKey;

/**
 * Provides utility methods for encrypting and decrypting the contents of a {@link Message}.
 * This class centralizes the logic for securing message metadata and payloads.
 */
public class MessageUtil {
    /**
     * Encrypts the metadata and payload of a message using AES.
     * A new Initialization Vector (IV) is generated for each encryption and stored in the message.
     *
     * @param message The message to encrypt. Its metadata and payload will be replaced with their encrypted versions.
     * @param aesKey  The {@link SecretKey} to use for AES encryption.
     */
    public static void EncryptMessage(Message message, SecretKey aesKey){
        byte[] IvBytes = EncryptionUtil.generateIV();
        message.setIvBytes(IvBytes);
        if (message.header.metadata_length > 0) {
            byte[] encrypted_metadata = EncryptionUtil.encryptDataAES(message.getMetadata(),aesKey,IvBytes);
            message.setMetadata(encrypted_metadata);
        }
        if (message.header.payload_length > 0) {
            byte[] encrypted_payload = EncryptionUtil.encryptDataAES(message.getPayload(),aesKey,IvBytes);
            message.setPayload(encrypted_payload);
        }
    }

    /**
     * Decrypts the metadata and payload of a message using AES.
     * It uses the Initialization Vector (IV) stored within the message itself.
     *
     * @param message The message to decrypt. Its metadata and payload will be replaced with their decrypted versions.
     * @param aesKey  The {@link SecretKey} that was used for the original encryption.
     */
    public static void DecryptMessage(Message message, SecretKey aesKey){
        byte[] IvBytes = message.getIvBytes();
        if (message.header.metadata_length > 0) {
            byte[] decrypted_metadata = EncryptionUtil.decryptDataAES(message.getMetadata(),aesKey,IvBytes);
            message.setMetadata(decrypted_metadata);
        }
        if (message.header.payload_length > 0) {
            byte[] decrypted_payload = EncryptionUtil.decryptDataAES(message.getPayload(),aesKey,IvBytes);
            message.setPayload(decrypted_payload);
        }
    }
}