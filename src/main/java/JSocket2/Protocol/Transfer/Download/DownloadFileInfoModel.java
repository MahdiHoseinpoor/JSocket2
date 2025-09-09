package JSocket2.Protocol.Transfer.Download;

/**
 * A data model containing information about a file that is available for download.
 * This information is typically sent from the server to the client in response to a download request.
 */
public class DownloadFileInfoModel {
    private String fileId;
    private String fileName;
    private String fileExtension;
    private long fileLength;

    /**
     * Constructs a new DownloadFileInfoModel.
     *
     * @param fileId        The unique identifier for the file.
     * @param fileName      The name of the file without its extension.
     * @param fileExtension The extension of the file.
     * @param fileLength    The total size of the file in bytes.
     */
    public DownloadFileInfoModel(String fileId,String fileName,String fileExtension,long fileLength){
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.fileLength = fileLength;
    }

    /**
     * Gets the unique identifier of the file.
     * @return The file ID.
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the file extension.
     * @param fileExtension The new file extension.
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Sets the unique identifier of the file.
     * @param fileId The new file ID.
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * Gets the file extension.
     * @return The file extension.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Sets the name of the file.
     * @param fileName The new file name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the total length of the file in bytes.
     * @return The file length.
     */
    public long getFileLength() {
        return fileLength;
    }

    /**
     * Gets the name of the file.
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the total length of the file in bytes.
     * @param fileLength The new file length.
     */
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}