package JSocket2.Protocol.Transfer.Upload;

/**
 * A data model containing the necessary information to resume a file upload.
 * This is sent from the server to the client in response to an {@link UploadResumeRequestMetadata}.
 */
public class UploadResumeResultModel {
    /** The unique identifier of the file. */
    public final String FileId;
    /** The index of the chunk from which to resume the upload. */
    public final int StartIndex;
    /** The byte offset in the file from which to resume. */
    public final long StartOffset;
    /** The size of each chunk in bytes. */
    public final int ChunkSize;
    /** The total size of the file. */
    public final long FileSize;

    /**
     * Constructs a new UploadResumeResultModel.
     *
     * @param FileId      The file's unique ID.
     * @param StartIndex  The starting chunk index.
     * @param StartOffset The starting byte offset.
     * @param ChunkSize   The chunk size.
     * @param FileSize    The total file size.
     */
    public UploadResumeResultModel(String FileId, int StartIndex, long StartOffset, int ChunkSize, long FileSize){
        this.FileId = FileId;
        this.StartIndex = StartIndex;
        this.StartOffset = StartOffset;
        this.ChunkSize = ChunkSize;
        this.FileSize = FileSize;
    }
}