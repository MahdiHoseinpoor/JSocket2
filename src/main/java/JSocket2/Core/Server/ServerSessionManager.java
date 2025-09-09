package JSocket2.Core.Server;

import JSocket2.Protocol.Message;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all active {@link ServerSession} instances on the server.
 * It is responsible for creating, tracking, and removing sessions. It also provides
 * a mechanism to index sessions by user ID for efficient message publishing.
 */
public class ServerSessionManager {
    private final Map<ClientHandler, ServerSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CopyOnWriteArrayList<ServerSession>> userSessions = new ConcurrentHashMap<>();

    /**
     * Creates a new session for a client and adds it to the manager.
     * @param clientHandler The handler for the client's connection.
     * @return The newly created {@link ServerSession}.
     */
    public ServerSession createSession(ClientHandler clientHandler) {
        ServerSession serverSession = new ServerSession(clientHandler,this);
        sessions.put(clientHandler, serverSession);
        return serverSession;
    }

    /**
     * Retrieves the session associated with a specific client handler.
     * @param clientHandler The client handler.
     * @return The associated {@link ServerSession}, or {@code null} if not found.
     */
    public ServerSession getSession(ClientHandler clientHandler) {
        return sessions.get(clientHandler);
    }

    /**
     * Removes a session from the manager.
     * @param clientHandler The handler of the session to remove.
     */
    public void removeSession(ClientHandler clientHandler) {
        sessions.remove(clientHandler);
    }

    /**
     * Closes and removes all active sessions.
     * @throws IOException if an I/O error occurs.
     */
    public void closeAll() throws IOException {
        for(var socket: sessions.keySet()){
            removeSession(socket);
        }
    }

    void indexSessionForUser(ServerSession sess, String userId) {
        userSessions
                .computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>())
                .add(sess);
    }

    void deindexSessionForUser(ServerSession sess, String userId) {
        var list = userSessions.get(userId);
        if (list != null) {
            list.remove(sess);
            if (list.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    /**
     * Publishes a message to all active sessions for a specific user.
     * @param userId The ID of the target user.
     * @param msg    The {@link Message} to send.
     */
    public void publishMessage(String userId, Message msg) {
        var list = userSessions.get(userId);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                var sess = list.get(i);
                try {
                    sess.getClientHandler().send(msg);
                } catch (SocketException e) {
                    sessions.remove(sess);
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Checks if a user has at least one active session.
     * @param userId The ID of the user to check.
     * @return {@code true} if the user is online, otherwise {@code false}.
     */
    public boolean isUserOnline(String userId) {
        if (userId == null) return false;
        var list = userSessions.get(userId);
        return list != null && !list.isEmpty();
    }
}