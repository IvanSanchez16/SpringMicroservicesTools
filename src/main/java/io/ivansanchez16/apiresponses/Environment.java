package io.ivansanchez16.apiresponses;

public enum Environment {

    DEVELOP("develop"),
    QA("qa"),
    PRODUCTION("production");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public static Environment getByValue(String value) {
        for (Environment env : values()) {
            if (env.value.equals(value)) {
                return env;
            }
        }

        return null;
    }
}
