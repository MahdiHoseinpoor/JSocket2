package JSocket2.Protocol.Rpc;

import JSocket2.Core.Server.ServerSessionManager;
import JSocket2.Protocol.Authentication.UserIdentity;
import JSocket2.Protocol.StatusCode;

/**
 * An abstract base class for all RPC controllers. It provides convenience methods
 * for creating standard RPC responses and gives access to the current user's identity
 * and the server session manager.
 */
public abstract class RpcControllerBase {
    private UserIdentity currentUser = null;
    private ServerSessionManager serverSessionManager = null;

    /**
     * Gets the identity of the user making the current request.
     *
     * @return The {@link UserIdentity} of the current user.
     */
    protected UserIdentity getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the identity of the user for the current request context.
     * This is typically called by the {@link RpcDispatcher}.
     *
     * @param currentUser The user's identity.
     */
    protected void setCurrentUser(UserIdentity currentUser) {
        if (this.currentUser == null) {
            this.currentUser = currentUser;
        }
    }

    /**
     * Gets the server session manager, which can be used to interact with client sessions.
     *
     * @return The {@link ServerSessionManager}.
     */
    protected ServerSessionManager getServerSessionManager() {
        return serverSessionManager;
    }

    /**
     * Sets the server session manager for the current request context.
     * This is typically called by the {@link RpcDispatcher}.
     *
     * @param serverSessionManager The server session manager.
     */
    protected void setServerSessionManager(ServerSessionManager serverSessionManager) {
        this.serverSessionManager = serverSessionManager;
    }

    /**
     * Creates a generic {@link RpcResponse}.
     *
     * @param statusCode The status code for the response.
     * @param message    A descriptive message.
     * @param content    The payload of the response.
     * @param <T>        The type of the payload.
     * @return A new {@link RpcResponse}.
     */
    protected final <T> RpcResponse<T> response(StatusCode statusCode, String message, T content) {
        return new RpcResponse<>(statusCode, message, content);
    }

    /**
     * Creates a generic {@link RpcResponse} with a null message.
     *
     * @param statusCode The status code for the response.
     * @param content    The payload of the response.
     * @param <T>        The type of the payload.
     * @return A new {@link RpcResponse}.
     */
    protected final <T> RpcResponse<T> response(StatusCode statusCode, T content) {
        return new RpcResponse<>(statusCode, null, content);
    }

    /**
     * Creates a 404 Not Found response.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#NOT_FOUND}.
     */
    protected final RpcResponse<Object> NotFound() {
        return response(StatusCode.NOT_FOUND, null);
    }


    /**
     * Creates a 400 Bad Request response.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#BAD_REQUEST}.
     */
    protected final RpcResponse<Object> BadRequest() {
        return response(StatusCode.BAD_REQUEST, null);
    }

    /**
     * Creates a 400 Bad Request response with a message.
     *
     * @param message A message explaining why the request was bad.
     * @return An {@link RpcResponse} with a {@link StatusCode#BAD_REQUEST}.
     */
    protected final RpcResponse<Object> BadRequest(String message) {
        return response(StatusCode.BAD_REQUEST, message, null);
    }

    /**
     * Creates a 403 Forbidden response.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#FORBIDDEN}.
     */
    protected final RpcResponse<Object> Forbidden() {
        return response(StatusCode.FORBIDDEN, null);
    }

    /**
     * Creates a 403 Forbidden response with a message.
     *
     * @param message A message explaining why the request is forbidden.
     * @return An {@link RpcResponse} with a {@link StatusCode#FORBIDDEN}.
     */
    protected final RpcResponse<Object> Forbidden(String message) {
        return response(StatusCode.FORBIDDEN, message, null);
    }

    /**
     * Creates a 200 OK response with a null payload.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final RpcResponse<Object> Ok() {
        return response(StatusCode.OK, null);
    }

    /**
     * Creates a 200 OK response with a payload.
     *
     * @param content The payload to include in the response.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final <T> RpcResponse<T> Ok(T content) {
        return response(StatusCode.OK, content);
    }
}