package JSocket2.Protocol.Transfer;

/**
 * A data model representing basic information about a file being transferred.
 * It includes the file's unique ID and the size of the chunks for transfer.
 */
public class FileInfoModel {
    /**
     * The unique identifier assigned to the file for the transfer process.
     */
    public String FileId;
    /**
     * The size of each data chunk in bytes for the file transfer. Defaults to 65536.
     */
    public int ChunkSize = 65536;

    /**
     * Constructs a FileInfoModel with a specified file ID and chunk size.
     *
     * @param FileId    The unique identifier for the file.
     * @param ChunkSize The size of each transfer chunk in bytes.
     */
    public FileInfoModel(String FileId,int ChunkSize){
        this.FileId = FileId;
        this.ChunkSize = ChunkSize;
    }

    /**
     * Constructs a FileInfoModel with a specified file ID and a default chunk size.
     *
     * @param FileId The unique identifier for the file.
     */
    public FileInfoModel(String FileId){
        this.FileId = FileId;
    }
}