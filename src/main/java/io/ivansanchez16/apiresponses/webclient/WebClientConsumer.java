package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.logger.LogMethods;
import io.ivansanchez16.logger.classes.ClientInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class WebClientConsumer {

    private final WebClient webClient;

    private final boolean throwWebClientExceptions;
    private final HttpHeaders defaultHeaders;
    private final LogMethods logMethods;

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

        // Add default headers
        defaultHeaders.forEach((header, values) -> values.forEach(value -> headers.add(header, value)));

        // Add transaction header
        ClientInfo clientInfo = logMethods.getRequestInfoHelper().getClientInfoFromRequest();
        if (clientInfo != null) {
            headers.add(logMethods.getLogConfig().getTransactionHeader(), clientInfo.transactionUUID().toString());
        }

        // Add session headers
        JSONObject sessionInfo = logMethods.getRequestInfoHelper().getSessionInfoFromRequest();
        if (sessionInfo != null) {
            String sessionHeadersPrefix = logMethods.getLogConfig().getSessionHeadersPrefix();
            Iterator<String> keys = sessionInfo.keys();

            while (keys.hasNext()) {
                String header = keys.next();

                headers.add(sessionHeadersPrefix + "." + header, sessionInfo.getString(header));
            }
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