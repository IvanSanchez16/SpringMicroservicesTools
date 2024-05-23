package io.ivansanchez16.apiresponses.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RequiredArgsConstructor
public class WebClientConsumer {

    private final WebClient webClient;

    private final boolean throwWebClientExceptions;
    private final HttpHeaders defaultHeaders;
    private final boolean logErrors;

    public String stringWithRequestParams(Map<String, Object> requestParams) {
        final StringBuilder sb = new StringBuilder();
        requestParams.forEach((key, object) -> {
            sb.append(key);
            sb.append("=");
            sb.append(object.toString());
            sb.append("&");
        });

        return sb.substring(0, sb.length()-1);
    }

    public Request getRequest(String uri) {
        final HttpHeaders headers = new HttpHeaders();

        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                logErrors,
                HttpMethod.GET,
                uri
        );
    }

    public Request postRequest(String uri) {
        final HttpHeaders headers = new HttpHeaders();

        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                logErrors,
                HttpMethod.POST,
                uri
        );
    }

    public Request patchRequest(String uri) {
        final HttpHeaders headers = new HttpHeaders();

        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                logErrors,
                HttpMethod.PATCH,
                uri
        );
    }

    public Request putRequest(String uri) {
        final HttpHeaders headers = new HttpHeaders();

        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                logErrors,
                HttpMethod.PUT,
                uri
        );
    }

    public Request deleteRequest(String uri) {
        final HttpHeaders headers = new HttpHeaders();

        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                logErrors,
                HttpMethod.DELETE,
                uri
        );
    }

}