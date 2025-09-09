package JSocket2.Protocol.EventHub;

import JSocket2.DI.ServiceProvider;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatches events to registered subscribers.
 * It uses a {@link ServiceProvider} to resolve subscriber instances and reflection to invoke the appropriate handler methods.
 */
public class EventBroker {
    private final Map<String, List<Class<?>>> subscribers;
    private final ServiceProvider provider;
    private final Gson gson = new Gson();

    /**
     * Constructs a new {@code EventBroker}.
     *
     * @param provider    The {@link ServiceProvider} used to get instances of subscribers.
     * @param subscribers A map where keys are event names and values are lists of subscriber classes.
     */
    public EventBroker(ServiceProvider provider, Map<String, List<Class<?>>> subscribers) {
        this.provider = provider;
        this.subscribers = subscribers;
    }

    /**
     * Publishes an event to all its subscribers.
     * It finds the relevant subscribers, resolves their instances, and invokes the correct handler method with the deserialized payload.
     *
     * @param metadata    The event's metadata, containing the event name.
     * @param payloadJson The event's payload, serialized as a JSON string.
     * @throws EventHandlingException if an error occurs during method invocation on a subscriber.
     */
    public void publish(EventMetadata metadata, String payloadJson) {
        String eventName = metadata.getEventName().toLowerCase();
        List<Class<?>> subscriberTypes = subscribers.get(eventName);

        if (subscriberTypes == null || subscriberTypes.isEmpty()) return;

        subscriberTypes.parallelStream().forEach(subscriberType -> {
            Object subscriber = provider.GetService(subscriberType);
            try {
                Object[] rawParameters = gson.fromJson(payloadJson, Object[].class);
                Map<Method, Object[]> matchingMethods = findMatchingMethod(subscriberType, eventName, rawParameters);
                for (Map.Entry<Method, Object[]> methodsEntry : matchingMethods.entrySet()) {
                    Method method = methodsEntry.getKey();
                    Object[] parameters = methodsEntry.getValue();
                    method.invoke(subscriber, parameters);
                }
            } catch (Exception e) {
                throw new EventHandlingException("Error handling event in subscriber: " + subscriberType.getName(), e);
            }
        });
    }

    /**
     * Finds methods in a subscriber class that match the event name and can accept the provided parameters.
     * It attempts to deserialize the parameters to the specific types required by the method signature.
     *
     * @param subscriberClass The class of the subscriber to inspect.
     * @param eventName       The name of the event.
     * @param parameters      An array of raw parameter objects deserialized from the JSON payload.
     * @return A map of matching {@link Method}s to their converted parameter arrays.
     */
    private Map<Method, Object[]> findMatchingMethod(Class<?> subscriberClass, String eventName, Object[] parameters) {
        Map<Method, Object[]> matchingMethods = new HashMap<>();
        Method[] methods = subscriberClass.getMethods();

        for (Method method : methods) {
            if (isEventHandlerMethod(method, eventName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == parameters.length) {
                    try {
                        Object[] convertedParams = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            convertedParams[i] = gson.fromJson(
                                    gson.toJson(parameters[i]),
                                    paramTypes[i]
                            );
                        }
                        matchingMethods.put(method, convertedParams);
                    } catch (Exception e) {
                        // Ignore if parameter types do not match
                    }
                }
            }
        }
        return matchingMethods;
    }

    /**
     * Checks if a method is an event handler for a specific event.
     *
     * @param method    The method to check.
     * @param eventName The name of the event.
     * @return {@code true} if the method is annotated with {@code @OnEvent} and its value matches the event name (case-insensitive).
     */
    private static boolean isEventHandlerMethod(Method method, String eventName) {
        return method.isAnnotationPresent(OnEvent.class) && method.getAnnotation(OnEvent.class).value().toLowerCase().equals(eventName);
    }
}