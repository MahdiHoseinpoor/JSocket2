package JSocket2.Protocol.EventHub;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event handler. The {@link EventBroker} will invoke methods
 * with this annotation when a corresponding event is published.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnEvent {
    /**
     * The name of the event that this method subscribes to.
     *
     * @return The event name.
     */
    String value();

    /**
     * The priority of the event handler. This is not currently implemented in the broker
     * but can be used for future extensions like ordered event handling.
     *
     * @return The priority level.
     */
    int priority() default 0;
}