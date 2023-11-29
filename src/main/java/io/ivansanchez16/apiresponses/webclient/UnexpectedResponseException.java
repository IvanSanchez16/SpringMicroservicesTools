package io.ivansanchez16.apiresponses.webclient;

public class UnexpectedResponseException extends RuntimeException{

    public UnexpectedResponseException(String message) {
        super(message);
    }
}