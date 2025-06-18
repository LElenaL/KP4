package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CalculatorModel {
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    static {
        OPERATOR_PRECEDENCE.put("+", 1);
        OPERATOR_PRECEDENCE.put("-", 1);
        OPERATOR_PRECEDENCE.put("*", 2);
        OPERATOR_PRECEDENCE.put("/", 2);
        OPERATOR_PRECEDENCE.put("%", 2);
        OPERATOR_PRECEDENCE.put("//", 2);
        OPERATOR_PRECEDENCE.put("**", 3);
        OPERATOR_PRECEDENCE.put("^", 3);
    }

    public double calculateExpression(String expression) throws IllegalArgumentException {
        List<String> tokens = tokenize(expression);
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }

                if (c == '-' && (i == 0 || tokens.get(tokens.size() - 1).equals("("))) {
                    currentToken.append(c);
                } else if (c == '*' && i + 1 < expression.length() && expression.charAt(i + 1) == '*') {
                    tokens.add("**");
                    i++;
                } else if (c == '/' && i + 1 < expression.length() && expression.charAt(i + 1) == '/') {
                    tokens.add("//");
                    i++;
                } else {
                    tokens.add(String.valueOf(c));
                }
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private List<String> infixToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                operatorStack.pop(); // Remove the '(' from stack
            } else {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") &&
                        OPERATOR_PRECEDENCE.getOrDefault(operatorStack.peek(), 0) >=
                                OPERATOR_PRECEDENCE.getOrDefault(token, 0)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return stack.pop();
    }

    private double applyOperator(double a, double b, String operator) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "**":
            case "^": return Math.pow(a, b);
            case "//": return Math.floorDiv((long)a, (long)b);
            case "%": return a % b;
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
