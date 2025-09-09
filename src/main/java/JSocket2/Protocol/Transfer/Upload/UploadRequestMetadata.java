package JSocket2.Protocol.Transfer.Upload;

/**
 * Represents the metadata for initiating a new file upload.
 * This information is sent from the client to the server to register a new file for uploading.
 */
public class UploadRequestMetadata {
    private String userId;
    private String fileName;
    private String fileExtension;
    private long fileLength;

    /**
     * Constructs a new UploadRequestMetadata.
     *
     * @param userId        The ID of the user initiating the upload.
     * @param fileName      The name of the file without its extension.
     * @param fileExtension The file's extension.
     * @param file_length   The total size of the file in bytes.
     */
    public UploadRequestMetadata(String userId, String fileName, String fileExtension, long file_length) {
        this.userId = userId;
        this.fileName = fileName;
        this.fileLength = file_length;
        this.fileExtension = fileExtension;
    }

    /**
     * Gets the file's extension.
     * @return The file extension.
     */
    public String getFileExtension(){
        return fileExtension;
    }

    /**
     * Sets the file's extension.
     * @param fileExtension The new file extension.
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Gets the ID of the user.
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user.
     * @param user_id The new user ID.
     */
    public void setUserId(String user_id) {
        this.userId = user_id;
    }

    /**
     * Gets the name of the file.
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file.
     * @param file_name The new file name.
     */
    public void setFileName(String file_name) {
        this.fileName = file_name;
    }

    /**
     * Gets the total length of the file in bytes.
     * @return The file length.
     */
    public long getFileLength() {
        return fileLength;
    }

    /**
     * Sets the total length of the file in bytes.
     * @param file_length The new file length.
     */
    public void setFileLength(long file_length) {
        this.fileLength = file_length;
    }
}