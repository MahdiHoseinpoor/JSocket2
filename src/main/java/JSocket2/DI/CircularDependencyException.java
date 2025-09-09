package JSocket2.DI;

/**
 * Thrown to indicate that a circular dependency has been detected during service resolution.
 * This occurs when a service's dependency tree includes itself, creating an unresolvable loop.
 */
public class CircularDependencyException extends RuntimeException {
    /**
     * Constructs a new {@code CircularDependencyException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public CircularDependencyException(String message) {
        super(message);
    }
}