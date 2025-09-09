package JSocket2.Protocol.Rpc;

import JSocket2.Core.Server.ServerSessionManager;
import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.Authentication.UserIdentity;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Responsible for dispatching incoming RPC calls to the appropriate controller and action method.
 * It uses a {@link ServiceProvider} to resolve controller instances and reflection to invoke methods.
 */
public class RpcDispatcher {
    private final Map<String, Class<?>> controllers;
    private final ServiceProvider provider;
    private final Gson gson = new Gson();

    /**
     * Constructs a new {@code RpcDispatcher}.
     *
     * @param provider    The service provider for resolving controller instances.
     * @param controllers A map of registered controller names to their class types.
     */
    public RpcDispatcher(ServiceProvider provider, Map<String, Class<?>> controllers) {
        this.provider = provider;
        this.controllers = controllers;
    }

    /**
     * Dispatches an RPC call.
     *
     * @param metadata             The RPC call metadata.
     * @param payload_json         The JSON string of the method arguments.
     * @param serverSessionManager The server session manager.
     * @param activeUser           The identity of the user making the call.
     * @return The {@link RpcResponse} from the invoked action method.
     * @throws RuntimeException if the controller is not found, the method is not found,
     *                          or an error occurs during invocation.
     */
    public RpcResponse<?> dispatch(RpcCallMetadata metadata, String payload_json, ServerSessionManager serverSessionManager, UserIdentity activeUser) {
        String controllerName = metadata.getController().toLowerCase();
        String actionName = metadata.getAction().toLowerCase();
        Class<?> controllerType = controllers.get(controllerName);
        if (controllerType == null) {
            throw new RuntimeException("Controller not registered: " + controllerName);
        }
        Object controller = provider.GetService(controllerType);
        if (!(controller instanceof RpcControllerBase)) {
            throw new RuntimeException("Controller must inherit from RpcControllerBase: " + controllerType.getName());
        }

        RpcControllerBase rpcController = (RpcControllerBase) controller;
        rpcController.setCurrentUser(activeUser);
        if (rpcController.getServerSessionManager() == null) {
            rpcController.setServerSessionManager(serverSessionManager);
        }

        try {
            Object[] rawParameters = gson.fromJson(payload_json, Object[].class);
            if (rawParameters == null) {
                Method method = controller.getClass().getMethod(actionName);
                return (RpcResponse<?>) method.invoke(controller);
            } else {
                Map.Entry<Method, Object[]> methodEntry = findMatchingMethod(controllerType, actionName, rawParameters);
                return (RpcResponse<?>) methodEntry.getKey().invoke(controller, methodEntry.getValue());
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Action method not found: " + actionName, e);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a method in a controller that matches the name and parameter types of an RPC call.
     *
     * @param controllerClass The class of the controller.
     * @param methodName      The name of the action method.
     * @param parameters      The raw parameters from the JSON payload.
     * @return A {@link Map.Entry} containing the matched {@link Method} and the converted parameters.
     * @throws NoSuchMethodException if no suitable method is found.
     */
    private Map.Entry<Method, Object[]> findMatchingMethod(Class<?> controllerClass, String methodName, Object[] parameters)
            throws NoSuchMethodException {
        for (Method method : controllerClass.getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == parameters.length) {
                    try {
                        Object[] convertedParams = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            convertedParams[i] = gson.fromJson(gson.toJson(parameters[i]), paramTypes[i]);
                        }
                        return Map.entry(method, convertedParams);
                    } catch (Exception e) {
                        // Type mismatch, continue searching
                    }
                }
            }
        }
        throw new NoSuchMethodException("No suitable method found for action '" + methodName + "' with " + parameters.length + " parameters.");
    }
}