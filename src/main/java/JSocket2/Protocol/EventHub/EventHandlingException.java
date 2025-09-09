package JSocket2.Protocol.EventHub;

/**
 * Thrown when an exception occurs while an {@link EventBroker} is attempting to invoke
 * an event handler method on a subscriber.
 */
public class EventHandlingException extends RuntimeException {
    /**
     * Constructs a new {@code EventHandlingException} with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public EventHandlingException(String message, Exception cause) {
        super(message, cause);
    }
}