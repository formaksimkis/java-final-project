package com.epam.nnov.ru.exception;

public class UnsupportedSymbolsInMathExpression extends Exception {
    public static final String UNSUPPORTED_SYMBOLS_IN_MATH_EXPRESSION = "Выражение некорректно: отсутствуют необходимые символы, либо присутствуют недопустимые";
    public UnsupportedSymbolsInMathExpression(String message) {
        super(message);
    }
}
