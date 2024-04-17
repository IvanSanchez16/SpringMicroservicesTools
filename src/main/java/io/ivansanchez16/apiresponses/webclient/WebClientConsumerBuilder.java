package io.ivansanchez16.apiresponses.webclient;

import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientConsumerBuilder {
    
    WebClientConsumerBuilder webClient(WebClient webClient);
    
    WebClientConsumerBuilder addDefaultHeader(String headerName, String headerValue);

    WebClientConsumerBuilder throwWebClientExceptions(boolean flag);

    WebClientConsumerBuilder logErrors(boolean flag);

    WebClientConsumer build();
}
