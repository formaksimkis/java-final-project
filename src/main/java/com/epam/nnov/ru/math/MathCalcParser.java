package com.epam.nnov.ru.math;

import com.epam.nnov.ru.exception.DivideByZeroException;
import com.epam.nnov.ru.exception.InvalidPowOfNum;
import com.epam.nnov.ru.exception.UnsupportedSymbolsInMathExpression;
import com.epam.nnov.ru.valid.Validator;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathCalcParser {

    private static final Map<String, Integer> PRIORITY_OPERATIONS;
    private static final Pattern PATTERN_NUM = Pattern.compile("\\d+\\.?\\d*");
    private static final Pattern PATTERN_OP = Pattern.compile("[\\*?\\/?\\+?\\-?\\^?]");
    private static final Pattern PATTERN_NUM_NEG = Pattern.compile("(\\(\\-)(\\d+\\.?\\d*\\))");

    static {
        PRIORITY_OPERATIONS = new HashMap<>();
        PRIORITY_OPERATIONS.put("(", 0);
        PRIORITY_OPERATIONS.put(")", 0);
        PRIORITY_OPERATIONS.put("+", 1);
        PRIORITY_OPERATIONS.put("-", 1);
        PRIORITY_OPERATIONS.put("*", 2);
        PRIORITY_OPERATIONS.put("/", 2);
        PRIORITY_OPERATIONS.put("^", 3);
    }

    private Stack<BigDecimal> reversePolishStack;

    private Stack<String> operationStack;

    public MathCalcParser() {
        reversePolishStack = new Stack<>();
        operationStack = new Stack<>();
    }

    public BigDecimal parseAndCalcByReversePolishNotation(String inputExpression) throws DivideByZeroException, UnsupportedSymbolsInMathExpression, InvalidPowOfNum {
       String mathExpression = Validator.deleteSpaces(inputExpression);
       mathExpression = convertNegNum(mathExpression);
       Matcher matcherNum = PATTERN_NUM.matcher(mathExpression);
       Matcher matcherOp = PATTERN_OP.matcher(mathExpression);
       boolean isNumFind = matcherNum.find();
       boolean isOpFind = matcherOp.find();
       char[] symbol = mathExpression.toCharArray();

       for (int i = 0; i < symbol.length;) {
           char leftBracket = '(';
           char rightBracket = ')';
           if (isNumFind && i == matcherNum.start()) {
               reversePolishStack.push(new BigDecimal(matcherNum.group()));
               i = matcherNum.end();
               isNumFind = matcherNum.find();
           } else if (symbol[i] == leftBracket) {
               operationStack.push(String.valueOf(leftBracket));
               i++;
           } else if (symbol[i] == rightBracket) {
               while (!operationStack.peek().equals(String.valueOf(leftBracket))) {
                   reversePolishStack.push(makeOperation(reversePolishStack.pop(),
                           reversePolishStack.pop(), operationStack.pop()));
               }
               operationStack.pop();
               i++;
           } else if (isOpFind && i == matcherOp.start()) {
               while (!operationStack.empty() && isOperationPrevPriorOrEqualsThanCurr(matcherOp.group(), operationStack.peek())) {
                   reversePolishStack.push(makeOperation(reversePolishStack.pop(), reversePolishStack.pop(), operationStack.pop()));
               }
               operationStack.push(matcherOp.group());
               i = matcherOp.end();
               isOpFind = matcherOp.find();
           } else {
               throw new UnsupportedSymbolsInMathExpression
                       (UnsupportedSymbolsInMathExpression.UNSUPPORTED_SYMBOLS_IN_MATH_EXPRESSION);
           }
       }
       while (!operationStack.isEmpty()) {
           reversePolishStack.push(makeOperation(reversePolishStack.pop(), reversePolishStack.pop(), operationStack.pop()));            //непредвиденный случай, если вдруг сюда попадём
       }
       return reversePolishStack.pop();
    }

    private boolean isOperationPrevPriorOrEqualsThanCurr(String opCurr, String opPrev) {
        return PRIORITY_OPERATIONS.get(opPrev) >= PRIORITY_OPERATIONS.get(opCurr);
    }

    private BigDecimal makeOperation (BigDecimal b, BigDecimal a, String operation) throws DivideByZeroException, InvalidPowOfNum {
       int scale = 10;
       switch (operation) {
            case "*":
                return a.multiply(b);
            case "/":
                try {
                    return a.divide(b, scale, BigDecimal.ROUND_FLOOR);
                } catch (ArithmeticException e) {
                    throw new DivideByZeroException(DivideByZeroException.DIVIDE_BY_ZERO_EXCEPTION);
                }
            case "+":
                return a.add(b);
            case "-":
                return a.add((b).negate());
            case "^":
               if (b.doubleValue() - b.intValue() != 0 && a.compareTo(BigDecimal.ZERO) == -1) {
                   throw new InvalidPowOfNum(InvalidPowOfNum.NEG_NUM_POW);
               }
               if (a.compareTo(BigDecimal.ZERO) == 0 && b.compareTo(BigDecimal.ZERO) == -1) {
                   throw new InvalidPowOfNum(InvalidPowOfNum.NEG_POW_FOR_ZERRO);
               }
               try {
                   return BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
               } catch (Exception e) {
                   throw new InvalidPowOfNum(InvalidPowOfNum.SMTH_WRONG);
               }
        }
        return null;
    }

    private String convertNegNum(String mathExpression) {
       Matcher matcherNumNeg = PATTERN_NUM_NEG.matcher(mathExpression);
       while (matcherNumNeg.find()) {
           mathExpression = new StringBuilder(mathExpression).
                   replace(matcherNumNeg.start(1), matcherNumNeg.end(1), "(0-").toString();
           matcherNumNeg = PATTERN_NUM_NEG.matcher(mathExpression);
       }
       return mathExpression;
    }

}
