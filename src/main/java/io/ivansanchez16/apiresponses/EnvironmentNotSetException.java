package io.ivansanchez16.apiresponses;

public class EnvironmentNotSetException extends RuntimeException{

    public EnvironmentNotSetException() {
        super("You need to specify an environment in configuration file");
    }
}
