package JSocket2.Protocol.Authentication;

/**
 * A data model class that encapsulates authentication-related information,
 * such as access keys and an optional file hash for validation.
 */
public class AuthModel {
    private int accessKeyCount;
    private String[] accessKeys;
    private String fileHash;

    /**
     * Constructs an {@code AuthModel} with access keys and their count.
     *
     * @param accessKeys     An array of access keys.
     * @param accessKeyCount The number of access keys.
     */
    public AuthModel(String[] accessKeys, int accessKeyCount) {
        this.accessKeys = accessKeys;
        this.accessKeyCount = accessKeyCount;
    }

    /**
     * Constructs an {@code AuthModel} with access keys, their count, and a file hash.
     *
     * @param accessKeys     An array of access keys.
     * @param accessKeyCount The number of access keys.
     * @param fileHash       A hash value, typically for verifying the integrity of a related file.
     */
    public AuthModel(String[] accessKeys, int accessKeyCount, String fileHash) {
        this.accessKeys = accessKeys;
        this.accessKeyCount = accessKeyCount;
        this.fileHash = fileHash;
    }

    /**
     * Gets the file hash.
     *
     * @return The file hash string.
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * Sets the file hash.
     *
     * @param fileHash The file hash string.
     */
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    /**
     * Gets the array of access keys.
     *
     * @return The array of access keys.
     */
    public String[] getAccessKeys() {
        return accessKeys;
    }

    /**
     * Sets the array of access keys.
     *
     * @param accessKeys The array of access keys.
     */
    public void setAccessKeys(String[] accessKeys) {
        this.accessKeys = accessKeys;
    }

    /**
     * Gets the number of access keys.
     *
     * @return The count of access keys.
     */
    public int getAccessKeyCount() {
        return accessKeyCount;
    }

    /**
     * Sets the number of access keys.
     *
     * @param accessKeyCount The count of access keys.
     */
    public void setAccessKeyCount(int accessKeyCount) {
        this.accessKeyCount = accessKeyCount;
    }
}