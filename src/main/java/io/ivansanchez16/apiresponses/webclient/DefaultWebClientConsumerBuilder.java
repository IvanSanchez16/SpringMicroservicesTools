package io.ivansanchez16.apiresponses.webclient;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

class DefaultWebClientConsumerBuilder implements WebClientConsumerBuilder{

    private WebClient webClient;
    private final List<Header> defaultHeaders = new ArrayList<>();
    private boolean throwWebClientExceptions = true;
    private boolean logErrors = false;

    @Override
    public WebClientConsumerBuilder webClient(WebClient webClient) {
        this.webClient = webClient;
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
    public WebClientConsumerBuilder logErrors(boolean flag) {
        this.logErrors = flag;
        return this;
    }

    @Override
    public WebClientConsumer build() {
        if (webClient == null) {
            throw new IllegalArgumentException("You need to specify a WebClient instance to build a WebClientConsumer");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        defaultHeaders.forEach(header -> httpHeaders.add(header.getName(), header.getValue()));

        return new WebClientConsumer(webClient, throwWebClientExceptions, httpHeaders, logErrors);
    }
}
