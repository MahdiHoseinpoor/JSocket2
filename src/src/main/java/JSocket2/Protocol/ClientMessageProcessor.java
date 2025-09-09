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

    public void Invoke(Message message) throws IOException {
        switch (message.header.type) {
            case RSA_PUBLIC_KEY -> handleRsaPublicKey(message);
            case SEND_CHUNK -> handleDownloadChunk(message);
            case EVENT -> handleEvent(message);
            default -> throw new UnsupportedOperationException("Unknown message type: " + message.header.type);
        }
    }

    private void handleEvent(Message message) {
        var metadatajson = new String(message.getMetadata(), StandardCharsets.UTF_8);
        System.out.println(metadatajson);
        var metadata = gson.fromJson(metadatajson, EventMetadata.class);
        var payloadJson = new String(message.getPayload(), StandardCharsets.UTF_8);
        eventBroker.publish(metadata, payloadJson);
    }

    private void handleDownloadChunk(Message message) throws IOException {
        fileTransferManager.ProcessSendChunk(message);
    }
    private void handleRsaPublicKey(Message message) throws IOException{
        try {
            var publicKey = EncryptionUtil.decodeRsaPublicKey(message.getPayload());
            clientSession.setServerPublicKey(publicKey);
            sendAesKey();
            //sendAuthModel();
            //Login
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

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