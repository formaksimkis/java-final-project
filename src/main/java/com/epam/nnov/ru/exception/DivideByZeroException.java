package com.epam.nnov.ru.exception;

public class DivideByZeroException extends Exception {
    public static final String DIVIDE_BY_ZERO_EXCEPTION = "Произошло деление на ноль";
    public DivideByZeroException(String message) {
        super(message);
    }
}
