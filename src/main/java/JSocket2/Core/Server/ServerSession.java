package JSocket2.Core.Server;

import JSocket2.Core.Session;
import JSocket2.Protocol.Authentication.UserIdentity;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a server-side session for a connected client. This class holds state
 * such as authorization status, subscribed user identities, and cryptographic keys.
 * It is managed by the {@link ServerSessionManager}.
 */
public class ServerSession extends Session {
    private final ServerSessionManager serverSessionManager;
    private final ClientHandler clientHandler;
    private final Map<String, UserIdentity> subscribedUsers = new ConcurrentHashMap<>();
    private UserIdentity activeUser = null;
    private boolean isAuthorized = false;

    /**
     * Constructs a new ServerSession.
     *
     * @param clientHandler       The handler for the client's connection.
     * @param serverSessionManager The manager that oversees this session.
     */
    public ServerSession(ClientHandler clientHandler, ServerSessionManager serverSessionManager) {
        super();
        this.clientHandler = clientHandler;
        this.serverSessionManager = serverSessionManager;
    }

    /**
     * Gets the associated client handler.
     * @return The {@link ClientHandler} for this session.
     */
    public ClientHandler getClientHandler(){
        return clientHandler;
    }

    /**
     * Subscribes a user identity to this session, marking the session as authorized.
     * @param user The user identity to subscribe.
     */
    public void subscribeUser(UserIdentity user) {
        if (subscribedUsers.put(user.getUserId(), user) == null) {
            serverSessionManager.indexSessionForUser(this, user.getUserId());
            setActiveUser(user);
        }
        isAuthorized = true;
    }

    /**
     * Unsubscribes a user from this session.
     * @param userId The ID of the user to unsubscribe.
     */
    public void unsubscribeUser(String userId) {
        if (subscribedUsers.remove(userId) != null) {
            serverSessionManager.deindexSessionForUser(this, userId);
        }
        if (activeUser != null && userId.equals(activeUser.getUserId())) {
            activeUser = null;
        }
        if (subscribedUsers.isEmpty()) {
            isAuthorized = false;
        }
    }

    /**
     * Closes the session, de-indexing all subscribed users.
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        for (var userId : subscribedUsers.keySet()) {
            serverSessionManager.deindexSessionForUser(this, userId);
        }
        subscribedUsers.clear();
        activeUser = null;
    }

    /**
     * Checks if the session is authorized.
     * @return {@code true} if authorized, otherwise {@code false}.
     */
    public boolean isAuthorized() {
        return isAuthorized;
    }

    /**
     * Sets the symmetric AES key for this session.
     * @param aesKey The {@link SecretKey} for encryption/decryption.
     */
    public void setAESKey(SecretKey aesKey) {
        super.aesKey = aesKey;
    }

    /**
     * Sets the currently active user for this session.
     * @param user The user identity to set as active.
     * @throws IllegalArgumentException if the user is not subscribed to this session.
     */
    public void setActiveUser(UserIdentity user) {
        if (!subscribedUsers.containsKey(user.getUserId())) {
            throw new IllegalArgumentException("User not subscribed to this session.");
        }
        this.activeUser = user;
    }

    /**
     * Gets the currently active user for this session.
     * @return The active {@link UserIdentity}.
     */
    public UserIdentity getActiveUser() {
        return this.activeUser;
    }

    /**
     * Gets the server session manager.
     * @return The {@link ServerSessionManager}.
     */
    public ServerSessionManager getServerSessionManager() {
        return serverSessionManager;
    }
}