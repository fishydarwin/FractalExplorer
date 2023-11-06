package me.fishydarwin.fractalexplorer.model.evaluator.compiler;

import me.fishydarwin.fractalexplorer.model.evaluator.context.Context;
import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.expression.ArithmeticExpression;
import me.fishydarwin.fractalexplorer.model.evaluator.expression.VariableExpression;
import me.fishydarwin.fractalexplorer.model.evaluator.expression.instantiate.ComplexInstantiateExpression;
import me.fishydarwin.fractalexplorer.model.evaluator.expression.instantiate.RealInstantiateExpression;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.*;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.ComplexVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.RealVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.ComplexVariableType;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

import java.util.function.Function;

public class FEXLCompiler {

    public static Function<Pair<Complex, Complex>, Pair<Complex, Double>> generateFunction
            (IStatement statement) {
        return (zcPair) -> {
            IContext context = new Context();
            context.setVariable("z", new ComplexVariable(zcPair.getFirst()));
            context.setVariable("c", new ComplexVariable(zcPair.getSecond()));
            context.setVariable("bound", new RealVariable(2));

            IVariable evaluated = statement.evaluate(context);
            if (!(evaluated.getVariableType() instanceof ComplexVariableType))
                throw new RuntimeException("Not a complex result.");

            double bound = (double) context.getVariable("bound").getValue();
            return new Pair<>((Complex) evaluated.getValue(), bound);
        };
    }

    public static IStatement compileFEXL(String fullInput) {
        fullInput = fullInput.strip();
        if (fullInput.length() == 0)
            throw new IllegalArgumentException("Empty FEXL program!");

        IStatement result = null;

        String[] fullInputSplitInstructions = fullInput.split("[;\\n]");
        for (String instruction : fullInputSplitInstructions) {
            IStatement currentStatement;

            if (instruction.strip().length() == 0) // empty
                continue;
            if (instruction.startsWith("//")) // comment
                continue;

            // all assignments
            String[] assignmentSplit = instruction.strip().split("=");
            if (assignmentSplit.length != 2)
                throw new IllegalArgumentException("Invalid token: " + instruction + ". Expected assignment.");

            // variable checks
            String varKey = assignmentSplit[0].strip();
            if (!varKey.matches("^(?!\\d|__|_$)\\w+$"))
                throw new IllegalArgumentException("Invalid variable name: " + varKey);

            // expression type
            String varExpression = assignmentSplit[1].strip();
            if (varExpression.matches("^(?!\\d|__|_$)\\w+$")) {
                // basic variable assignment
                currentStatement = new AssignStatement(varKey, new VariableExpression(varExpression));
            }
            else if (varExpression.contains("[") && varExpression.contains("]")) {
                // unary arithmetic
                String[] expressionSplit = varExpression.split("\\[");
                if (expressionSplit.length != 2)
                    throw new IllegalArgumentException("Unknown unary operator, or too many arguments!");

                String operationEnumName = expressionSplit[0].strip().toUpperCase();
                String variableUnary = expressionSplit[1].strip().split("\\]")[0].strip();

                ArithmeticExpression.UnaryNumericExpressionOperator operator =
                        ArithmeticExpression.UnaryNumericExpressionOperator.valueOf(operationEnumName);

                currentStatement = new AssignStatement(varKey,
                        new ArithmeticExpression(
                                new VariableStatement(variableUnary),
                                null,
                                operator)
                );
            }
            else if (varExpression.contains("+") || varExpression.contains("-") ||
                    varExpression.contains("*") || varExpression.contains("/")) {
                // binary arithmetic
                String[] expressionSplit = varExpression.split("[\\+\\-\\*/]");
                if (expressionSplit.length != 2)
                    throw new IllegalArgumentException("Unknown binary operator, or too many arguments!");

                String variableBinary1 = expressionSplit[0].strip();
                String variableBinary2 = expressionSplit[1].strip();

                String operationEnumName = "ADD";
                if (varExpression.contains("-")) operationEnumName = "SUB";
                else if (varExpression.contains("*")) operationEnumName = "MUL";
                else if (varExpression.contains("/")) operationEnumName = "DIV";

                ArithmeticExpression.BinaryNumericExpressionOperator operator =
                        ArithmeticExpression.BinaryNumericExpressionOperator.valueOf(operationEnumName);

                IStatement variableBinary1Result;
                try {
                    double variableBinary1Double = Double.parseDouble(variableBinary1);
                    variableBinary1Result = new NumericStatement(new RealVariable(variableBinary1Double));
                }
                catch (Exception ignored) { variableBinary1Result = new VariableStatement(variableBinary1); }

                IStatement variableBinary2Result;
                try {
                    double variableBinary2Double = Double.parseDouble(variableBinary2);
                    variableBinary2Result = new NumericStatement(new RealVariable(variableBinary2Double));
                }
                catch (Exception ignored) { variableBinary2Result = new VariableStatement(variableBinary2); }

                currentStatement = new AssignStatement(varKey,
                        new ArithmeticExpression(
                                variableBinary1Result,
                                variableBinary2Result,
                                operator)
                );
            }
            else if (varExpression.contains("real:")) {
                // real instantiation
                String[] expressionSplit = varExpression.split(":");
                if (expressionSplit.length != 2)
                    throw new IllegalArgumentException("Invalid real instantiation!");

                String variableReal = expressionSplit[1].strip();

                IStatement variableRealResult;
                try {
                    double variableRealDouble = Double.parseDouble(variableReal);
                    variableRealResult = new NumericStatement(new RealVariable(variableRealDouble));
                }
                catch (Exception ignored) { variableRealResult = new VariableStatement(variableReal); }

                currentStatement = new AssignStatement(varKey,
                        new RealInstantiateExpression(variableRealResult)
                );
            }
            else if (varExpression.contains("complex:")) {
                // complex instantiation
                String[] expressionSplit = varExpression.split(":");
                if (expressionSplit.length != 2)
                    throw new IllegalArgumentException("Invalid complex instantiation!");

                String[] complexSplit = expressionSplit[1].strip().split(",");
                if (complexSplit.length != 2)
                    throw new IllegalArgumentException("Invalid complex instantiation!" +
                            "Did you forget an argument?");

                String variableReal1 = complexSplit[0].strip();
                String variableReal2 = complexSplit[1].strip();

                IStatement variableReal1Result;
                try {
                    double variableReal1Double = Double.parseDouble(variableReal1);
                    variableReal1Result = new NumericStatement(new RealVariable(variableReal1Double));
                }
                catch (Exception ignored) { variableReal1Result = new VariableStatement(variableReal1); }

                IStatement variableReal2Result;
                try {
                    double variableReal2Double = Double.parseDouble(variableReal2);
                    variableReal2Result = new NumericStatement(new RealVariable(variableReal2Double));
                }
                catch (Exception ignored) { variableReal2Result = new VariableStatement(variableReal2); }

                currentStatement = new AssignStatement(varKey,
                        new ComplexInstantiateExpression(
                                variableReal1Result,
                                variableReal2Result
                        )
                );
            }
            else throw new IllegalArgumentException("Expression type unknown: " + varExpression);

            if (result == null) {
                result = currentStatement;
            } else {
                result = new CompoundStatement(result, currentStatement);
            }
        }

        return result;
    }

}
