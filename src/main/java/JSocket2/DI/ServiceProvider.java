package JSocket2.DI;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves services based on the descriptors provided by a {@link ServiceCollection}.
 * This class manages the lifetime of services (Singleton, Scoped, Transient) and handles dependency injection.
 */
public class ServiceProvider {
    private final Map<Class<?>, ServiceDescriptor> descriptors;
    private final Map<Class<?>, Object> singletonInstances = new ConcurrentHashMap<>();
    private final ThreadLocal<Map<Class<?>, Object>> scopedInstances = ThreadLocal.withInitial(ConcurrentHashMap::new);
    private final ThreadLocal<Set<Class<?>>> creatingStack = ThreadLocal.withInitial(HashSet::new);
    private final ThreadLocal<Boolean> scopeActive = ThreadLocal.withInitial(() -> false);

    /**
     * Constructs a new {@code ServiceProvider} with a map of service descriptors.
     *
     * @param descriptors The service descriptors to use for service resolution.
     */
    public ServiceProvider(Map<Class<?>, ServiceDescriptor> descriptors) {
        this.descriptors = new ConcurrentHashMap<>(descriptors);
    }

    /**
     * Retrieves a service of the specified type from the container.
     *
     * @param service The type of the service to retrieve.
     * @param <T>     The type of the service.
     * @return An instance of the requested service.
     * @throws IllegalArgumentException if the service is not registered.
     */
    public <T> T GetService(Class<T> service) {
        ServiceDescriptor descriptor = descriptors.get(service);
        if (descriptor == null) {
            throw new IllegalArgumentException("Service not registered: " + service.getName());
        }
        if (descriptor.lifetime == ServiceLifetime.SCOPED && !scopeActive.get()) {
            scopeActive.set(true);
        }
        return resolveService(descriptor);
    }

    /**
     * Resolves a service based on its descriptor and lifetime.
     *
     * @param descriptor The descriptor of the service to resolve.
     * @param <T>        The type of the service.
     * @return An instance of the resolved service.
     */
    private <T> T resolveService(ServiceDescriptor descriptor) {
        switch (descriptor.lifetime) {
            case SCOPED:
                return resolveScopedService(descriptor);
            case SINGLETON:
                return resolveSingletonService(descriptor);
            case TRANSIENT:
                return createNewInstance(descriptor);
            default:
                throw new IllegalArgumentException("Unknown lifetime: " + descriptor.lifetime);
        }
    }

    /**
     * Resolves a scoped service. A single instance is maintained per thread.
     *
     * @param descriptor The descriptor of the service to resolve.
     * @param <T>        The type of the service.
     * @return An instance of the scoped service.
     */
    @SuppressWarnings("unchecked")
    private <T> T resolveScopedService(ServiceDescriptor descriptor) {
        Map<Class<?>, Object> scopeMap = scopedInstances.get();
        return (T) scopeMap.computeIfAbsent(descriptor.serviceType,
                k -> createNewInstance(descriptor));
    }

    /**
     * Resolves a singleton service. A single instance is maintained for the lifetime of the provider.
     *
     * @param descriptor The descriptor of the service to resolve.
     * @param <T>        The type of the service.
     * @return The singleton instance of the service.
     */
    @SuppressWarnings("unchecked")
    private <T> T resolveSingletonService(ServiceDescriptor descriptor) {
        if (descriptor.instance != null) {
            return (T) descriptor.instance;
        }
        return (T) singletonInstances.computeIfAbsent(descriptor.serviceType,
                k -> createNewInstance(descriptor));
    }

    /**
     * Creates a new instance of a service, handling circular dependency checks.
     *
     * @param descriptor The descriptor of the service to instantiate.
     * @param <T>        The type of the service.
     * @return A new instance of the service.
     * @throws CircularDependencyException if a circular dependency is detected.
     */
    @SuppressWarnings("unchecked")
    private <T> T createNewInstance(ServiceDescriptor descriptor) {
        Class<?> implementationType = descriptor.implementationType;
        Set<Class<?>> stack = creatingStack.get();
        if (stack.contains(implementationType)) {
            throw new CircularDependencyException(
                    "Circular dependency detected: " + stack + " -> " + implementationType
            );
        }
        stack.add(implementationType);
        try {
            return (T) createInstance(implementationType);
        } finally {
            stack.remove(implementationType);
        }
    }

    /**
     * Instantiates a class by resolving its constructor and its dependencies.
     *
     * @param implementationType The class to instantiate.
     * @param <T>                The type of the class.
     * @return A new instance of the class.
     * @throws ServiceCreationException if instantiation fails.
     */
    private <T> T createInstance(Class<T> implementationType) {
        try {
            Constructor<?>[] constructors = implementationType.getConstructors();
            Constructor<?> constructor = selectConstructor(constructors);
            constructor.setAccessible(true);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = GetService(paramTypes[i]);
            }
            Object instance = constructor.newInstance(params);
            return implementationType.cast(instance);
        } catch (CircularDependencyException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceCreationException(
                    "Failed to create instance of " + implementationType.getName()
            );
        }
    }

    /**
     * Selects the appropriate constructor for instantiation.
     * If there is only one constructor, it is used. If there are multiple,
     * the one annotated with {@link Inject} is selected.
     *
     * @param constructors An array of available constructors.
     * @return The constructor to be used for instantiation.
     * @throws ServiceCreationException if there are multiple constructors and none are marked with {@link Inject}.
     */
    private Constructor<?> selectConstructor(Constructor<?>[] constructors) {
        if (constructors.length == 1) {
            return constructors[0];
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }
        throw new ServiceCreationException("Multiple constructors found but no @Inject annotation specified");
    }
}