package JSocket2.Protocol.Transfer.Download;

/**
 * Represents the metadata for a request to download a specific chunk of a file.
 * This object is typically serialized and sent to the server to initiate the download of a part of a file.
 */
public class DownloadChunkRequestMetadata {
    private String fileId;
    private int startChunkIndex;
    private long StartOffset;

    /**
     * Constructs metadata for a download chunk request.
     *
     * @param fileId          The unique identifier of the file.
     * @param startChunkIndex The index of the first chunk to download.
     * @param StartOffset     The byte offset from which to start reading the file.
     */
    public DownloadChunkRequestMetadata(String fileId, int startChunkIndex, long StartOffset){
        this.fileId = fileId;
        this.startChunkIndex = startChunkIndex;
        this.StartOffset = StartOffset;
    }

    /**
     * Constructs metadata for a download chunk request starting from the beginning of the file.
     *
     * @param fileId The unique identifier of the file.
     */
    public DownloadChunkRequestMetadata(String fileId){
        this.fileId = fileId;
        this.startChunkIndex = 0;
        this.StartOffset = 0;
    }

    /**
     * Gets the starting byte offset for the download.
     * @return The start offset.
     */
    public long getStartOffset() {
        return StartOffset;
    }

    /**
     * Sets the starting byte offset for the download.
     * @param startOffset The start offset.
     */
    public void setStartOffset(int startOffset) {
        StartOffset = startOffset;
    }

    /**
     * Gets the index of the chunk to start downloading from.
     * @return The start chunk index.
     */
    public int getStartChunkIndex() {
        return startChunkIndex;
    }

    /**
     * Sets the index of the chunk to start downloading from.
     * @param startChunkIndex The start chunk index.
     */
    public void setStartChunkIndex(int startChunkIndex) {
        this.startChunkIndex = startChunkIndex;
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