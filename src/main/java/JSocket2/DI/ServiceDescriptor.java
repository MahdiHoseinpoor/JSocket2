package JSocket2.DI;

/**
 * Describes a registered service, including its type, implementation, lifetime, and a potential pre-created instance.
 */
public class ServiceDescriptor {
    /**
     * The type of the service.
     */
    public final Class<?> serviceType;
    /**
     * The implementation type of the service. May be null if an instance is provided directly.
     */
    public final Class<?> implementationType;
    /**
     * The lifetime of the service (SINGLETON, SCOPED, or TRANSIENT).
     */
    public final ServiceLifetime lifetime;
    /**
     * A pre-created instance of the service. Only used for singleton lifetimes registered with an instance.
     */
    public final Object instance;

    /**
     * Constructs a {@code ServiceDescriptor} for a service with a pre-created instance.
     *
     * @param serviceType The type of the service.
     * @param lifetime    The lifetime of the service.
     * @param instance    The pre-created instance.
     */
    public ServiceDescriptor(Class<?> serviceType, ServiceLifetime lifetime, Object instance) {
        this.serviceType = serviceType;
        this.implementationType = null;
        this.lifetime = lifetime;
        this.instance = instance;
    }

    /**
     * Constructs a {@code ServiceDescriptor} for a service that will be instantiated by the container.
     *
     * @param serviceType        The type of the service.
     * @param implementationType The implementation type of the service.
     * @param lifetime           The lifetime of the service.
     */
    public ServiceDescriptor(Class<?> serviceType, Class<?> implementationType, ServiceLifetime lifetime) {
        this.serviceType = serviceType;
        this.implementationType = implementationType;
        this.lifetime = lifetime;
        this.instance = null;
    }
}