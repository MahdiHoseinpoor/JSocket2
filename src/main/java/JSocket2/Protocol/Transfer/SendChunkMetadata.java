package JSocket2.Protocol.Transfer;

/**
 * Represents the metadata associated with a single chunk of a file being transferred.
 * This information is sent along with the chunk data.
 */
public class SendChunkMetadata {
    /** The unique identifier of the file this chunk belongs to. */
    public String fileId;
    /** The zero-based index of this chunk. */
    public int chunkIndex;
    /** The total number of chunks that make up the entire file. */
    public int totalChunks;
    /** The byte offset of this chunk within the file. */
    public long offset;
}