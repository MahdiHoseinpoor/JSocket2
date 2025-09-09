package JSocket2.Protocol;

import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.Authentication.AuthProcessState;
import JSocket2.Core.Client.ClientSession;
import JSocket2.Cryptography.EncryptionUtil;
import JSocket2.Protocol.Authentication.IAccessKeyManager;
import JSocket2.Protocol.EventHub.EventBroker;
import JSocket2.Protocol.EventHub.EventMetadata;
import JSocket2.Protocol.Rpc.RpcResponseMetadata;
import JSocket2.Protocol.Transfer.ClientFileTransferManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Processes messages received by the client from the server.
 * This class is responsible for handling different types of messages such as
 * cryptographic key exchanges, file transfer chunks, and events.
 */
public class ClientMessageProcessor implements IMessageProcessor {
    private final Gson gson;
    private final MessageHandler messageHandler;
    private final ClientSession clientSession;
    private final ClientFileTransferManager fileTransferManager;
    private final Map<UUID, CompletableFuture<Message>> pendingRequests;
    private IAccessKeyManager accessKeyManager;
    private final Runnable onHandShakeComplete;
    private final ServiceProvider serviceProvider;
    private final EventBroker eventBroker;

    /**
     * Constructs a new ClientMessageProcessor.
     *
     * @param handler             The handler for reading and writing messages.
     * @param clientSession       The session object for the client.
     * @param pendingRequests     A map of pending requests awaiting a response.
     * @param fileTransferManager The manager for handling file transfers.
     * @param onHandShakeComplete A callback to run when the cryptographic handshake is complete.
     * @param serviceProvider     The dependency injection service provider.
     * @param eventBroker         The broker for publishing and subscribing to events.
     */
    public ClientMessageProcessor(MessageHandler handler, ClientSession clientSession, Map<UUID, CompletableFuture<Message>> pendingRequests, ClientFileTransferManager fileTransferManager, Runnable onHandShakeComplete, ServiceProvider serviceProvider, EventBroker eventBroker){
        this.onHandShakeComplete = onHandShakeComplete;
        this.serviceProvider = serviceProvider;
        this.eventBroker = eventBroker;
        this.gson = new Gson();
        this.messageHandler = handler;
        this.clientSession = clientSession;
        this.pendingRequests = pendingRequests;
        this.fileTransferManager = fileTransferManager;
    }

    /**
     * Processes an incoming message by delegating it to the appropriate handler based on its type.
     *
     * @param message The message to process.
     * @throws IOException If an I/O error occurs.
     * @throws UnsupportedOperationException If the message type is unknown.
     */
    public void Invoke(Message message) throws IOException {
        switch (message.header.type) {
            case RSA_PUBLIC_KEY -> handleRsaPublicKey(message);
            case SEND_CHUNK -> handleDownloadChunk(message);
            case EVENT -> handleEvent(message);
            default -> throw new UnsupportedOperationException("Unknown message type: " + message.header.type);
        }
    }

    /**
     * Handles an incoming event message.
     *
     * @param message The event message.
     */
    private void handleEvent(Message message) {
        var metadatajson = new String(message.getMetadata(), StandardCharsets.UTF_8);
        System.out.println(metadatajson);
        var metadata = gson.fromJson(metadatajson, EventMetadata.class);
        var payloadJson = new String(message.getPayload(), StandardCharsets.UTF_8);
        eventBroker.publish(metadata, payloadJson);
    }

    /**
     * Handles a file chunk received from the server during a download.
     *
     * @param message The message containing the file chunk.
     * @throws IOException If an I/O error occurs during file processing.
     */
    private void handleDownloadChunk(Message message) throws IOException {
        fileTransferManager.ProcessSendChunk(message);
    }

    /**
     * Handles the server's RSA public key to initiate the secure session.
     *
     * @param message The message containing the RSA public key.
     * @throws IOException If an I/O error occurs.
     */
    private void handleRsaPublicKey(Message message) throws IOException{
        try {
            var publicKey = EncryptionUtil.decodeRsaPublicKey(message.getPayload());
            clientSession.setServerPublicKey(publicKey);
            sendAesKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts the client's AES key with the server's public RSA key and sends it to the server.
     *
     * @throws IOException If an I/O error occurs while sending the key.
     */
    private void sendAesKey() throws IOException {
        UUID requestId = UUID.randomUUID();
        byte[] aes_key = clientSession.getAESKey().getEncoded();
        byte[] encrypted_aes_key = EncryptionUtil.encryptDataRSA(aes_key,clientSession.getServerPublicKey());
        MessageHeader header = MessageHeader.BuildAesKeyHeader(requestId,encrypted_aes_key.length);
        Message message = new Message(header);
        message.setPayload(encrypted_aes_key);
        messageHandler.write(message);
        if(onHandShakeComplete != null){
            onHandShakeComplete.run();
        }
    }
}