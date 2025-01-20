package io.github.ivansanchez16.jpautils;

import lombok.Getter;

@Getter
public class InvalidValueException extends RuntimeException {

    private final String attributeName;
    private final String value;
    private final String entityAttribute;

    public InvalidValueException(String message, String attributeName, String value, String entityAttribute) {
        super(message);
        this.attributeName = attributeName;
        this.value = value;
        this.entityAttribute = entityAttribute;
    }
}
