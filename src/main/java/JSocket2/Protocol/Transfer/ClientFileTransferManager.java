package JSocket2.Protocol.Transfer;

import JSocket2.Protocol.Message;
import JSocket2.Protocol.MessageHandler;
import JSocket2.Protocol.MessageHeader;
import JSocket2.Protocol.Rpc.RpcResponseMetadata;
import JSocket2.Protocol.Transfer.Download.DownloadChunkRequestMetadata;
import JSocket2.Protocol.Transfer.Download.DownloadFileInfoModel;
import JSocket2.Protocol.Transfer.Download.DownloadRequestMetadata;
import JSocket2.Protocol.Transfer.Upload.UploadRequestMetadata;
import JSocket2.Protocol.Transfer.Upload.UploadResumeRequestMetadata;
import JSocket2.Protocol.Transfer.Upload.UploadResumeResultModel;
import JSocket2.Utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * Manages file transfers from the client's perspective.
 * This class handles initiating, starting, resuming, and monitoring uploads and downloads.
 */
public class ClientFileTransferManager extends FileTransferManager{
    private final String SAVE_PATH = "src/files/client";

    /**
     * Constructs a ClientFileTransferManager.
     *
     * @param handler         The message handler for communication with the server.
     * @param pendingRequests A map of pending requests awaiting server responses.
     */
    public ClientFileTransferManager(MessageHandler handler, Map<UUID, CompletableFuture<Message>> pendingRequests) {
        super(handler,pendingRequests);
    }

    /**
     * Initiates a file upload by sending metadata to the server and receiving a file ID.
     *
     * @param file The file to be uploaded.
     * @return A {@link FileInfoModel} containing the assigned file ID and chunk size.
     * @throws IOException If an I/O error occurs during the request.
     */
    public FileInfoModel initiateUpload(File file) throws IOException {
        String fileName = FileUtil.getFileNameWithoutExtension(file);
        String fileExtension = FileUtil.getFileExtension(file);
        long fileSize = file.length();

        UUID requestId = UUID.randomUUID();
        return sendUploadInitRequest(requestId, fileName, fileExtension, fileSize);
    }

    /**
     * Starts uploading a file from the beginning.
     *
     * @param info The file info model received from {@link #initiateUpload(File)}.
     * @param file The file to upload.
     * @throws IOException If an I/O error occurs during the upload.
     */
    public void StartUpload(FileInfoModel info,File file) throws IOException {
        long fileSize = file.length();
        UUID requestId = UUID.randomUUID();
        sendFileInternal(requestId,info.FileId,file,0,0,info.ChunkSize,fileSize);
    }

    /**
     * Resumes a previously paused or interrupted file upload.
     *
     * @param fileId The ID of the file to resume uploading.
     * @param file   The source file.
     * @throws IOException If an I/O error occurs during the upload.
     */
    public void ContinueUpload(String fileId, File file) throws IOException {
        UploadResumeResultModel resumeInfo = sendTransferResumeRequest(UUID.randomUUID(), fileId);
        sendFileInternal(
                UUID.randomUUID(),
                fileId,
                file,
                resumeInfo.StartIndex,
                resumeInfo.StartOffset,
                resumeInfo.ChunkSize,
                resumeInfo.FileSize
        );
    }

    /**
     * Sends a request to the server to get the status of a resumable upload.
     *
     * @param requestId The UUID for this request.
     * @param fileId    The ID of the file to resume.
     * @return An {@link UploadResumeResultModel} with the offset and index to continue from.
     * @throws IOException If a communication error occurs.
     */
    private UploadResumeResultModel sendTransferResumeRequest(UUID requestId, String fileId) throws IOException {
        UploadResumeRequestMetadata metadata = new UploadResumeRequestMetadata(fileId);
        byte[] metadataBytes = gson.toJson(metadata).getBytes(StandardCharsets.UTF_8);
        Message request = new Message(
                MessageHeader.BuildResumeUploadRequestAckHeader(requestId, true, metadataBytes.length, 0),
                metadataBytes,
                new byte[0]
        );

        CompletableFuture<Message> futureResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureResponse);
        handler.write(request);

        Message response;
        try {
            response = futureResponse.join();
        } finally {
            pendingRequests.remove(requestId);
        }

        return gson.fromJson(
                new String(response.getPayload(), StandardCharsets.UTF_8),
                UploadResumeResultModel.class
        );
    }

    /**
     * Sends the initial request to the server to start an upload process.
     *
     * @param requestId     The UUID for this request.
     * @param fileName      The name of the file.
     * @param fileExtension The extension of the file.
     * @param fileSize      The total size of the file.
     * @return A {@link FileInfoModel} with the server-assigned file ID.
     * @throws IOException If a communication error occurs.
     */
    private FileInfoModel sendUploadInitRequest(UUID requestId, String fileName,String fileExtension, long fileSize) throws IOException {
        UploadRequestMetadata metadata = new UploadRequestMetadata("1234", fileName,fileExtension, fileSize);
        byte[] metadataBytes = gson.toJson(metadata).getBytes(StandardCharsets.UTF_8);

        Message request = new Message(
                MessageHeader.BuildUploadRequestHeader(requestId, true, metadataBytes.length, 0),
                metadataBytes,
                new byte[0]
        );

        CompletableFuture<Message> futureResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureResponse);
        handler.write(request);

        Message response;
        try {
            response = futureResponse.join();
        } finally {
            pendingRequests.remove(requestId);
        }

        return gson.fromJson(
                new String(response.getPayload(), StandardCharsets.UTF_8),
                FileInfoModel.class
        );
    }

    /**
     * Initiates a file download. It first checks if the download can be resumed.
     *
     * @param fileId          The ID of the file to download.
     * @param destinationPath The final path where the file will be saved.
     * @return A {@link TransferInfo} object representing the state of the download.
     * @throws IOException If an I/O error occurs.
     */
    public TransferInfo initiateDownload(String fileId, String destinationPath) throws IOException {
        if (TransferFiles.canLoad(fileId)) {
            try {
                TransferFiles tf = TransferFiles.Load(fileId);
                if (tf != null) {
                    activeTransfers.put(fileId, tf);
                    return tf.getinfo();
                }
            } catch (Exception e) {
                System.err.println("Could not resume download for " + fileId + ", starting fresh. " + e.getMessage());
            }
        }
        String systemTempDir = System.getProperty("java.io.tmpdir");
        String tempPath = Paths.get(systemTempDir, "JTelegram").toString();

        return sendDownloadRequest(UUID.randomUUID(), fileId, destinationPath, tempPath);
    }

    /**
     * Initiates a file download and attempts to resume if possible.
     *
     * @param fileId The ID of the file to download.
     * @return A {@link TransferInfo} object representing the state of the download.
     * @throws IOException If an I/O error occurs.
     */
    public TransferInfo initiateDownload(String fileId) throws IOException {
        sendDownloadRequest(UUID.randomUUID(),fileId);
        String basePath = "src/files/temp";
        File infoFile = new File(basePath, fileId + ".info");
        if (infoFile.exists()) {
            try (FileReader reader = new FileReader(infoFile)) {
                return gson.fromJson(reader, TransferInfo.class);
            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Sends a request to the server to get file info and start a download.
     *
     * @param requestId       The UUID for this request.
     * @param fileId          The ID of the file to download.
     * @param destinationPath The final directory for the downloaded file.
     * @param tempPath        The directory for temporary download files.
     * @return A {@link TransferInfo} object representing the newly created transfer state.
     * @throws IOException If a communication error occurs.
     */
    private TransferInfo sendDownloadRequest(UUID requestId, String fileId, String destinationPath, String tempPath) throws IOException {
        DownloadFileInfoModel info = getDownloadFileInfoFromServer(fileId);

        int totalChunksCount = (int) Math.ceil((double) info.getFileLength() / 65536);
        createTransfer(fileId,info.getFileName(),info.getFileExtension(),destinationPath,tempPath,totalChunksCount,info.getFileLength());
        return activeTransfers.get(fileId).getinfo();
    }

    /**
     * Sends a request to the server to get file info and start a download to the default path.
     *
     * @param requestId The UUID for this request.
     * @param fileId    The ID of the file to download.
     * @throws IOException If a communication error occurs.
     */
    private void sendDownloadRequest(UUID requestId, String fileId) throws IOException {
        DownloadFileInfoModel info = getDownloadFileInfoFromServer(fileId);
        int totalChunksCount = (int) Math.ceil((double) info.getFileLength() / 65536);
        createTransfer(fileId,info.getFileName(),info.getFileExtension(),SAVE_PATH,"src/files/temp",totalChunksCount,info.getFileLength());
    }

    /**
     * Requests a specific chunk of a file from the server.
     *
     * @param fileId     The ID of the file.
     * @param chunkIndex The index of the chunk to request.
     * @param offset     The byte offset of the chunk.
     * @throws IOException If a communication error occurs.
     */
    public void sendDownloadChunkRequest(String fileId,int chunkIndex,long offset) throws IOException {
        DownloadChunkRequestMetadata metadata = new DownloadChunkRequestMetadata(fileId,chunkIndex,offset);
        byte[] metadataBytes = gson.toJson(metadata).getBytes(StandardCharsets.UTF_8);
        Message request = new Message(
                MessageHeader.BuildStartDownloadRequestHeader(UUID.randomUUID(), true, metadataBytes.length, 0),
                metadataBytes,
                new byte[0]
        );
        handler.write(request);
    }

    /**
     * Processes a received file chunk from the server.
     * It updates progress listeners and requests the next chunk if the transfer should continue.
     *
     * @param message The message containing the file chunk.
     * @throws IOException If an I/O error occurs while processing the chunk.
     */
    @Override
    public void ProcessSendChunk(Message message) throws IOException {
        super.ProcessSendChunk(message);
        var metadata = gson.fromJson(
                new String(message.getMetadata(), StandardCharsets.UTF_8),
                SendChunkMetadata.class
        );
        IProgressListener progressListener = progressListeners.get(metadata.fileId);
        if (progressListener != null) {
            progressListener.onProgress(metadata.chunkIndex+1,metadata.totalChunks);
        }
        BooleanSupplier continueSuppliersOrDefault = continueSuppliers.getOrDefault(metadata.fileId, () -> true);
        if(continueSuppliersOrDefault.getAsBoolean() && (metadata.chunkIndex != metadata.totalChunks-1)){
            sendDownloadChunkRequest(metadata.fileId,metadata.chunkIndex+1,metadata.offset+65536);
        }
    }

    /**
     * Fetches file information from the server for a given file ID.
     *
     * @param fileId The unique ID of the file.
     * @return A {@link DownloadFileInfoModel} containing the file's metadata.
     * @throws IOException If a communication error occurs.
     */
    public DownloadFileInfoModel getDownloadFileInfoFromServer(String fileId) throws IOException {
        UUID requestId = UUID.randomUUID();
        DownloadRequestMetadata metadata = new DownloadRequestMetadata(fileId);
        byte[] metadataBytes = gson.toJson(metadata).getBytes(StandardCharsets.UTF_8);

        Message request = new Message(
                MessageHeader.BuildDownloadRequestHeader(requestId, true, metadataBytes.length, 0),
                metadataBytes,
                new byte[0]
        );

        CompletableFuture<Message> futureResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureResponse);
        handler.write(request);

        Message response;
        try {
            response = futureResponse.join();
        } finally {
            pendingRequests.remove(requestId);
        }

        return gson.fromJson(
                new String(response.getPayload(), StandardCharsets.UTF_8),
                DownloadFileInfoModel.class
        );
    }
}