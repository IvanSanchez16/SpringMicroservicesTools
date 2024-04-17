package io.ivansanchez16.apiresponses.webclient;

import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@UtilityClass
public class WebClientConfig {

    @Bean
    public static WebConsumerBeansRegistration webClientBeansRegistration(Environment environment, WebClient.Builder builder) {
        return new WebConsumerBeansRegistration(environment, builder);
    }

    @Bean
    public static WebClientConsumerBuilder webClientConsumerBuilder() {
        return new DefaultWebClientConsumerBuilder();
    }

}