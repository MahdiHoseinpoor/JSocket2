package JSocket2.Protocol;

import JSocket2.Core.Server.IClientLifecycleListener;
import JSocket2.Protocol.Authentication.IAuthService;
import JSocket2.Protocol.Authentication.InvalidAccessKeyException;
import JSocket2.Core.Server.ServerSession;
import JSocket2.Cryptography.EncryptionUtil;
import JSocket2.Cryptography.RsaKeyManager;
import JSocket2.Protocol.Authentication.AuthModel;
import JSocket2.Protocol.Rpc.RpcCallMetadata;
import JSocket2.Protocol.Rpc.RpcDispatcher;
import JSocket2.Protocol.Rpc.RpcResponseMetadata;
import JSocket2.Protocol.Transfer.ServerFileTransferManager;
import JSocket2.Protocol.Transfer.Upload.UploadResumeRequestMetadata;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Processes messages received by the server from a client.
 * This class handles various message types including RPC calls, file transfers,
 * and authentication, delegating tasks to the appropriate managers and services.
 */
public class ServerMessageProcessor implements IMessageProcessor {
    private final Gson gson;
    private final RpcDispatcher rpcDispatcher;
    private final MessageHandler messageHandler;
    private final ServerFileTransferManager fileTransferManager;
    private final ServerSession serverSession;
    private final IAuthService authService;
    private RsaKeyManager rsaKeyManager;
    private final IClientLifecycleListener clientLifecycleListener;

    /**
     * Constructs a new ServerMessageProcessor.
     *
     * @param handler                 The handler for reading and writing messages.
     * @param rpcDispatcher           The dispatcher for handling RPC calls.
     * @param fileTransferManager     The manager for file transfers.
     * @param serverSession           The session object for the connected client.
     * @param rsaKeyManager           The manager for RSA keys used in handshakes.
     * @param authService             The service for authenticating users.
     * @param clientLifecycleListener A listener for client lifecycle events like authentication.
     */
    public ServerMessageProcessor(MessageHandler handler, RpcDispatcher rpcDispatcher, ServerFileTransferManager fileTransferManager, ServerSession serverSession, RsaKeyManager rsaKeyManager, IAuthService authService, IClientLifecycleListener clientLifecycleListener) {
        this.gson = new Gson();
        this.messageHandler = handler;
        this.rpcDispatcher = rpcDispatcher;
        this.fileTransferManager = fileTransferManager;
        this.serverSession = serverSession;
        this.rsaKeyManager = rsaKeyManager;
        this.authService = authService;
        this.clientLifecycleListener = clientLifecycleListener;
    }

    /**
     * Processes an incoming message by delegating it to the appropriate handler based on its type.
     *
     * @param message The message to process.
     * @throws IOException If an I/O error occurs.
     * @throws UnsupportedOperationException If the message type is unknown.
     */
    @Override
    public void Invoke(Message message) throws IOException {
        switch (message.header.type) {
            case RPC_CALL -> handleRpcCall(message);
            case UPLOAD_REQUEST -> handleUploadRequest(message);
            case UPLOAD_CHUNK -> handleUploadChunk(message);
            case SEND_CHUNK -> handleUploadChunk(message);
            case UPLOAD_RESUME_REQUEST -> handleUploadResumeRequest(message);
            case DOWNLOAD_REQUEST -> handleDownloadRequest(message);
            case DOWNLOAD_START -> handleStartDownload(message);
            case UPLOAD_ACK -> {}
            case AES_KEY -> handeAesKey(message);
            case AUTH -> handleAuth(message);
            default -> throw new UnsupportedOperationException("Unknown message type: " + message.header.type);
        }
    }

    /**
     * Handles a request to start sending chunks for a file download.
     * @param message The start download message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleStartDownload(Message message) throws IOException {
        fileTransferManager.ProcessDownloadChunkRequest(message);
    }

    /**
     * Handles a file download request from a client.
     * @param message The download request message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleDownloadRequest(Message message) throws IOException {
        fileTransferManager.ProcessDownloadRequest(message);
    }

    /**
     * Handles a request to resume a file upload.
     * @param message The resume upload request message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleUploadResumeRequest(Message message) throws IOException {
        var metadata = gson.fromJson(new String(message.getMetadata(), StandardCharsets.UTF_8), UploadResumeRequestMetadata.class);
        fileTransferManager.ProcessUploadResumeRequest(message.header.uuid, metadata);
    }

    /**
     * Handles the encrypted AES key sent by the client, completing the handshake.
     * @param message The message containing the encrypted AES key.
     */
    private void handeAesKey(Message message) {
        var encrypted_aes_key = message.getPayload();
        var decrypted_aes_key = EncryptionUtil.decryptDataRSA(encrypted_aes_key, rsaKeyManager.getRSAPrivateKey());
        var aesKey = EncryptionUtil.decodeAesKey(decrypted_aes_key);
        serverSession.setAESKey(aesKey);
        System.out.println("handshake is complete");
    }

    /**
     * Handles an authentication request from the client.
     * @param message The authentication message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleAuth(Message message) throws IOException {
        var payloadJson = new String(message.getPayload(), StandardCharsets.UTF_8);
        var response = gson.fromJson(payloadJson, AuthModel.class);
        String responseMetadata = null;
        try {
            for (var key : response.getAccessKeys()) {
                if(!authService.IsKeyValid(key))
                    continue;
                var user = authService.Login(key);
                serverSession.subscribeUser(user);
                if (clientLifecycleListener != null) {
                    clientLifecycleListener.onClientAuthenticated(serverSession);
                }
            }
            responseMetadata = gson.toJson(new RpcResponseMetadata(StatusCode.OK.code, "Auth was successful"));
        }
        catch (InvalidAccessKeyException e) {
            responseMetadata = gson.toJson(new RpcResponseMetadata(StatusCode.BAD_REQUEST.code, "Auth was failed"));
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            var msg = new Message(
                    MessageHeader.BuildRpcResponseHeader(message.header.uuid, false, responseMetadata.length(), 0),
                    responseMetadata.getBytes(StandardCharsets.UTF_8),
                    new byte[0]
            );
            messageHandler.write(msg);
        }
    }

    /**
     * Handles an RPC call from the client.
     * @param message The RPC call message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleRpcCall(Message message) throws IOException {
        var metadata = gson.fromJson(new String(message.getMetadata(), StandardCharsets.UTF_8), RpcCallMetadata.class);
        var payloadJson = new String(message.getPayload(), StandardCharsets.UTF_8);
        var response = rpcDispatcher.dispatch(metadata, payloadJson,serverSession.getServerSessionManager(),serverSession.getActiveUser());
        if(response != null) {
            var rpcResponseMetadataString = gson.toJson(new RpcResponseMetadata(response.getStatusCode().code, response.getMessage()));
            var rpcResponsePayloadString = gson.toJson(response.getPayload());

            byte[] responseMetadataBytes = rpcResponseMetadataString.getBytes(StandardCharsets.UTF_8);
            byte[] responsePayloadBytes = rpcResponsePayloadString.getBytes(StandardCharsets.UTF_8);

            var msg = new Message(
                    MessageHeader.BuildRpcResponseHeader(message.header.uuid, false, responseMetadataBytes.length, responsePayloadBytes.length),
                    responseMetadataBytes,
                    responsePayloadBytes
            );

            messageHandler.write(msg);
        }
    }

    /**
     * Handles a file upload request from the client.
     * @param message The upload request message.
     * @throws IOException if an I/O error occurs.
     */
    private void handleUploadRequest(Message message) throws IOException {
        fileTransferManager.ProcessUploadRequest(message);
    }

    /**
     * Handles a file chunk sent by the client.
     * @param message The message containing the file chunk.
     * @throws IOException if an I/O error occurs.
     */
    private void handleUploadChunk(Message message) throws IOException {
        fileTransferManager.ProcessSendChunk(message);
    }
}