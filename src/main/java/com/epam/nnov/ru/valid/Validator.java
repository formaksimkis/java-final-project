package com.epam.nnov.ru.valid;

import com.epam.nnov.ru.exception.BracketsMissmatchException;
import com.epam.nnov.ru.exception.UnsupportedSymbolsInMathExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern PATTERN_NUM_OP_NUM = Pattern.compile("(\\d+\\.?\\d*)(\\*?\\/?\\+?\\-?\\^?)(\\d+\\.?\\d*)");
    private static final Pattern PATTERN_NUM_IN_BRACKETS = Pattern.compile("(\\(\\-?\\d+\\.?\\d*\\))");
    private static final Pattern PATTERN_BAD_NUM = Pattern.compile("(\\d+\\.\\d+\\.)");
    private static final Pattern PATTERN_WRONG_SYMB_COMBS = Pattern.compile("(\\)\\d)|(\\)\\()|(\\d\\()|(\\([\\-|\\+|\\*|\\/|\\^]\\d+\\.?\\d*(?!\\)|\\.)\\D)");

    private static final Set<Character> OPERATIONS;
    static {
        OPERATIONS = new HashSet<>();
        OPERATIONS.add('+');
        OPERATIONS.add('-');
        OPERATIONS.add('*');
        OPERATIONS.add('/');
        OPERATIONS.add('^');
        OPERATIONS.add('(');
        OPERATIONS.add(')');
        OPERATIONS.add('.');
    }

    public Validator() {
    }

    public boolean isMathExpressionValid(String mathExpression) throws UnsupportedSymbolsInMathExpression, BracketsMissmatchException {

        String expression = deleteSpaces(mathExpression);
        Matcher matchBadNum = PATTERN_BAD_NUM.matcher(expression);
        Matcher wrongSymbCombs = PATTERN_WRONG_SYMB_COMBS.matcher(expression);
        if (matchBadNum.find() || wrongSymbCombs.find()) {
            throw new UnsupportedSymbolsInMathExpression(UnsupportedSymbolsInMathExpression.UNSUPPORTED_SYMBOLS_IN_MATH_EXPRESSION);
        }

        char[] elems = expression.toCharArray();
        int balanceCounterBrackets = 0;

        for (int i = 0; i < elems.length; i++) {
            if (elems.length == 1 && Character.isDigit(elems[i])) {
                return true;
            }
            if (!Character.isDigit(elems[i]) && !OPERATIONS.contains(elems[i])) {
                throw new UnsupportedSymbolsInMathExpression(UnsupportedSymbolsInMathExpression.UNSUPPORTED_SYMBOLS_IN_MATH_EXPRESSION);
            }
            balanceCounterBrackets += elems[i] == '(' ? 1 : elems[i] == ')' ? -1 : 0;
        }
        if (balanceCounterBrackets != 0) {
            throw new BracketsMissmatchException(BracketsMissmatchException.BRACKETS_MISSMATCH_EXCEPTION);
        } else return isExpressionCalculated(expression);
    }

    private boolean isExpressionCalculated (String expression) throws UnsupportedSymbolsInMathExpression {
        String result = expression;
        while (!result.equals("0") && !result.equals(collapseNumOpNum(collapseNegNum(result)))) {
            result = collapseNumOpNum(collapseNegNum(result));
        }

        if (!result.equals("0")) {
            throw new UnsupportedSymbolsInMathExpression(UnsupportedSymbolsInMathExpression.UNSUPPORTED_SYMBOLS_IN_MATH_EXPRESSION);
        }
        return true;
    }

    private String collapseNegNum (String expression) {
        Matcher matchNumInBrackets = PATTERN_NUM_IN_BRACKETS.matcher(expression);
        boolean findNumInBrackets = matchNumInBrackets.find();
        while (findNumInBrackets) {
            expression = new StringBuilder(expression)
                    .replace(matchNumInBrackets.start(1), matchNumInBrackets.end(1), "0").toString();
            matchNumInBrackets = PATTERN_NUM_IN_BRACKETS.matcher(expression);
            findNumInBrackets = matchNumInBrackets.find();
        }
        return expression;
    }

    private String collapseNumOpNum (String expression) {
        Matcher matcherNumOpNum = PATTERN_NUM_OP_NUM.matcher(expression);
        boolean findNumOpNum = matcherNumOpNum.find();
        while (findNumOpNum) {
                expression = new StringBuilder(expression)
                        .replace(matcherNumOpNum.start(1), matcherNumOpNum.end(3), "0").toString();
                matcherNumOpNum = PATTERN_NUM_OP_NUM.matcher(expression);
                findNumOpNum = matcherNumOpNum.find();
        }
        return expression;
    }

    public static String deleteSpaces (String mathExpression) {
        return mathExpression.replaceAll(" ", "");
    }
}
