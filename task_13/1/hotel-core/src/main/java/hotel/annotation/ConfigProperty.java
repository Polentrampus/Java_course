package hotel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
    String configFileName() default "hotel.properties";
    String propertyName() default "";
    Class<?> valueType() default String.class;
}
