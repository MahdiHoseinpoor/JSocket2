package JSocket2.Core.Client;

import JSocket2.DI.ServiceCollection;
import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.*;
import JSocket2.Protocol.Authentication.AuthModel;
import JSocket2.Protocol.EventHub.EventBroker;
import JSocket2.Protocol.EventHub.EventSubscriberCollection;
import JSocket2.Protocol.Rpc.RpcResponseMetadata;
import JSocket2.Protocol.Transfer.ClientFileTransferManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ClientApplication implements IConnectionEventListener {
    private final String host;
    private final int port;
    private final ReconnectionOptions options;
    private final EventSubscriberCollection subscribers;
    private final ServiceCollection services;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private volatile boolean shutdownRequested = false;

    private final List<Consumer<Boolean>> connectionStatusListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<ClientApplication>> connectedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<ClientApplication>> reconnectListeners = new CopyOnWriteArrayList<>();

    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor(r -> new Thread(r, "JSocket-ConnectionManager"));
    private final ExecutorService backgroundExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    // Protocol and State fields
    private final Gson gson = new Gson();
    private Socket socket;
    private MessageHandler messageHandler;
    private ClientMessageProcessor messageProcessor;
    private MessageListener messageListener;
    private Thread listenerThread;
    private ClientSession clientSession;
    private ClientFileTransferManager fileTransferManager;
    private EventBroker eventBroker;
    private ServiceProvider serviceProvider;
    private final ConcurrentMap<UUID, CompletableFuture<Message>> pendingRequests = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Future<?>> activeTasks = new ConcurrentHashMap<>();

    public ClientApplication(String host, int port, IConnectionEventListener ignored, EventSubscriberCollection subscribers, ServiceCollection services, ReconnectionOptions options) {
        this.host = host;
        this.port = port;
        this.subscribers = subscribers;
        this.services = services;
        this.options = options;
        this.serviceProvider = services.CreateServiceProvider();
        this.eventBroker = subscribers.CreateEventBroker(serviceProvider);
    }

    /**
     * Starts the client asynchronously. It will attempt to connect and, if it fails
     * or gets disconnected, will automatically try to reconnect based on the configured
     * ReconnectionOptions.
     */
    public void startAsync() {
        if (shutdownRequested) {
            throw new IllegalStateException("Client has been shut down and cannot be restarted.");
        }
        connectionExecutor.submit(this::connectionLoop);
    }

    private void connectionLoop() {
        Random random = new Random();
        int tryCount = 0;
        int currentRetryDelay = options.getMinRetryDelay();

        while (!shutdownRequested) {
            if (tryConnectOnce()) {
                // Successfully connected. The message listener thread will now handle
                // disconnects by calling onConnectionLost(). This loop's job is done for now.
                return;
            }

            // Connection failed, wait before retrying
            try {
                Thread.sleep(currentRetryDelay);
                tryCount++;
                if (tryCount >= options.getMaxTryCount_for_changeRetryDelay()) {
                    var delay = Math.min(options.getMaxRetryDelay(), (int) (currentRetryDelay * 1.5));
                    currentRetryDelay = random.nextInt((int) (delay * options.getCoefficient_jitter()), delay);
                    tryCount = 0;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit if interrupted
            }
        }
    }

    private boolean tryConnectOnce() {
        try {
            socket = new Socket(host, port);
            clientSession = new ClientSession();
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            messageHandler = new MessageHandler(in, out, clientSession);
            messageProcessor = new ClientMessageProcessor(messageHandler, clientSession, pendingRequests, getFileTransferManager(), this::onConnected, serviceProvider, eventBroker);
            // Pass 'this' as the IConnectionEventListener
            messageListener = new MessageListener(messageHandler, pendingRequests, messageProcessor, clientSession, this);

            listenerThread = new Thread(messageListener, "JSocket-MessageListener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            running.set(true);

            return true;
        } catch (IOException e) {
            running.set(false);
            onDisconnected();
            return false;
        }
    }

    /**
     * Permanently stops the client, closes the connection, and halts any reconnection attempts.
     */
    public void shutdown() {
        shutdownRequested = true;
        connectionExecutor.shutdownNow();
        backgroundExecutor.shutdownNow();
        cleanupCurrentConnection();
    }

    private void cleanupCurrentConnection() {
        try {
            if (listenerThread != null && listenerThread.isAlive()) {
                listenerThread.interrupt();
            }
            for (var activeTask : activeTasks.values()) {
                activeTask.cancel(true);
            }
            activeTasks.clear();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        } finally {
            onDisconnected();
        }
    }

    @Override
    public void onConnectionLost() {
        cleanupCurrentConnection();
        if (!shutdownRequested) {
            System.out.println("Connection lost. Attempting to reconnect...");
            reconnectListeners.forEach(listener -> listener.accept(this));
            // Resubmit the connection loop to start the reconnection process
            startAsync();
        }
    }

    // --- Event Listeners and Callbacks ---
    private void onConnected() {
        connected.set(true);
        connectedListeners.forEach(listener -> listener.accept(this));
        connectionStatusListeners.forEach(listener -> listener.accept(true));
    }

    private void onDisconnected() {
        connected.set(false);
        connectionStatusListeners.forEach(listener -> listener.accept(false));
    }

    public void addConnectionStatusListener(Consumer<Boolean> listener) {
        connectionStatusListeners.add(listener);
    }

    public void removeConnectionStatusListener(Consumer<Boolean> listener) {
        connectionStatusListeners.remove(listener);
    }

    public void addConnectedListener(Consumer<ClientApplication> listener) {
        connectedListeners.add(listener);
    }

    public void addReconnectListener(Consumer<ClientApplication> listener) {
        reconnectListeners.add(listener);
    }

    // --- Getters and Business Logic Methods ---
    public boolean isConnected() {
        return connected.get();
    }

    public ExecutorService getBackgroundExecutor() {
        return backgroundExecutor;
    }

    public void registerTask(String Id, Future<?> task) {
        activeTasks.put(Id, task);
    }

    public void unregisterTask(String Id) {
        activeTasks.remove(Id);
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public ConcurrentMap<UUID, CompletableFuture<Message>> getPendingRequests() {
        return pendingRequests;
    }

    public ClientFileTransferManager getFileTransferManager() {
        if (fileTransferManager == null && messageHandler != null) {
            fileTransferManager = new ClientFileTransferManager(messageHandler, pendingRequests);
        }
        return fileTransferManager;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public StatusCode sendAuthModel(AuthModel authModel) throws IOException, InterruptedException {
        if (!isConnected()) {
            throw new IOException("Client is not connected.");
        }
        var payloadJson = gson.toJson(authModel);
        UUID requestId = UUID.randomUUID();
        MessageHeader header = MessageHeader.BuildAuthHeader(requestId,payloadJson.length());
        Message message = new Message(header);
        message.setPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

        CompletableFuture<Message> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        messageHandler.write(message);

        try {
            var response = future.get(10, TimeUnit.SECONDS); // Add a timeout
            var metadata = gson.fromJson(new String(response.getMetadata(),StandardCharsets.UTF_8), RpcResponseMetadata.class);
            return StatusCode.fromCode(metadata.getStatusCode());
        } catch (ExecutionException | TimeoutException e) {
            throw new IOException("Failed to get authentication response", e);
        } finally {
            pendingRequests.remove(requestId);
        }
    }
}