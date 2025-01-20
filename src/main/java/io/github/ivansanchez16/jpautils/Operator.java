package io.github.ivansanchez16.jpautils;

public enum Operator {

    EQUAL("eq"),
    GREATER_THAN("gt"),
    GREATER_THAN_EQUAL("gte"),
    LESS_THAN("lt"),
    LESS_THAN_EQUAL("lte");

    private final String postfix;

    Operator(String postfix) {
        this.postfix = postfix;
    }

    public String getPostfix() {
        return postfix;
    }

    public static Operator getByPostfix(String postfix) {
        for (Operator operator : Operator.values()) {
            if (operator.getPostfix().equals(postfix)) {
                return operator;
            }
        }

        return null;
    }
}
