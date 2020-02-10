package com.epam.nnov.ru;

import com.epam.nnov.ru.exception.BracketsMissmatchException;
import com.epam.nnov.ru.exception.DivideByZeroException;
import com.epam.nnov.ru.exception.InvalidPowOfNum;
import com.epam.nnov.ru.exception.UnsupportedSymbolsInMathExpression;
import com.epam.nnov.ru.math.MathCalcParser;
import com.epam.nnov.ru.valid.Validator;

import java.util.Scanner;

public class Console {

    public static void main(String[] args) {
        System.out.println("Допускается использование цифр, в том числе дробных с разделителем '.' и знаков сложения/вычитания/деления/умножения/возведения в степень \n"+
                "Могут встречаться скобки и отрицательные числа.\n" +
                "Например выражение вида (-2)^2 - ((-4) * 3.5^0.5) будет считаться корректным.");
        System.out.println("Введите математическое выражение: ");
        Scanner sc = new Scanner(System.in);
        String inputMathExpression = sc.nextLine();
        Validator val = new Validator();

        try {
            if (val.isMathExpressionValid(inputMathExpression)) {
                MathCalcParser parser = new MathCalcParser();
                System.out.println(parser.parseAndCalcByReversePolishNotation(inputMathExpression));
            }
        } catch (UnsupportedSymbolsInMathExpression e) {
            System.out.println(e);
        } catch (BracketsMissmatchException e) {
            System.out.println(e);
        } catch (DivideByZeroException e) {
            System.out.println(e);
        } catch (InvalidPowOfNum e) {
            System.out.println(e);
        }
    }
}
