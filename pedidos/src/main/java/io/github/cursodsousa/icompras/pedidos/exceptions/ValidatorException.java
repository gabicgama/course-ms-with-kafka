package io.github.cursodsousa.icompras.pedidos.exceptions;

import lombok.Getter;

@Getter
public class ValidatorException extends RuntimeException {

    private final String field;
    private final String message;

    public ValidatorException(String message, String field) {
        super(message);
        this.message = message;
        this.field = field;
    }
}
