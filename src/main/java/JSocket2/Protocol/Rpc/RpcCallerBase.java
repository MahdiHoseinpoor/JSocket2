package JSocket2.Protocol.Rpc;

import JSocket2.Core.Client.ClientApplication;
import JSocket2.Protocol.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A base class for client-side RPC callers. It provides the core functionality
 * for creating and sending RPC messages to the server.
 */
public class RpcCallerBase {
    private final ClientApplication clientApplication;
    protected final Gson gson;

    /**
     * Constructs an {@code RpcCallerBase} with a default {@link Gson} instance.
     *
     * @param clientApplication The client's app
     */
    public RpcCallerBase(ClientApplication clientApplication) {
        this(clientApplication, new Gson());
    }

    /**
     * Constructs an {@code RpcCallerBase} with a custom {@link Gson} instance.
     *
     * @param clientApplication The client's app
     * @param gson              The Gson instance to use for JSON serialization.
     */
    public RpcCallerBase(ClientApplication clientApplication, Gson gson) {
        this.clientApplication = clientApplication;
        this.gson = gson;
    }

    /**
     * Invokes a remote procedure without waiting for a response (fire-and-forget).
     *
     * @param controllerName The name of the target controller.
     * @param actionName     The name of the target action method.
     * @param payloadObjects The arguments to be passed to the remote method.
     * @throws IOException If a network error occurs while sending the message.
     */
    protected void callRpc(String controllerName, String actionName, Object... payloadObjects) throws IOException {
        UUID requestId = UUID.randomUUID();
        Message message = createRpcCallMessage(controllerName, actionName, payloadObjects, requestId);
        clientApplication.getMessageHandler().write(message);
    }

    /**
     * Invokes a remote procedure and waits for a response containing a single object.
     *
     * @param controllerName The name of the target controller.
     * @param actionName     The name of the target action method.
     * @param responseClass  The class of the expected response payload.
     * @param payloadObjects The arguments to be passed to the remote method.
     * @param <T>            The type of the expected response payload.
     * @return An {@link RpcResponse} containing the result from the server.
     * @throws IOException If a network error occurs while sending the message.
     */
    protected <T> RpcResponse<T> callRpcAndGetResponse(String controllerName, String actionName, Class<T> responseClass, Object... payloadObjects) throws IOException {
        UUID requestId = UUID.randomUUID();
        Message message = createRpcCallMessage(controllerName, actionName, payloadObjects, requestId);
        CompletableFuture<Message> future = new CompletableFuture<>();
        clientApplication.getPendingRequests().put(requestId, future);
        clientApplication.getMessageHandler().write(message);
        Message responseMessage = future.join();
        RpcResponse<T> response = RpcHelper.convertMessageToRpcResponse(responseMessage, responseClass);
        if (response.getStatusCode() != StatusCode.OK) {
            System.out.println("Error Code: " + response.getStatusCode() + " " + response.getMessage());
        }
        return response;
    }

    /**
     * Invokes a remote procedure and waits for a response containing a list of objects.
     *
     * @param controllerName The name of the target controller.
     * @param actionName     The name of the target action method.
     * @param responseClass  The class of the elements in the expected response list.
     * @param payloadObjects The arguments to be passed to the remote method.
     * @param <T>            The generic type of the elements in the list.
     * @return An {@link RpcResponse} containing the list result from the server.
     * @throws IOException If a network error occurs while sending the message.
     */
    protected <T> RpcResponse<List<T>> callRpcAndGetListResponse(String controllerName, String actionName, Class<T> responseClass, Object... payloadObjects) throws IOException {
        UUID requestId = UUID.randomUUID();
        Message message = createRpcCallMessage(controllerName, actionName, payloadObjects, requestId);
        CompletableFuture<Message> future = new CompletableFuture<>();
        clientApplication.getPendingRequests().put(requestId, future);
        clientApplication.getMessageHandler().write(message);
        Message responseMessage = future.join();
        RpcResponse<List<T>> response = RpcHelper.convertMessageToRpcListResponse(responseMessage, responseClass);
        if (response.getStatusCode() != StatusCode.OK) {
            System.out.println("Error Code: " + response.getStatusCode() + " " + response.getMessage());
        }
        return response;
    }

    /**
     * Creates a {@link Message} object for an RPC call.
     *
     * @param controllerName The name of the target controller.
     * @param actionName     The name of the target action.
     * @param payloadObjects The arguments for the action.
     * @param requestId      The unique ID for this request.
     * @return A {@link Message} ready to be sent to the server.
     * @throws IOException If an error occurs during message creation.
     */
    private Message createRpcCallMessage(String controllerName, String actionName, Object[] payloadObjects, UUID requestId) throws IOException {
        RpcCallMetadata metadata = new RpcCallMetadata(controllerName, actionName);
        String metadataJson = gson.toJson(metadata);
        String payloadJson = gson.toJson(payloadObjects);
        byte[] metadataBytes = metadataJson.getBytes(StandardCharsets.UTF_8);
        byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
        MessageHeader header = MessageHeader.BuildRpcCallHeader(
                requestId, true, metadataBytes.length, payloadBytes.length
        );
        return new Message(header, metadataBytes, payloadBytes);
    }
}