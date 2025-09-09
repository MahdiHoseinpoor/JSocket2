package JSocket2.Protocol.Rpc;

import JSocket2.DI.ServiceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection that scans and registers classes annotated with {@link RpcController}.
 * This collection is used to build an {@link RpcDispatcher}.
 */
public class RpcControllerCollection {
    private final Map<String, Class<?>> controllers = new HashMap<>();

    /**
     * Registers a controller class. The controller name is derived from the class's
     * simple name or the {@code Name} property of the {@link RpcController} annotation.
     *
     * @param controllerType The class type of the controller to register.
     * @throws IllegalStateException if a controller with the same name is already registered.
     */
    public void registerController(Class<?> controllerType) {
        String controllerName;
        if (controllerType.isAnnotationPresent(RpcController.class)) {
            RpcController rpcController = controllerType.getAnnotation(RpcController.class);
            controllerName = rpcController.Name().isEmpty()
                    ? controllerType.getSimpleName().toLowerCase()
                    : rpcController.Name().toLowerCase();
        } else {
            controllerName = controllerType.getSimpleName().toLowerCase();
        }

        if (controllers.containsKey(controllerName)) {
            throw new IllegalStateException("Controller '" + controllerName + "' is already registered.");
        }
        controllers.put(controllerName, controllerType);
    }

    /**
     * Creates an {@link RpcDispatcher} using the registered controllers.
     *
     * @param provider The {@link ServiceProvider} for resolving controller instances.
     * @return A new, configured {@link RpcDispatcher}.
     */
    public RpcDispatcher CreateRpcDispatcher(ServiceProvider provider) {
        return new RpcDispatcher(provider, controllers);
    }
}