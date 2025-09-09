package JSocket2.DI;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of service descriptors for a dependency injection container.
 * This class is used to register services and their lifetimes.
 */
public class ServiceCollection {
    /**
     * A map holding the service descriptors, keyed by the service type.
     */
    private final Map<Class<?>, ServiceDescriptor> descriptors = new HashMap<>();

    /**
     * Registers a scoped service with a specified implementation type.
     * A new instance will be created once per scope (e.g., per thread in this implementation).
     *
     * @param serviceType        The type of the service to register.
     * @param implementationType The implementation type of the service.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddScoped(Class<?> serviceType, Class<?> implementationType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, implementationType, ServiceLifetime.SCOPED));
        return this;
    }

    /**
     * Registers a singleton service with a pre-existing instance.
     * The same instance will be used for all requests for this service type.
     *
     * @param serviceType The type of the service to register.
     * @param instance    The instance to be used.
     * @param <T>         The type of the instance.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public <T> ServiceCollection AddSingletonWithInstance(Class<?> serviceType, T instance) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, ServiceLifetime.SINGLETON, instance));
        return this;
    }

    /**
     * Registers a singleton service with a specified implementation type.
     * A single instance will be created on the first request and reused for all subsequent requests.
     *
     * @param serviceType        The type of the service to register.
     * @param implementationType The implementation type of the service.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddSingleton(Class<?> serviceType, Class<?> implementationType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, implementationType, ServiceLifetime.SINGLETON));
        return this;
    }

    /**
     * Registers a transient service with a specified implementation type.
     * A new instance will be created every time the service is requested.
     *
     * @param serviceType        The type of the service to register.
     * @param implementationType The implementation type of the service.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddTransient(Class<?> serviceType, Class<?> implementationType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, implementationType, ServiceLifetime.TRANSIENT));
        return this;
    }

    /**
     * Registers a scoped service where the service type is also the implementation type.
     *
     * @param serviceType The service type.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddScoped(Class<?> serviceType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, serviceType, ServiceLifetime.SCOPED));
        return this;
    }

    /**
     * Registers a singleton service where the service type is also the implementation type.
     *
     * @param serviceType The service type.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddSingleton(Class<?> serviceType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, serviceType, ServiceLifetime.SINGLETON));
        return this;
    }

    /**
     * Registers a transient service where the service type is also the implementation type.
     *
     * @param serviceType The service type.
     * @return The current {@link ServiceCollection} instance for method chaining.
     */
    public ServiceCollection AddTransient(Class<?> serviceType) {
        descriptors.put(serviceType, new ServiceDescriptor(serviceType, serviceType, ServiceLifetime.TRANSIENT));
        return this;
    }

    /**
     * Creates a {@link ServiceProvider} from the registered service descriptors.
     *
     * @return A new {@link ServiceProvider} instance.
     */
    public ServiceProvider CreateServiceProvider() {
        return new ServiceProvider(descriptors);
    }
}