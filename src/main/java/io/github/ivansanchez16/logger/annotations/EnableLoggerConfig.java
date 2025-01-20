package io.github.ivansanchez16.logger.annotations;

import io.github.ivansanchez16.logger.LogConfig;
import io.github.ivansanchez16.logger.LoggerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({LogConfig.class, LoggerConfig.class})
public @interface EnableLoggerConfig {
}
