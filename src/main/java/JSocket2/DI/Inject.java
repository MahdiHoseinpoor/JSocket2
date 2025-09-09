package JSocket2.DI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the constructor to be used by the {@link ServiceProvider} for dependency injection.
 * When a class has multiple constructors, this annotation marks the one that the DI container should use to create an instance.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}