package JSocket2.Protocol.EventHub;

import JSocket2.DI.ServiceProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the collection of event subscribers. It scans classes for methods
 * annotated with {@link OnEvent} and registers them as subscribers for the specified events.
 */
public class EventSubscriberCollection {
    private final Map<String, List<Class<?>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Constructs a new, empty {@code EventSubscriberCollection}.
     */
    public EventSubscriberCollection() {
    }

    /**
     * Scans a subscriber class for methods annotated with {@link OnEvent} and registers them.
     *
     * @param subscribeType The class to scan for event handler methods.
     * @param <TSubscriber> The type of the subscriber class.
     */
    public <TSubscriber> void subscribe(Class<TSubscriber> subscribeType) {
        for (var method : subscribeType.getMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                var onEventAnnotation = method.getAnnotation(OnEvent.class);
                if (!onEventAnnotation.value().isEmpty()) {
                    subscribe(onEventAnnotation.value(), subscribeType);
                }
            }
        }
    }

    /**
     * Subscribes a class to a specific event name.
     *
     * @param eventName     The name of the event.
     * @param subscribeType The class that will handle the event.
     * @param <TSubscriber> The type of the subscriber class.
     */
    private <TSubscriber> void subscribe(String eventName, Class<TSubscriber> subscribeType) {
        subscribers.computeIfAbsent(eventName.toLowerCase(), k -> new CopyOnWriteArrayList<>())
                .add(subscribeType);
    }

    /**
     * Creates an {@link EventBroker} instance using the collected subscribers.
     *
     * @param provider The {@link ServiceProvider} that the {@link EventBroker} will use to resolve subscriber instances.
     * @return A new {@link EventBroker} configured with the registered subscribers.
     */
    public EventBroker CreateEventBroker(ServiceProvider provider) {
        return new EventBroker(provider, subscribers);
    }
}