package JSocket2.Core.Server;

import JSocket2.DI.ServiceCollection;
import JSocket2.DI.ServiceProvider;
import JSocket2.Protocol.Rpc.RpcControllerCollection;
import JSocket2.Protocol.Rpc.RpcDispatcher;
import JSocket2.Protocol.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main entry point for the JSocket2 server. This class is responsible for
 * initializing the server socket, accepting incoming client connections, and
 * creating a new {@link ClientHandler} for each connection.
 */
public class ServerApplication {
    private final int PORT;
    private final ServerSocket serverSocket;
    private final RpcDispatcher rpcDispatcher;
    public final ServiceProvider serviceProvider;
    final ServerSessionManager serverSessionManager;
    private final Map<UUID, CompletableFuture<Message>> pendingRequests;

    /**
     * Constructs the ServerApplication.
     *
     * @param port                  The port number to listen on.
     * @param rpcControllerCollection A collection of registered RPC controllers.
     * @param services              The dependency injection service collection.
     * @throws IOException if an error occurs while opening the server socket.
     */
    public ServerApplication(int port, RpcControllerCollection rpcControllerCollection, ServiceCollection services) throws IOException {
        this.PORT = port;
        this.serviceProvider = services.CreateServiceProvider();
        this.serverSocket = new ServerSocket(PORT);
        this.serverSessionManager =serviceProvider.GetService(ServerSessionManager.class);
        this.pendingRequests = new ConcurrentHashMap<>();
        this.rpcDispatcher = rpcControllerCollection.CreateRpcDispatcher(this.serviceProvider);

    }

    /**
     * Starts the server's main loop, which listens for and accepts client connections.
     * For each accepted connection, a new {@link ClientHandler} is created and started in a new thread.
     */
    public void Run() {
        try {
            System.out.println("Server run in " + InetAddress.getLocalHost().getHostAddress() + ":"+PORT);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(serviceProvider,socket, rpcDispatcher, serverSessionManager,pendingRequests);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            Close();
        }
        catch (Exception e) {
            Close();
        }
    }

    /**
     * Shuts down the server by closing the server socket.
     */
    public void Close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Gets the session manager that tracks all active client sessions.
     * @return The {@link ServerSessionManager} instance.
     */
    public ServerSessionManager getServerSessionManager() {
        return serverSessionManager;
    }
}