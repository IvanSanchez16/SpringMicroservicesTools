package io.github.ivansanchez16.apiresponses.webclient.exceptions;

import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
public class UnexpectedResponseException extends RuntimeException{

    private final String uri;
    private final HttpMethod httpMethod;

    public UnexpectedResponseException(String message, String uri, HttpMethod httpMethod) {
        super(message);
        this.uri = uri;
        this.httpMethod = httpMethod;
    }


}