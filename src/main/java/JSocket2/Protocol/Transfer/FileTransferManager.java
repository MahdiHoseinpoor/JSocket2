package JSocket2.Protocol.Transfer;

import JSocket2.Protocol.StatusCode;
import JSocket2.Protocol.Message;
import JSocket2.Protocol.MessageHandler;
import JSocket2.Protocol.MessageHeader;
import JSocket2.Protocol.Rpc.RpcResponseMetadata;
import com.google.gson.Gson;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * An abstract base class for managing file transfers (both uploads and downloads).
 * It provides common functionality for handling file chunks, managing transfer states,
 * and interacting with progress listeners.
 */
public abstract class FileTransferManager {

    protected static final Gson gson = new Gson();
    protected final Map<UUID, CompletableFuture<Message>> pendingRequests;
    protected final MessageHandler handler;

    protected final Map<String, IProgressListener> progressListeners = new ConcurrentHashMap<>();
    protected final Map<String, BooleanSupplier> continueSuppliers = new ConcurrentHashMap<>();
    protected final Map<String, TransferFiles> activeTransfers = new ConcurrentHashMap<>();

    /**
     * Registers listeners for a specific file transfer.
     *
     * @param fileId         The ID of the file transfer to monitor.
     * @param listener       A listener to receive progress updates. Can be null.
     * @param shouldContinue A supplier that returns false to pause or cancel the transfer. Can be null.
     */
    public void registerTransferListener(String fileId,
                                         IProgressListener listener,
                                         BooleanSupplier shouldContinue) {
        if (listener != null) {
            progressListeners.put(fileId, listener);
        }
        if (shouldContinue != null) {
            continueSuppliers.put(fileId, shouldContinue);
        }
    }

    /**
     * Removes all listeners associated with a file transfer.
     *
     * @param fileId The ID of the file transfer.
     */
    protected void unregisterTransfer(String fileId) {
        progressListeners.remove(fileId);
        continueSuppliers.remove(fileId);
    }

    /**
     * Constructs a FileTransferManager.
     *
     * @param handler         The message handler for network communication.
     * @param pendingRequests A map of pending requests for response correlation.
     */
    public FileTransferManager(MessageHandler handler,Map<UUID, CompletableFuture<Message>> pendingRequests ) {
        this.handler = handler;
        this.pendingRequests = pendingRequests;
    }

    /**
     * Gets the map of currently active transfers.
     * @return A map of file IDs to {@link TransferFiles} objects.
     */
    public Map<String, TransferFiles> getActiveTransfers(){
        return activeTransfers;
    }

    /**
     * Core logic for sending a file by breaking it into chunks.
     *
     * @param requestId   The UUID for the transfer operation.
     * @param fileId      The unique ID for the file.
     * @param file        The file to be sent.
     * @param startIndex  The starting chunk index (for resuming).
     * @param startOffset The starting byte offset (for resuming).
     * @param chunkSize   The size of each chunk.
     * @param fileSize    The total size of the file.
     * @throws IOException If an I/O error occurs.
     */
    protected void sendFileInternal(UUID requestId, String fileId, File file, int startIndex, long startOffset, int chunkSize, long fileSize) throws IOException {
        System.out.println("Sending file started. file ID: " + fileId);
        try (InputStream inStream = new BufferedInputStream(new FileInputStream(file))) {
            sendChunks(requestId, inStream, fileId, fileSize, chunkSize, startIndex, startOffset);
        }
    }

    /**
     * Deactivates all active transfers, saving their state for potential resuming.
     *
     * @throws IOException If an error occurs while closing transfer files.
     */
    public void deactivateTransfers() throws IOException {
        for(var fileId : activeTransfers.keySet()){
            TransferFiles transferFiles = activeTransfers.remove(fileId);
            transferFiles.closeAndCleanup();
        }
    }

    /**
     * Creates a new transfer state using the default temporary directory.
     *
     * @param fileId           The file's unique ID.
     * @param fileName         The name of the file.
     * @param fileExtension    The extension of the file.
     * @param destinationPath  The final destination path.
     * @param totalChunksCount The total number of chunks.
     * @param fileSize         The total size of the file.
     * @throws IOException If an error occurs creating the transfer files.
     */
    protected void createTransfer(String fileId,String fileName,String fileExtension,String destinationPath,int totalChunksCount,long fileSize) throws IOException {
        activeTransfers.put(fileId, TransferFiles.CreateDefault(fileId,fileName,fileExtension,destinationPath,totalChunksCount,fileSize));
    }

    /**
     * Creates a new transfer state using a specified temporary directory.
     *
     * @param fileId           The file's unique ID.
     * @param fileName         The name of the file.
     * @param fileExtension    The extension of the file.
     * @param destinationPath  The final destination path.
     * @param tempPath         The path for temporary files.
     * @param totalChunksCount The total number of chunks.
     * @param fileSize         The total size of the file.
     * @throws IOException If an error occurs creating the transfer files.
     */
    protected void createTransfer(String fileId,String fileName,String fileExtension,String destinationPath,String tempPath,int totalChunksCount,long fileSize) throws IOException {
        activeTransfers.put(fileId, TransferFiles.Create(fileId,fileName,fileExtension,destinationPath,tempPath,totalChunksCount,fileSize));
    }

    /**
     * Checks if a transfer can be resumed by looking for existing temp/info files.
     *
     * @param fileId The ID of the file.
     * @return True if the transfer can be resumed, false otherwise.
     */
    protected boolean CanLoadTransfer(String fileId){
        return TransferFiles.canLoad(fileId);
    }

    /**
     * Loads a paused transfer from disk.
     *
     * @param fileId The ID of the file to load.
     * @return The loaded {@link TransferFiles} object.
     * @throws IOException If an error occurs while loading.
     */
    protected TransferFiles LoadTransfer(String fileId) throws IOException {
        var transferFiles = TransferFiles.Load(fileId);
        activeTransfers.put(fileId,transferFiles );
        return transferFiles;
    }

    /**
     * Writes a received chunk of data to the temporary file on disk.
     *
     * @param fileId     The ID of the file.
     * @param offset     The offset at which to write the data.
     * @param chunkIndex The index of the chunk being written.
     * @param data       The chunk data.
     * @throws IOException If an I/O error occurs.
     */
    protected void writeChunk(String fileId, long offset,int chunkIndex, byte[] data) throws IOException {
        if (!activeTransfers.containsKey(fileId)) {
            throw new IllegalStateException("Transfer with fileId \"" + fileId + "\" is not initialized.");
        }
        activeTransfers.get(fileId).writeChunk(offset, data, chunkIndex);
    }

    /**
     * Closes an active transfer, marking it as paused.
     *
     * @param fileId The ID of the file transfer.
     * @throws IOException If an I/O error occurs.
     */
    protected void closeTransfer(String fileId) throws IOException {
        if (!activeTransfers.containsKey(fileId)) {
            throw new IllegalStateException("Transfer with fileId \"" + fileId + "\" is not active.");
        }
        TransferFiles transferFiles = activeTransfers.remove(fileId);
        transferFiles.closeAndCleanup();
    }

    /**
     * Finalizes a transfer, moving the temporary file to its final destination.
     *
     * @param fileId The ID of the completed file transfer.
     * @throws IOException If an I/O error occurs.
     */
    protected void finishTransfer(String fileId) throws IOException {
        if (!activeTransfers.containsKey(fileId)) {
            throw new IllegalStateException("Transfer with fileId \"" + fileId + "\" is not active.");
        }
        TransferFiles transferFiles = activeTransfers.remove(fileId);
        transferFiles.finalizeTransfer();
    }

    /**
     * Processes an incoming message containing a file chunk.
     *
     * @param message The message with chunk data.
     * @throws IOException If an I/O error occurs.
     */
    public void ProcessSendChunk(Message message) throws IOException {
        var metadata = gson.fromJson(
                new String(message.getMetadata(), StandardCharsets.UTF_8),
                SendChunkMetadata.class
        );
        receiveChunk(message.header.uuid,metadata,message.getPayload());
    }

    /**
     * Helper method to process a received chunk and send an acknowledgment.
     *
     * @param requestId The UUID of the request.
     * @param metadata  The metadata of the received chunk.
     * @param chunkData The data of the chunk.
     * @throws IOException If an I/O error occurs.
     */
    protected void receiveChunk(UUID requestId, SendChunkMetadata metadata,byte[] chunkData) throws IOException {
        receiveChunk(requestId,metadata,chunkData,true);
    }

    /**
     * Core logic for processing a received chunk.
     *
     * @param requestId The UUID of the request.
     * @param metadata  The metadata of the chunk.
     * @param chunkData The data of the chunk.
     * @param sendAck   Whether to send an acknowledgment back.
     * @throws IOException If an I/O error occurs.
     */
    protected void receiveChunk(UUID requestId, SendChunkMetadata metadata,byte[] chunkData,boolean sendAck) throws IOException {
        writeChunk(metadata.fileId, metadata.offset,metadata.chunkIndex, chunkData);
        if(metadata.chunkIndex == metadata.totalChunks-1){
            finishTransfer(metadata.fileId);
        }
        if(sendAck) {
            var responseMetadata = gson.toJson(new RpcResponseMetadata(StatusCode.OK.code, ""));
            var responsePayload = ByteBuffer.allocate(4).putInt(metadata.chunkIndex).array();
            var ackMessage = new Message(
                    MessageHeader.BuildUploadAckHeader(
                            requestId, responseMetadata.getBytes(StandardCharsets.UTF_8).length
                    ),
                    responseMetadata.getBytes(StandardCharsets.UTF_8),
                    responsePayload
            );
            handler.write(ackMessage);
        }
    }

    /**
     * Sends a single chunk of a file and waits for acknowledgment.
     *
     * @param requestId   The UUID for the request.
     * @param chunkData   The data to send.
     * @param fileId      The file ID.
     * @param chunkIndex  The index of this chunk.
     * @param totalChunks The total number of chunks.
     * @param offset      The byte offset of this chunk.
     * @throws IOException If a communication error occurs.
     */
    protected void sendChunk(UUID requestId, byte[] chunkData, String fileId, int chunkIndex, int totalChunks, long offset) throws IOException {
        sendChunk(requestId,chunkData,fileId,chunkIndex,totalChunks,offset,true);
    }

    /**
     * Sends a single chunk of a file with an option to skip waiting for acknowledgment.
     *
     * @param requestId   The UUID for the request.
     * @param chunkData   The data to send.
     * @param fileId      The file ID.
     * @param chunkIndex  The index of this chunk.
     * @param totalChunks The total number of chunks.
     * @param offset      The byte offset of this chunk.
     * @param hasAck      If true, waits for an acknowledgment.
     * @throws IOException If a communication error occurs.
     */
    protected void sendChunk(UUID requestId, byte[] chunkData, String fileId, int chunkIndex, int totalChunks, long offset,boolean hasAck) throws IOException {
        SendChunkMetadata chunkMetadata = new SendChunkMetadata();
        chunkMetadata.fileId = fileId;
        chunkMetadata.chunkIndex = chunkIndex;
        chunkMetadata.totalChunks = totalChunks;
        chunkMetadata.offset = offset;

        byte[] metaBytes = gson.toJson(chunkMetadata).getBytes(StandardCharsets.UTF_8);

        Message chunkMsg = new Message(
                MessageHeader.BuildSendChunkHeader(requestId, true, metaBytes.length, chunkData.length),
                metaBytes,
                chunkData
        );

        if(hasAck){
            CompletableFuture<Message> chunkFuture = new CompletableFuture<>();
            pendingRequests.put(requestId, chunkFuture);
            handler.write(chunkMsg);
            try {
                chunkFuture.join();
            } finally {
                pendingRequests.remove(requestId);
            }
        } else {
            handler.write(chunkMsg);
        }
    }

    /**
     * Reads a file from an input stream and sends it in chunks.
     *
     * @param requestId   The UUID for the transfer.
     * @param inStream    The input stream of the file.
     * @param fileId      The file's unique ID.
     * @param fileSize    The total size of the file.
     * @param chunkSize   The size of each chunk.
     * @param startIndex  The starting chunk index for resuming.
     * @param startOffset The starting byte offset for resuming.
     * @throws IOException If an I/O error occurs.
     */
    protected void sendChunks(UUID requestId, InputStream inStream, String fileId, long fileSize, int chunkSize, int startIndex, long startOffset) throws IOException {
        if (startOffset > 0) {
            if (inStream.skip(startOffset) < startOffset) {
                throw new IOException("Unable to skip to resume offset: " + startOffset);
            }
        }

        byte[] buffer = new byte[chunkSize];
        int bytesRead;
        int index = startIndex;
        long offset = startOffset;
        long uploaded = startOffset;
        int totalChunks = (int) ((fileSize + chunkSize - 1) / chunkSize);

        while ((bytesRead = inStream.read(buffer)) != -1) {
            BooleanSupplier continueSuppliersOrDefault = continueSuppliers.getOrDefault(fileId, () -> true);
            if (!continueSuppliersOrDefault.getAsBoolean()) {
                throw new IOException("Transfer cancelled for " + fileId);
            }
            byte[] chunkData = new byte[bytesRead];
            System.arraycopy(buffer, 0, chunkData, 0, bytesRead);
            sendChunk(requestId, chunkData, fileId, index, totalChunks, offset);
            uploaded += bytesRead;
            offset += bytesRead;
            index++;
            IProgressListener progressListener = progressListeners.get(fileId);
            if (progressListener != null) {
                progressListener.onProgress(uploaded, fileSize);
            }
        }
    }

    /**
     * Sends a single, specific chunk of a file. Used for downloads where the client requests chunks.
     *
     * @param requestId    The UUID for the request.
     * @param inStream     The input stream of the file.
     * @param fileId       The file's unique ID.
     * @param fileSize     The total size of the file.
     * @param chunkSize    The size of each chunk.
     * @param chunkIndex   The index of the chunk to send.
     * @throws IOException If an I/O error occurs.
     */
    protected void sendSpecificChunk(UUID requestId, InputStream inStream, String fileId, long fileSize, int chunkSize, int chunkIndex) throws IOException {
        long offset = (long) chunkSize * chunkIndex;
        if (offset >= fileSize) {
            throw new IllegalArgumentException("Chunk index out of file size range.");
        }
        if (inStream.skip(offset) < offset) {
            throw new IOException("Unable to skip to chunk offset: " + offset);
        }

        byte[] buffer = new byte[chunkSize];
        int bytesRead = inStream.read(buffer);

        if (bytesRead <= 0) {
            throw new IOException("Failed to read chunk at offset: " + offset);
        }

        byte[] chunkData = new byte[bytesRead];
        System.arraycopy(buffer, 0, chunkData, 0, bytesRead);

        int totalChunks = (int) ((fileSize + chunkSize - 1) / chunkSize);
        sendChunk(requestId, chunkData, fileId, chunkIndex, totalChunks, offset,false);
    }
}