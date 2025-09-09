package JSocket2.DI;

/**
 * Thrown when an error occurs during the instantiation of a service by the {@link ServiceProvider}.
 * This can be caused by issues like missing dependencies, lack of an appropriate constructor, or exceptions thrown by the constructor itself.
 */
public class ServiceCreationException extends RuntimeException {
    /**
     * Constructs a new {@code ServiceCreationException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public ServiceCreationException(String message) {
        super(message);
    }
}