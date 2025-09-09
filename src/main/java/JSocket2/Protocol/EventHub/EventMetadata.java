package JSocket2.Protocol.EventHub;

/**
 * Holds metadata associated with an event, primarily the event's name.
 */
public class EventMetadata {
    private final String eventName;

    /**
     * Constructs new {@code EventMetadata}.
     *
     * @param eventName The name of the event.
     */
    public EventMetadata(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the name of the event.
     *
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }
}