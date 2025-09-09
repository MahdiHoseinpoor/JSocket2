package JSocket2.Protocol.Rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method within an {@link RpcController} as an invokable action.
 * The {@link RpcDispatcher} uses this to identify methods that can be called remotely.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcAction {
    /**
     * Specifies a public name for the action. If not set, the method's name is used by default.
     * This allows for decoupling the public-facing API name from the internal method name.
     *
     * @return The public name of the RPC action.
     */
    String Name() default "";
}