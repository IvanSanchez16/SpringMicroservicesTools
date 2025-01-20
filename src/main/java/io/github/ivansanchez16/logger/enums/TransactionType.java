package io.github.ivansanchez16.logger.enums;

public enum TransactionType {
    GRABAR("Grabado"),
    ACTUALIZAR("Actualizado"),
    ELIMINAR("Eliminado");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
