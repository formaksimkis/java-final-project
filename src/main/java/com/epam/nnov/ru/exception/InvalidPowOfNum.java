package com.epam.nnov.ru.exception;

public class InvalidPowOfNum extends Exception {
    public static final String NEG_NUM_POW = "Невозможно извлечь корень из отрицательного числа";
    public static final String NEG_POW_FOR_ZERRO = "Невозможно возвести ноль в отрицательную степень";
    public static final String SMTH_WRONG = "Что-то пошло не так при возведении в степень";
    public InvalidPowOfNum(String message) {
        super(message);
    }
}