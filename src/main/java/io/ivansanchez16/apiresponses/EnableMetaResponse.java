package io.ivansanchez16.apiresponses;

import io.ivansanchez16.logger.annotations.EnableLoggerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableLoggerConfig
@Import({EnvironmentConfig.class, MetaResponseConfig.class})
public @interface EnableMetaResponse {
}
