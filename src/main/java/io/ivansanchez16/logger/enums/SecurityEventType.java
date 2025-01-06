package io.ivansanchez16.logger.enums;

public enum SecurityEventType {
    CAMBIOS_PRIVILEGIOS("Cambios de privilegios de acceso"),
    ACCESO_INFO_SENSIBLE("Acceso a información sensible");

    private final String value;

    SecurityEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
