package JSocket2.Core.Server;

import JSocket2.Cryptography.RsaKeyManager;
import JSocket2.DI.ServiceCollection;
import JSocket2.Protocol.Authentication.IAuthService;
import JSocket2.Protocol.Rpc.RpcControllerCollection;

import java.io.IOException;

/**
 * A builder for creating and configuring a {@link ServerApplication} instance.
 * Provides a fluent API for setting the port, registering services, controllers,
 * and lifecycle listeners.
 */
public class ServerApplicationBuilder {
    private int port = 8080;
    private final RpcControllerCollection rpcControllerCollection;
    private final ServiceCollection services;
    private boolean setAuth = false;
    private Class<? extends IClientLifecycleListener> clientLifecycleListenerType;

    /**
     * Constructs a new ServerApplicationBuilder with default services.
     */
    public ServerApplicationBuilder(){
        services = new ServiceCollection();
        rpcControllerCollection = new RpcControllerCollection();
        services.AddSingleton(ServerSessionManager.class);
        services.AddSingletonWithInstance(RsaKeyManager.class,new RsaKeyManager());
    }

    /**
     * Sets the port on which the server will listen.
     *
     * @param port The port number.
     * @return This builder instance for chaining.
     */
    public ServerApplicationBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the implementation for the client lifecycle listener.
     *
     * @param listenerType The class that implements {@link IClientLifecycleListener}.
     * @return This builder instance for chaining.
     */
    public ServerApplicationBuilder setClientLifecycleListener(Class<? extends IClientLifecycleListener> listenerType) {
        this.clientLifecycleListenerType = listenerType;
        services.AddSingleton(IClientLifecycleListener.class, listenerType);
        return this;
    }

    /**
     * Gets the service collection for registering custom dependencies.
     *
     * @return The {@link ServiceCollection} instance.
     */
    public ServiceCollection getServices(){
        return services;
    }

    /**
     * Sets the authentication service implementation.
     *
     * @param authService The class that implements {@link IAuthService}.
     * @return This builder instance for chaining.
     */
    public ServerApplicationBuilder setAuthService(Class<?> authService) {
        services.AddScoped(IAuthService.class,authService);
        setAuth = true;
        return this;
    }

    /**
     * Adds an RPC controller to the application.
     *
     * @param controllerType The class type of the controller.
     * @param <T>            The type of the controller.
     * @return This builder instance for chaining.
     */
    public <T> ServerApplicationBuilder addController(Class<T> controllerType) {
        this.services.AddScoped(controllerType);
        this.rpcControllerCollection.registerController(controllerType);
        return this;
    }

    /**
     * Builds and returns a new {@link ServerApplication} with the specified configuration.
     *
     * @return A configured {@link ServerApplication} instance.
     * @throws IOException if the server socket cannot be created.
     * @throws RuntimeException if the prerequisite configurations (like auth service) are not met.
     */
    public ServerApplication build() throws IOException {
        if(!canBuild()){
            throw new RuntimeException("Can't build ServerApplication");
        }
        return new ServerApplication(port, rpcControllerCollection,services);
    }
    private boolean canBuild(){
        return setAuth && port > 1023 && port < 49151;
    }
}