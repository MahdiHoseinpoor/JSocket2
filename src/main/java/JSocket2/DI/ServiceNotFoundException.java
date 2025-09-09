package JSocket2.DI;

/**
 * Thrown when a requested service has not been registered with the {@link ServiceCollection}.
 */
public class ServiceNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code ServiceNotFoundException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }
}