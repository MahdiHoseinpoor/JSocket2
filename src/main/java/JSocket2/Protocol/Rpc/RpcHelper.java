package JSocket2.Protocol.Rpc;

import JSocket2.Protocol.Message;
import JSocket2.Protocol.MessageType;
import JSocket2.Protocol.StatusCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A utility class to help with converting raw {@link Message} objects into structured {@link RpcResponse} objects.
 */
public class RpcHelper {
    private static final Gson gson = new Gson();

    /**
     * Converts a {@link Message} into an {@link RpcResponse} where the payload is a {@link List}.
     *
     * @param message      The message received from the server.
     * @param elementClass The class of the elements within the list.
     * @param <T>          The generic type of the list elements.
     * @return A deserialized {@link RpcResponse} containing a list payload.
     */
    public static <T> RpcResponse<List<T>> convertMessageToRpcListResponse(Message message, Class<T> elementClass) {
        Type listType = TypeToken.getParameterized(List.class, elementClass).getType();
        @SuppressWarnings("unchecked")
        RpcResponse<List<T>> response = (RpcResponse<List<T>>) convertMessageToRpcResponse(message, listType);
        return response;
    }

    /**
     * Converts a {@link Message} into an {@link RpcResponse} with a single object payload.
     *
     * @param message       The message received from the server.
     * @param responseClass The class of the expected payload.
     * @param <T>           The type of the payload.
     * @return A deserialized {@link RpcResponse}.
     */
    public static <T> RpcResponse<T> convertMessageToRpcResponse(Message message, Class<T> responseClass) {
        @SuppressWarnings("unchecked")
        RpcResponse<T> response = (RpcResponse<T>) convertMessageToRpcResponse(message, (Type) responseClass);
        return response;
    }

    /**
     * Core conversion logic that deserializes a message's metadata and payload into an {@link RpcResponse}.
     *
     * @param message       The raw message.
     * @param responseType  The {@link Type} of the expected payload.
     * @return A deserialized {@link RpcResponse}.
     * @throws RuntimeException if the message is not of type {@link MessageType#RPC_RESPONSE}.
     */
    private static RpcResponse<?> convertMessageToRpcResponse(Message message, Type responseType) {
        if (message.header.type != MessageType.RPC_RESPONSE) {
            throw new RuntimeException("Invalid message type for RPC response conversion.");
        }
        RpcResponseMetadata metaObj = gson.fromJson(
                new String(message.getMetadata(), StandardCharsets.UTF_8),
                RpcResponseMetadata.class
        );
        Object result = gson.fromJson(
                new String(message.getPayload(), StandardCharsets.UTF_8),
                responseType
        );
        return new RpcResponse<>(StatusCode.fromCode(metaObj.getStatusCode()), metaObj.getMessage(), result);
    }
}