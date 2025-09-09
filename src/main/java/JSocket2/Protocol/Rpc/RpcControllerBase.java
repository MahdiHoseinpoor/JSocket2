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
    // Potentially add an RpcRequest object here if you have one,
    // to give controllers access to request-specific data like parameters, headers, etc.
    // private RpcRequest currentRequest = null;

    /**
     * Gets the identity of the user making the current request.
     *
     * @return The {@link UserIdentity} of the current user. Returns null if no user is authenticated.
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
        // Allow setting it only once per request context, or clear it if null is passed.
        // For simplicity, we'll keep the current behavior of setting only if null.
        if (this.currentUser == null) {
            this.currentUser = currentUser;
        }
    }

    /**
     * Gets the server session manager, which can be used to interact with client sessions.
     *
     * @return The {@link ServerSessionManager}. Returns null if not set.
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
        // Similar to currentUser, you might want to ensure this is set only once or handle null clearing.
        if (this.serverSessionManager == null) {
            this.serverSessionManager = serverSessionManager;
        }
    }

    // --- Core Response Builders ---

    /**
     * Creates a generic {@link RpcResponse}.
     *
     * @param statusCode The status code for the response.
     * @param message    A descriptive message. Can be null.
     * @param content    The payload of the response. Can be null.
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
     * @param content    The payload of the response. Can be null.
     * @param <T>        The type of the payload.
     * @return A new {@link RpcResponse}.
     */
    protected final <T> RpcResponse<T> response(StatusCode statusCode, T content) {
        return new RpcResponse<>(statusCode, null, content);
    }

    /**
     * Creates a generic {@link RpcResponse} with a null content.
     *
     * @param statusCode The status code for the response.
     * @param message    A descriptive message. Can be null.
     * @return A new {@link RpcResponse}.
     */
    protected final RpcResponse<Object> response(StatusCode statusCode, String message) {
        return new RpcResponse<>(statusCode, message, null);
    }

    // --- Success Responses (2xx) ---

    /**
     * Creates a 200 OK response with a null payload and no message.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final RpcResponse<Object> Ok() {
        return response(StatusCode.OK, null);
    }

    /**
     * Creates a 200 OK response with a payload and no message.
     *
     * @param content The payload to include in the response.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final <T> RpcResponse<T> Ok(T content) {
        return response(StatusCode.OK, content);
    }

    /**
     * Creates a 200 OK response with a message and a payload.
     *
     * @param message A message to include in the response.
     * @param content The payload to include in the response.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final <T> RpcResponse<T> Ok(String message, T content) {
        return response(StatusCode.OK, message, content);
    }

    /**
     * Creates a 200 OK response with a message and a null payload.
     *
     * @param message A message to include in the response.
     * @return An {@link RpcResponse} with a {@link StatusCode#OK}.
     */
    protected final RpcResponse<Object> Ok(String message) {
        return response(StatusCode.OK, message, null);
    }

    /**
     * Creates a 201 Created response with a null payload and no message.
     * Typically used after a resource has been successfully created.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#CREATED}.
     */
    protected final RpcResponse<Object> Created() {
        return response(StatusCode.CREATED, null);
    }

    /**
     * Creates a 201 Created response with a payload and no message.
     *
     * @param content The payload of the newly created resource.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#CREATED}.
     */
    protected final <T> RpcResponse<T> Created(T content) {
        return response(StatusCode.CREATED, content);
    }

    /**
     * Creates a 201 Created response with a message and a payload.
     *
     * @param message A message about the created resource.
     * @param content The payload of the newly created resource.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#CREATED}.
     */
    protected final <T> RpcResponse<T> Created(String message, T content) {
        return response(StatusCode.CREATED, message, content);
    }

    /**
     * Creates a 202 Accepted response with a null payload and no message.
     * Indicates that the request has been accepted for processing, but the processing is not yet complete.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#ACCEPTED}.
     */
    protected final RpcResponse<Object> Accepted() {
        return response(StatusCode.ACCEPTED, null);
    }

    /**
     * Creates a 202 Accepted response with a payload and no message.
     *
     * @param content Optional content about the accepted request.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#ACCEPTED}.
     */
    protected final <T> RpcResponse<T> Accepted(T content) {
        return response(StatusCode.ACCEPTED, content);
    }

    /**
     * Creates a 202 Accepted response with a message and a null payload.
     *
     * @param message A message indicating the request has been accepted.
     * @return An {@link RpcResponse} with a {@link StatusCode#ACCEPTED}.
     */
    protected final RpcResponse<Object> Accepted(String message) {
        return response(StatusCode.ACCEPTED, message, null);
    }


    /**
     * Creates a 204 No Content response.
     * Indicates that the server has successfully fulfilled the request and there is no additional content to send in the response payload body.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#NO_CONTENT}.
     */
    protected final RpcResponse<Object> NoContent() {
        return response(StatusCode.NO_CONTENT, null);
    }

    // --- Client Error Responses (4xx) ---

    /**
     * Creates a 400 Bad Request response with a null message and payload.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#BAD_REQUEST}.
     */
    protected final RpcResponse<Object> BadRequest() {
        return response(StatusCode.BAD_REQUEST, null);
    }

    /**
     * Creates a 400 Bad Request response with a message and a null payload.
     *
     * @param message A message explaining why the request was bad.
     * @return An {@link RpcResponse} with a {@link StatusCode#BAD_REQUEST}.
     */
    protected final RpcResponse<Object> BadRequest(String message) {
        return response(StatusCode.BAD_REQUEST, message, null);
    }

    /**
     * Creates a 400 Bad Request response with a message and a payload.
     *
     * @param message A message explaining why the request was bad.
     * @param content Additional details about the bad request.
     * @param <T>     The type of the payload.
     * @return An {@link RpcResponse} with a {@link StatusCode#BAD_REQUEST}.
     */
    protected final <T> RpcResponse<T> BadRequest(String message, T content) {
        return response(StatusCode.BAD_REQUEST, message, content);
    }

    /**
     * Creates a 401 Unauthorized response with a null message and payload.
     * The request requires user authentication.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#UNAUTHORIZED}.
     */
    protected final RpcResponse<Object> Unauthorized() {
        return response(StatusCode.UNAUTHORIZED, null);
    }

    /**
     * Creates a 401 Unauthorized response with a message and a null payload.
     *
     * @param message A message explaining why the request is unauthorized.
     * @return An {@link RpcResponse} with a {@link StatusCode#UNAUTHORIZED}.
     */
    protected final RpcResponse<Object> Unauthorized(String message) {
        return response(StatusCode.UNAUTHORIZED, message, null);
    }

    /**
     * Creates a 403 Forbidden response with a null message and payload.
     * The server understood the request but refuses to authorize it.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#FORBIDDEN}.
     */
    protected final RpcResponse<Object> Forbidden() {
        return response(StatusCode.FORBIDDEN, null);
    }

    /**
     * Creates a 403 Forbidden response with a message and a null payload.
     *
     * @param message A message explaining why the request is forbidden.
     * @return An {@link RpcResponse} with a {@link StatusCode#FORBIDDEN}.
     */
    protected final RpcResponse<Object> Forbidden(String message) {
        return response(StatusCode.FORBIDDEN, message, null);
    }

    /**
     * Creates a 404 Not Found response with a null message and payload.
     * The requested resource could not be found.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#NOT_FOUND}.
     */
    protected final RpcResponse<Object> NotFound() {
        return response(StatusCode.NOT_FOUND, null);
    }

    /**
     * Creates a 404 Not Found response with a message and a null payload.
     *
     * @param message A message explaining why the resource was not found.
     * @return An {@link RpcResponse} with a {@link StatusCode#NOT_FOUND}.
     */
    protected final RpcResponse<Object> NotFound(String message) {
        return response(StatusCode.NOT_FOUND, message, null);
    }

    /**
     * Creates a 409 Conflict response with a null message and payload.
     * Indicates a request conflict with the current state of the target resource.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#CONFLICT}.
     */
    protected final RpcResponse<Object> Conflict() {
        return response(StatusCode.CONFLICT, null);
    }

    /**
     * Creates a 409 Conflict response with a message and a null payload.
     *
     * @param message A message explaining the conflict.
     * @return An {@link RpcResponse} with a {@link StatusCode#CONFLICT}.
     */
    protected final RpcResponse<Object> Conflict(String message) {
        return response(StatusCode.CONFLICT, message, null);
    }

    /**
     * Creates a 412 Precondition Failed response with a null message and payload.
     * The client has provided conditions in the request headers which the server could not meet.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#PRECONDITION_FAILED}.
     */
    protected final RpcResponse<Object> PreconditionFailed() {
        return response(StatusCode.PRECONDITION_FAILED, null);
    }

    /**
     * Creates a 412 Precondition Failed response with a message and a null payload.
     *
     * @param message A message explaining why the precondition failed.
     * @return An {@link RpcResponse} with a {@link StatusCode#PRECONDITION_FAILED}.
     */
    protected final RpcResponse<Object> PreconditionFailed(String message) {
        return response(StatusCode.PRECONDITION_FAILED, message, null);
    }

    // --- Server Error Responses (5xx) ---

    /**
     * Creates a 500 Internal Server Error response with a null message and payload.
     * A generic error message, given when an unexpected condition was encountered.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#INTERNAL_SERVER_ERROR}.
     */
    protected final RpcResponse<Object> InternalServerError() {
        return response(StatusCode.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * Creates a 500 Internal Server Error response with a message and a null payload.
     *
     * @param message A message providing details about the internal server error.
     * @return An {@link RpcResponse} with a {@link StatusCode#INTERNAL_SERVER_ERROR}.
     */
    protected final RpcResponse<Object> InternalServerError(String message) {
        return response(StatusCode.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * Creates a 501 Not Implemented response with a null message and payload.
     * The server does not support the functionality required to fulfill the request.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#NOT_IMPLEMENTED}.
     */
    protected final RpcResponse<Object> NotImplemented() {
        return response(StatusCode.NOT_IMPLEMENTED, null);
    }

    /**
     * Creates a 501 Not Implemented response with a message and a null payload.
     *
     * @param message A message explaining that the functionality is not implemented.
     * @return An {@link RpcResponse} with a {@link StatusCode#NOT_IMPLEMENTED}.
     */
    protected final RpcResponse<Object> NotImplemented(String message) {
        return response(StatusCode.NOT_IMPLEMENTED, message, null);
    }

    /**
     * Creates a 503 Service Unavailable response with a null message and payload.
     * The server is currently unable to handle the request due to temporary overload or maintenance.
     *
     * @return An {@link RpcResponse} with a {@link StatusCode#SERVICE_UNAVAILABLE}.
     */
    protected final RpcResponse<Object> ServiceUnavailable() {
        return response(StatusCode.SERVICE_UNAVAILABLE, null);
    }

    /**
     * Creates a 503 Service Unavailable response with a message and a null payload.
     *
     * @param message A message explaining why the service is unavailable.
     * @return An {@link RpcResponse} with a {@link StatusCode#SERVICE_UNAVAILABLE}.
     */
    protected final RpcResponse<Object> ServiceUnavailable(String message) {
        return response(StatusCode.SERVICE_UNAVAILABLE, message, null);
    }
}