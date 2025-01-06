package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.logger.LogMethods;
import io.ivansanchez16.logger.classes.ClientInfo;
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
    private final LogMethods logMethods;

    private final String transactionHeader;

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
        return genericRequest(uri, HttpMethod.GET);
    }

    public Request postRequest(String uri) {
        return genericRequest(uri, HttpMethod.POST);
    }

    public Request patchRequest(String uri) {
        return genericRequest(uri, HttpMethod.PATCH);
    }

    public Request putRequest(String uri) {
        return genericRequest(uri, HttpMethod.PUT);
    }

    public Request deleteRequest(String uri) {
        return genericRequest(uri, HttpMethod.DELETE);
    }

    private Request genericRequest(String uri, HttpMethod httpMethod) {
        final HttpHeaders headers = new HttpHeaders();

        // Add default and transaction headers to petition
        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        ClientInfo clientInfo = (ClientInfo) logMethods.request.getAttribute("ORIGIN-INFO");
        if (clientInfo != null) {
            headers.add(transactionHeader, clientInfo.transactionUUID().toString());
        }

        return new DefaultRequest(
                webClient,
                throwWebClientExceptions,
                headers,
                httpMethod,
                uri,
                logMethods
        );
    }

}