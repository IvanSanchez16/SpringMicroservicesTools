package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.logger.LogConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public WebClientConfig() {
        // Se deja en blanco para crear beans necesarios
    }

    @Bean
    public static WebConsumerBeansRegistration webClientBeansRegistration(Environment environment, WebClient.Builder builder) {
        return new WebConsumerBeansRegistration(environment, builder);
    }

    @Bean
    public static WebClientConsumerBuilder webClientConsumerBuilder(LogConfig logConfig) {
        return new DefaultWebClientConsumerBuilder(logConfig.getTransactionHeader());
    }

}