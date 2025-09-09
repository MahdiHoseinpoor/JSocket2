package JSocket2.Protocol.Rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an RPC controller, making it discoverable by the {@link RpcDispatcher}.
 * All public methods within a controller are potential RPC actions.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcController {
    /**
     * Specifies a public name for the controller. If not set, the class's simple name is used.
     * This allows the public API name to be different from the class name.
     *
     * @return The public name of the controller.
     */
    String Name() default "";
}