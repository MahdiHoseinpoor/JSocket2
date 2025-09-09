package JSocket2.Protocol;

import java.io.IOException;

/**
 * Defines the contract for a message processor.
 * A message processor is responsible for handling incoming messages.
 */
public interface IMessageProcessor {
    /**
     * Invoked to process an incoming message.
     *
     * @param message The message to be processed.
     * @throws IOException If an I/O error occurs during message processing.
     */
    void Invoke(Message message) throws IOException;
}