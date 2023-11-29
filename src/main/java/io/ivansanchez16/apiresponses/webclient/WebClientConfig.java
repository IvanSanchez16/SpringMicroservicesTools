package io.ivansanchez16.apiresponses.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public static WebConsumerBeansRegistration webClientBeansRegistration(Environment environment, WebClient.Builder builder) {
        return new WebConsumerBeansRegistration(environment, builder);
    }

}