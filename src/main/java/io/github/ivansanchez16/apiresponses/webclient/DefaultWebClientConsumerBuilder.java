package io.github.ivansanchez16.apiresponses.webclient;

import io.github.ivansanchez16.logger.LogMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class DefaultWebClientConsumerBuilder implements WebClientConsumerBuilder{

    private WebClient webClient;

    private LogMethods logMethods;
    private final List<Header> defaultHeaders = new ArrayList<>();
    private boolean throwWebClientExceptions = true;

    @Override
    public WebClientConsumerBuilder webClient(WebClient webClient) {
        this.webClient = webClient;
        throwWebClientExceptions = true;
        defaultHeaders.clear();
        return this;
    }

    @Override
    public WebClientConsumerBuilder addDefaultHeader(String headerName, String headerValue) {
        defaultHeaders.add( new Header(headerName, headerValue) );
        return this;
    }

    @Override
    public WebClientConsumerBuilder throwWebClientExceptions(boolean flag) {
        this.throwWebClientExceptions = flag;
        return this;
    }

    @Override
    public WebClientConsumerBuilder logErrors(LogMethods logMethods) {
        this.logMethods = logMethods;
        return this;
    }

    @Override
    public WebClientConsumer build() {
        if (webClient == null) {
            throw new IllegalArgumentException("You need to specify a WebClient instance to build a WebClientConsumer");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        defaultHeaders.forEach(header -> httpHeaders.add(header.getName(), header.getValue()));

        return new WebClientConsumer(webClient, throwWebClientExceptions, httpHeaders, logMethods);
    }
}
