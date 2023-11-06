package me.fishydarwin.fractalexplorer.model.evaluator.expression;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.numeric.INumeric;

public class ArithmeticExpression implements IExpression {

    public interface NumericExpressionOperator {}
    public enum UnaryNumericExpressionOperator implements NumericExpressionOperator {
        ABS, SQRT, SIN, COS, TAN, RE, IM
    }
    public enum BinaryNumericExpressionOperator implements NumericExpressionOperator {
        ADD, SUB, MUL, DIV
    }

    private final IStatement statement1;
    private final IStatement statement2;
    private final NumericExpressionOperator operator;

    public ArithmeticExpression(IStatement statement1, IStatement statement2,
                                NumericExpressionOperator operator) {
        this.statement1 = statement1;
        this.statement2 = statement2;
        this.operator = operator;
    }

    @Override
    public IVariable evaluate(IContext context) {
        IVariable ncVar1 = statement1.evaluate(context);
        if (!(ncVar1 instanceof INumeric var1))
            throw new IllegalArgumentException("Cannot perform arithmetic expressions on non-numeric types.");
        if (operator instanceof UnaryNumericExpressionOperator opCast) {
            switch (opCast) {
                default -> throw new ArithmeticException("Invalid unary operator.");
                case ABS -> {
                    return var1.abs();
                }
                case SQRT -> {
                    return var1.sqrt();
                }
                case SIN -> {
                    return var1.sin();
                }
                case COS -> {
                    return var1.cos();
                }
                case TAN -> {
                    return var1.tan();
                }
                case RE -> {
                    return var1.real();
                }
                case IM -> {
                    return var1.imaginary();
                }
            }
        } else if (operator instanceof BinaryNumericExpressionOperator opCast) {
            IVariable ncVar2 = statement2.evaluate(context);
            if (!(ncVar2 instanceof INumeric var2))
                throw new IllegalArgumentException("Cannot perform arithmetic expressions on non-numeric types.");
            if (!var1.variableType().equals(var2.variableType())) {
                throw new ArithmeticException("Variables have mismatching types.");
            }
            switch (opCast) {
                default -> throw new ArithmeticException("Invalid binary operator.");
                case ADD -> {
                    return var1.add(var2.grab());
                }
                case SUB -> {
                    return var1.sub(var2.grab());
                }
                case MUL -> {
                    return var1.mul(var2.grab());
                }
                case DIV -> {
                    return var1.div(var2.grab());
                }
            }
        }
        throw new RuntimeException("Operator is not of a recognized type.");
    }

}
