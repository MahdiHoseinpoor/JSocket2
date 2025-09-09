package JSocket2.Protocol.Transfer.Download;

/**
 * Represents the metadata for a request to download a file.
 * This is the initial request sent by the client to get information about a file before starting the download.
 */
public class DownloadRequestMetadata {
    private String fileId;

    /**
     * Constructs a new DownloadRequestMetadata.
     *
     * @param fileId The unique identifier of the file to be downloaded.
     */
    public DownloadRequestMetadata( String fileId){
        this.fileId = fileId;
    }

    /**
     * Gets the unique identifier of the file.
     * @return The file ID.
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the unique identifier of the file.
     * @param fileId The file ID.
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}