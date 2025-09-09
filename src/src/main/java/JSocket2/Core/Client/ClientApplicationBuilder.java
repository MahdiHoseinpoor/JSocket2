package JSocket2.Core.Client;

import JSocket2.DI.ServiceCollection;
import JSocket2.Protocol.EventHub.EventSubscriberCollection;
import JSocket2.Protocol.IConnectionEventListener;
import java.util.function.Consumer;

public class ClientApplicationBuilder {
    private String host;
    private int port;
    private final ServiceCollection services;
    private final EventSubscriberCollection subscribers;
    private IConnectionEventListener connectionEventListener;
    private final ReconnectionOptions reconnectionOptions;

    public ClientApplicationBuilder(){
        services = new ServiceCollection();
        subscribers = new EventSubscriberCollection();
        reconnectionOptions = new ReconnectionOptions();
    }

    ClientApplicationBuilder setConnectionEventListener(IConnectionEventListener connectionEventListener){
        this.connectionEventListener = connectionEventListener;
        return this;
    }

    public ClientApplicationBuilder setEndpoint(String host,int port){
        this.host = host;
        this.port = port;
        return this;
    }

    public ClientApplicationBuilder withReconnectionOptions(Consumer<ReconnectionOptions> optionsConsumer) {
        optionsConsumer.accept(this.reconnectionOptions);
        return this;
    }

    public ServiceCollection getServices(){
        return services;
    }

    public ClientApplicationBuilder addEventSubscriber(Class<?> subscribeType){
        services.AddSingleton(subscribeType);
        subscribers.subscribe(subscribeType);
        return this;
    }

    public ClientApplication Build(){
        return new ClientApplication(host, port, connectionEventListener, subscribers, services, reconnectionOptions);
    }
}