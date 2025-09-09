package JSocket2.Protocol.Transfer;

 /*
 Defines a listener for monitoring the progress of a file transfer.
 */
 public interface IProgressListener {
    /**
     * Called periodically to update the progress of a transfer.
     * @param transferred The number of bytes (or chunks) transferred so far
     * @param total The total number of bytes (or chunks) to be transferred.
     */
    void onProgress(long transferred, long total);
    }