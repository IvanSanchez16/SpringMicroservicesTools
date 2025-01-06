package io.ivansanchez16.apiresponses.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.ivansanchez16.apiresponses.ApiBodyDTO;
import io.ivansanchez16.apiresponses.webclient.exceptions.MakeResponseException;
import io.ivansanchez16.apiresponses.webclient.exceptions.UnexpectedResponseException;
import io.ivansanchez16.logger.LogMethods;
import io.ivansanchez16.jpautils.PageQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@RequiredArgsConstructor
class DefaultRequest implements Request {

    private static final String UNEXPECTED_RESPONSE_MESSAGE = "The api response body has different structure of object provided";

    private final WebClient webClient;
    private final boolean throwWebClientExceptions;
    private final HttpHeaders headers;
    private final boolean logErrors;

    private final HttpMethod httpMethod;
    private final String uri;

    private MediaType mediaType = MediaType.APPLICATION_JSON;
    private Object body;
    private MultipartBodyBuilder multipartBodyBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final Gson gson = new Gson();

    @Override
    public Request setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public Request addHeader(String headerName, String headerValue) {
        headers.add(headerName, headerValue);
        return this;
    }

    @Override
    public Request addBody(Object requestBody) {
        this.body = requestBody;
        return this;
    }

    @Override
    public Request multipartBody(MultipartBodyBuilder builder) {
        this.multipartBodyBuilder = builder;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ApiBodyDTO<T> objectResponse(Class<T> clazz) {
        final String response = makeRequest();

        try {
            final ApiBodyDTO<LinkedTreeMap<String, Object>> apiBodyDTO = gson.fromJson(response, ApiBodyDTO.class);

            final ApiBodyDTO<T> finalResponse = new ApiBodyDTO<>( apiBodyDTO.getMeta() );
            if (apiBodyDTO.getData() != null) {
                finalResponse.setData( objectMapper.convertValue(apiBodyDTO.getData(), clazz) );
            }

            return finalResponse;

        } catch (Exception e) {
            if (logErrors) {
                LogMethods.logException(e);
            }

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE_MESSAGE, uri, httpMethod);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ApiBodyDTO<List<T>> listResponse(Class<T> clazz) {
        final String response = makeRequest();

        try {
            final ApiBodyDTO<LinkedTreeMap<String, Object>> apiBodyDTO = gson.fromJson(response, ApiBodyDTO.class);

            final ApiBodyDTO<List<T>> finalResponse = new ApiBodyDTO<>( apiBodyDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiBodyDTO.getData(), List.class) );

            finalResponse.setData( finalResponse.getData()
                    .stream()
                    .map(o -> objectMapper.convertValue(o, clazz))
                    .toList());

            return finalResponse;

        } catch (Exception e) {
            if (logErrors) {
                LogMethods.logException(e);
            }

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE_MESSAGE, uri, httpMethod);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ApiBodyDTO<PageQuery<T>> pageableResponse(Class<T> clazz) {
        final String response = makeRequest();

        try {
            final ApiBodyDTO<LinkedTreeMap<String, Object>> apiBodyDTO = gson.fromJson(response, ApiBodyDTO.class);

            final ApiBodyDTO<PageQuery<T>> finalResponse = new ApiBodyDTO<>( apiBodyDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiBodyDTO.getData(), PageQuery.class) );

            finalResponse.getData().setRows( finalResponse.getData().getRows()
                    .stream()
                    .map(o -> objectMapper.convertValue(o, clazz))
                    .toList());

            return finalResponse;

        } catch (Exception e) {
            if (logErrors) {
                LogMethods.logException(e);
            }

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE_MESSAGE, uri, httpMethod);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiBodyDTO<Void> noDataResponse() {
        final String response = makeRequest();

        try {
            final ApiBodyDTO<LinkedTreeMap<String, Object>> apiBodyDTO = gson.fromJson(response, ApiBodyDTO.class);

            return new ApiBodyDTO<>( apiBodyDTO.getMeta() );

        } catch (Exception e) {
            if (logErrors) {
                LogMethods.logException(e);
            }

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE_MESSAGE, uri, httpMethod);
        }
    }

    @Override
    public String rawResponse() {
        return makeRequest();
    }

    @Override
    public void ignoreResponse() {
        makeRequest();
    }

    private String makeRequest() {
        final WebClient.RequestHeadersSpec<?> requestObject;

        try {
            if (multipartBodyBuilder != null) {
                requestObject = buildRequestWithMultiPartBody();
            } else if (body != null) {
                requestObject = buildRequestWithBody();
            } else {
                requestObject = buildRequestWithoutBody();
            }
        } catch (Exception e) {
            throw new MakeResponseException(e.getMessage(), e);
        }

        String response;
        try {
            response = requestObject
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            if (throwWebClientExceptions) {
                // Rethrow exception to get stacktrace
                throw new WebClientResponseException(
                        ex.getMessage(),
                        ex.getStatusCode(),
                        ex.getStatusText(),
                        ex.getHeaders(),
                        ex.getResponseBodyAsByteArray(),
                        null,
                        ex.getRequest()
                );
            }

            response = ex.getResponseBodyAsString();
        } catch (WebClientRequestException ex) {
            // Rethrow exception to get stacktrace
            final Exception newExp = new Exception( ex.getMessage() );

            throw new WebClientRequestException(
                    newExp,
                    ex.getMethod(),
                    ex.getUri(),
                    ex.getHeaders()
            );
        }

        return response;
    }

    private WebClient.RequestHeadersSpec<?> buildRequestWithoutBody() {
        return webClient.method(httpMethod)
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(h -> h.addAll(headers))
                .contentType(mediaType);
    }

    private WebClient.RequestHeadersSpec<?> buildRequestWithBody() {
        return webClient.method(httpMethod)
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(h -> h.addAll(headers))
                .contentType(mediaType)
                .bodyValue(body);
    }

    private WebClient.RequestHeadersSpec<?> buildRequestWithMultiPartBody() {
        return webClient.method(httpMethod)
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(h -> h.addAll(headers))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body( BodyInserters.fromMultipartData(
                        multipartBodyBuilder.build()
                ));
    }
}
