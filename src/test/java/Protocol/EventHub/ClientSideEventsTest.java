package Protocol.EventHub;

import JSocket2.DI.ServiceCollection;
import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.EventHub.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the client-side event handling mechanism, specifically focusing on the {@link EventBroker}.
 * These tests verify that events are correctly published and that subscribers receive and process them as expected.
 */
public class ClientSideEventsTest {
    ServiceProvider serviceProvider;
    EventBroker eventBroker;
    EventSubscriberCollection eventSubscriberCollection = new EventSubscriberCollection();
    Gson gson = new Gson();

    /**
     * Sets up the testing environment before each test.
     * This method initializes the dependency injection container, registers the {@link SimpleEventSubscriber},
     * and creates the {@link EventBroker} instance to be tested.
     */
    @BeforeEach
    void setup() {
        var services = new ServiceCollection();
        services.AddSingleton(SimpleEventSubscriber.class);
        eventSubscriberCollection.subscribe(SimpleEventSubscriber.class);
        serviceProvider = services.CreateServiceProvider();
        eventBroker = eventSubscriberCollection.CreateEventBroker(serviceProvider);
    }

    /**
     * Tests that an event with a custom data model as its payload is correctly deserialized
     * and handled by the subscriber method.
     */
    @Test
    void publishEventWithModel_ShouldCorrectlyHandleSumOperation() {
        var metadata = new EventMetadata("withModelSumEvent");
        var model = new Object[]{new SimpleSumEventModel(5, 6)};
        eventBroker.publish(metadata, gson.toJson(model));

        var subscriber = serviceProvider.GetService(SimpleEventSubscriber.class);
        assertEquals(11, subscriber.onWithModelSumEventResult);
    }

    /**
     * Tests that an event with primitive types as its payload is correctly mapped
     * to the parameters of the subscriber method.
     */
    @Test
    void publishEventWithoutModel_ShouldCorrectlySumParameters() {
        var metadata = new EventMetadata("withoutModelSumEvent");
        var model = new Object[]{5, 6};
        eventBroker.publish(metadata, gson.toJson(model));

        var subscriber = serviceProvider.GetService(SimpleEventSubscriber.class);
        assertEquals(11, subscriber.onWithoutModelSumEventResult);
    }

    /**
     * Tests that a single event can be handled by multiple subscriber methods,
     * and that all handlers are invoked correctly.
     */
    @Test
    void publishMultipleOperationsEvent_ShouldReturnAllResults() {
        var metadata = new EventMetadata("multipleEvent");
        var model = new Object[]{10, 5};
        eventBroker.publish(metadata, gson.toJson(model));

        var subscriber = serviceProvider.GetService(SimpleEventSubscriber.class);
        assertArrayEquals(new int[]{15, 5, 50, 2}, subscriber.multipleEventResult);
    }

    /**
     * Tests that the event handling mechanism is case-insensitive with respect to the event name
     * specified in the {@code @OnEvent} annotation.
     */
    @Test
    void publishEvent_ShouldIgnoreEventNameCase() {
        var metadata = new EventMetadata("iGnoReEveNtNameCaSe");
        var model = new Object[]{2};
        eventBroker.publish(metadata, gson.toJson(model));

        var subscriber = serviceProvider.GetService(SimpleEventSubscriber.class);
        assertEquals(2, subscriber.IgnoreEventNameCaseResult);
    }

    /**
     * Tests that the system does not throw an exception when an event is published for which
     * the subscribed handler method has a mismatched parameter list. This ensures robustness.
     */
    @Test
    void publishEventWithWrongSubscriber_ShouldNotThrowException() {
        var metadata = new EventMetadata("ignoreWrongSubscriberEvent");
        var model = new Object[]{2, 3};

        assertDoesNotThrow(() -> eventBroker.publish(metadata, gson.toJson(model)));
    }
}