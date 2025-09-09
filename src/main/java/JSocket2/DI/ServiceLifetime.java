package JSocket2.DI;

/**
 * Specifies the lifetime of a service in a dependency injection container.
 */
public enum ServiceLifetime {
    /**
     * A single instance of the service is created and shared across all requests.
     */
    SINGLETON,
    /**
     * A new instance of the service is created for each request.
     */
    TRANSIENT,
    /**
     * A new instance of the service is created once per scope. In this implementation, a scope is tied to a thread.
     */
    SCOPED
}