package JSocket2.Protocol.Transfer;

/**
 * A data model that holds all the state and metadata for a single file transfer.
 * This object is typically serialized to a .info JSON file to allow for resuming transfers.
 */
public class TransferInfo {
    private String fileId;
    private String fileExtension;
    private String fileName;
    private String destinationPath;
    private TransferState transferState;
    private long lastWrittenOffset;
    private int lastChunkIndex;
    private int totalChunksCount;
    private long fileSize;

    /**
     * Constructs a new TransferInfo object.
     *
     * @param fileId            The unique identifier for the transfer.
     * @param fileName          The name of the file.
     * @param fileExtension     The extension of the file.
     * @param destinationPath   The final destination directory.
     * @param lastWrittenOffset The last written byte offset in the temporary file.
     * @param lastChunkIndex    The index of the last successfully transferred chunk.
     * @param totalChunksCount  The total number of chunks for the file.
     * @param fileSize          The total size of the file in bytes.
     */
    public TransferInfo(String fileId,String fileName,String fileExtension,String destinationPath, long lastWrittenOffset, int lastChunkIndex,int totalChunksCount,long fileSize) {
        this.fileId = fileId;
        this.fileExtension = fileExtension;
        this.fileName = fileName;
        this.lastWrittenOffset = lastWrittenOffset;
        this.lastChunkIndex = lastChunkIndex;
        this.destinationPath = destinationPath;
        this.totalChunksCount = totalChunksCount;
        this.transferState = TransferState.InProcess;
        this.fileSize = fileSize;
    }

    /**
     * Gets the total size of the file.
     * @return The file size in bytes.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the total size of the file.
     * @param fileSize The file size in bytes.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the current state of the transfer (e.g., InProcess, Paused).
     * @return The {@link TransferState}.
     */
    public TransferState getTransferState() {
        return transferState;
    }

    /**
     * Gets the total number of chunks for the file.
     * @return The total chunk count.
     */
    public int getTotalChunksCount() {
        return totalChunksCount;
    }

    /**
     * Sets the total number of chunks for the file.
     * @param totalChunksCount The total chunk count.
     */
    public void setTotalChunksCount(int totalChunksCount) {
        this.totalChunksCount = totalChunksCount;
    }

    /**
     * Sets the current state of the transfer.
     * @param transferState The new {@link TransferState}.
     */
    public void setTransferState(TransferState transferState) {
        this.transferState = transferState;
    }

    /**
     * Gets the final destination path for the file.
     * @return The destination path.
     */
    public String getDestinationPath() {
        return destinationPath;
    }

    /**
     * Sets the final destination path for the file.
     * @param destinationPath The new destination path.
     */
    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    /**
     * Gets the unique identifier for this transfer.
     * @return The file ID.
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the unique identifier for this transfer.
     * @param fileId The new file ID.
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * Sets the file's extension.
     * @param fileExtension The new file extension.
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Sets the file's name.
     * @param fileName The new file name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file's extension.
     * @return The file extension.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Gets the file's name.
     * @return The file name.
     */
    public String getFileName(){
        return fileName;
    }

    /**
     * Gets the last written byte offset in the temporary file.
     * @return The last written offset.
     */
    public long getLastWrittenOffset() {
        return lastWrittenOffset;
    }

    /**
     * Sets the last written byte offset.
     * @param lastWrittenOffset The new offset.
     */
    public void setLastWrittenOffset(long lastWrittenOffset) {
        this.lastWrittenOffset = lastWrittenOffset;
    }

    /**
     * Gets the index of the last successfully transferred chunk.
     * @return The last chunk index.
     */
    public int getLastChunkIndex() {
        return lastChunkIndex;
    }

    /**
     * Sets the index of the last successfully transferred chunk.
     * @param lastChunkIndex The new chunk index.
     */
    public void setLastChunkIndex(int lastChunkIndex) {
        this.lastChunkIndex = lastChunkIndex;
    }
}