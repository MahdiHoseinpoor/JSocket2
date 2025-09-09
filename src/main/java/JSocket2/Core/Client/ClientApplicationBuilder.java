package JSocket2.Core.Client;

import JSocket2.DI.ServiceCollection;
import JSocket2.Protocol.EventHub.EventSubscriberCollection;
import JSocket2.Protocol.IConnectionEventListener;
import java.util.function.Consumer;

/**
 * A builder for creating and configuring a {@link ClientApplication} instance.
 * This class provides a fluent API for setting up the client's endpoint,
 * services, event subscribers, and reconnection behavior.
 */
public class ClientApplicationBuilder {
    private String host;
    private int port;
    private final ServiceCollection services;
    private final EventSubscriberCollection subscribers;
    private IConnectionEventListener connectionEventListener;
    private final ReconnectionOptions reconnectionOptions;

    /**
     * Constructs a new ClientApplicationBuilder with default configurations.
     */
    public ClientApplicationBuilder(){
        services = new ServiceCollection();
        subscribers = new EventSubscriberCollection();
        reconnectionOptions = new ReconnectionOptions();
    }

    ClientApplicationBuilder setConnectionEventListener(IConnectionEventListener connectionEventListener){
        this.connectionEventListener = connectionEventListener;
        return this;
    }

    /**
     * Sets the server endpoint for the client to connect to.
     *
     * @param host The server's hostname or IP address.
     * @param port The server's port number.
     * @return This builder instance for chaining.
     */
    public ClientApplicationBuilder setEndpoint(String host,int port){
        this.host = host;
        this.port = port;
        return this;
    }

    /**
     * Configures the client's automatic reconnection behavior.
     *
     * @param optionsConsumer A consumer that receives a {@link ReconnectionOptions}
     *                        instance to configure.
     * @return This builder instance for chaining.
     */
    public ClientApplicationBuilder withReconnectionOptions(Consumer<ReconnectionOptions> optionsConsumer) {
        optionsConsumer.accept(this.reconnectionOptions);
        return this;
    }

    /**
     * Gets the service collection for registering dependencies.
     *
     * @return The {@link ServiceCollection} instance.
     */
    public ServiceCollection getServices(){
        return services;
    }

    /**
     * Registers a class as an event subscriber. The class will also be registered
     * as a singleton service in the DI container.
     *
     * @param subscribeType The class type of the event subscriber.
     * @return This builder instance for chaining.
     */
    public ClientApplicationBuilder addEventSubscriber(Class<?> subscribeType){
        services.AddSingleton(subscribeType);
        subscribers.subscribe(subscribeType);
        return this;
    }

    /**
     * Builds and returns a new {@link ClientApplication} with the specified configuration.
     *
     * @return A configured {@link ClientApplication} instance.
     */
    public ClientApplication Build(){
        return new ClientApplication(host, port, connectionEventListener, subscribers, services, reconnectionOptions);
    }
}