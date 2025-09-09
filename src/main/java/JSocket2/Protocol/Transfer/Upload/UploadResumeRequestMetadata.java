package JSocket2.Protocol.Transfer.Upload;

/**
 * Represents the metadata for a request to resume a previously paused file upload.
 * It contains the ID of the file to be resumed.
 */
public class UploadResumeRequestMetadata {
    /**
     * The unique identifier of the file whose upload is to be resumed.
     */
    public final String FileId;

    /**
     * Constructs a new UploadResumeRequestMetadata.
     *
     * @param FileId The unique ID of the file.
     */
    public UploadResumeRequestMetadata(String FileId){
        this.FileId = FileId;
    }
}