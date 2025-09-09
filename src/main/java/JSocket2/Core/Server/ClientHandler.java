package JSocket2.Core.Server;

import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.Authentication.IAuthService;
import JSocket2.Cryptography.RsaKeyManager;
import JSocket2.Protocol.*;
import JSocket2.Protocol.Rpc.RpcDispatcher;
import JSocket2.Protocol.Transfer.ServerFileTransferManager;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the server-side logic for a single connected client.
 * Each instance runs in its own thread, handling message processing,
 * session management, and communication for one client.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private RpcDispatcher rpcDispatcher;
    private MessageHandler messageHandler;
    private IMessageProcessor messageProcessor;
    private ServerFileTransferManager fileTransferManager;
    private RsaKeyManager rsaKeyManager;
    private IAuthService authService;
    private final IClientLifecycleListener clientLifecycleListener;
    private final ServiceProvider serviceProvider;
    private final Map<UUID, CompletableFuture<Message>> pendingRequests;
    private final ServerSession serverSession;
    private boolean isActive = true;

    /**
     * Constructs a new ClientHandler for a given client socket.
     *
     * @param serviceProvider      The dependency injection service provider.
     * @param socket               The client's socket connection.
     * @param rpcDispatcher        The dispatcher for handling RPC calls.
     * @param serverSessionManager The manager for all server sessions.
     * @param pendingRequests      A map of requests awaiting responses.
     * @throws IOException if an I/O error occurs when creating streams.
     */
    public ClientHandler(ServiceProvider serviceProvider,Socket socket,
                         RpcDispatcher rpcDispatcher,
                         ServerSessionManager serverSessionManager,
                         Map<UUID, CompletableFuture<Message>> pendingRequests) throws IOException {
        this.serviceProvider = serviceProvider;
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.rpcDispatcher = rpcDispatcher;
        this.serverSession = serverSessionManager.createSession(this);
        this.messageHandler = new MessageHandler(in,out,serverSession);
        this.rsaKeyManager = this.serviceProvider.GetService(RsaKeyManager.class);
        this.pendingRequests = pendingRequests;
        this.fileTransferManager = new ServerFileTransferManager(messageHandler,this.pendingRequests);
        this.authService =  serviceProvider.GetService(IAuthService.class);
        this.clientLifecycleListener = serviceProvider.GetService(IClientLifecycleListener.class);
        sendRsaPublicKey();
        this.messageProcessor = new ServerMessageProcessor(this.messageHandler,this.rpcDispatcher,this.fileTransferManager, serverSession,rsaKeyManager,this.authService,this.clientLifecycleListener);

    }
    private void sendRsaPublicKey() throws IOException {
        System.out.println("handshake is started");
        UUID requestId = UUID.randomUUID();
        byte[] publicKey = rsaKeyManager.getRSAPublicKey().getEncoded();
        MessageHeader header = MessageHeader.BuildRsaPublicKeyHeader(requestId,publicKey.length);
        Message message = new Message(header);
        message.setPayload(publicKey);
        messageHandler.write(message);
    }

    /**
     * The main execution loop for the client handler. Listens for incoming messages,
     * dispatches them to the message processor, and handles disconnection.
     */
    public void run() {
        while (isActive) {
            try {
                Message message = messageHandler.read();
                if (message.header.uuid != null &&
                        pendingRequests.containsKey(message.header.uuid)) {
                    CompletableFuture<Message> future = pendingRequests.remove(message.header.uuid);
                    if (future != null) {

                        future.complete(message);
                    }
                }else{
                    messageProcessor.Invoke(message);
                }
            } catch (IOException e) {
                System.out.println(e.fillInStackTrace());
                if (clientLifecycleListener != null) {
                    clientLifecycleListener.onClientDisconnected(serverSession);
                }
                try {
                    fileTransferManager.deactivateTransfers();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                isActive = false;
            }
        }
    }

    /**
     * Sends a message to the connected client.
     *
     * @param message The {@link Message} to send.
     * @throws IOException if a communication error occurs.
     */
    public void send(Message message) throws IOException {
        messageHandler.write(message);
    }
}