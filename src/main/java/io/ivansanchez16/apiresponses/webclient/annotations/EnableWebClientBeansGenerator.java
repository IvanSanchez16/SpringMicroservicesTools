package io.ivansanchez16.apiresponses.webclient.annotations;

import io.ivansanchez16.apiresponses.webclient.WebClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(WebClientConfig.class)
public @interface EnableWebClientBeansGenerator {
}
