package JSocket2.Protocol;

import JSocket2.Core.Session;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Runs in a background thread to continuously listen for incoming messages.
 * It reads messages from the network, dispatches responses to waiting futures,
 * and passes other messages to a message processor.
 */
public class MessageListener implements Runnable {

    private final MessageHandler messageHandler;
    private final Map<UUID, CompletableFuture<Message>> pendingRequests;
    private final IMessageProcessor messageProcessor;
    private final Session session;
    private final IConnectionEventListener connectionEventListener;
    private volatile boolean running = true;

    /**
     * Constructs a MessageListener.
     *
     * @param messageHandler          The handler for reading messages from the stream.
     * @param pendingRequests         A map of pending requests awaiting a response.
     * @param messageProcessor        The processor for handling unsolicited messages.
     * @param session                 The current session.
     * @param connectionEventListener A listener for connection events, like disconnection.
     */
    public MessageListener(
            MessageHandler messageHandler,
            Map<UUID, CompletableFuture<Message>> pendingRequests,
            IMessageProcessor messageProcessor,
            Session session,
            IConnectionEventListener connectionEventListener
    ) {
        this.messageHandler = messageHandler;
        this.pendingRequests = pendingRequests;
        this.messageProcessor = messageProcessor;
        this.session = session;
        this.connectionEventListener = connectionEventListener;
    }

    /**
     * The main loop for listening for messages. Reads messages and dispatches them
     * until the listener is stopped or a connection error occurs.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Message message = messageHandler.read();
                if (message.header.uuid != null &&
                        pendingRequests.containsKey(message.header.uuid)) {
                    CompletableFuture<Message> future = pendingRequests.remove(message.header.uuid);
                    future.complete(message);
                } else {
                    messageProcessor.Invoke(message);
                }
            } catch (IOException e) {
                if (connectionEventListener != null) {
                    connectionEventListener.onConnectionLost();
                }
                stop();
            }
            catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    /**
     * Stops the message listening loop.
     */
    public void stop() {
        running = false;
    }
}