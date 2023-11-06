package me.fishydarwin.fractalexplorer.model.evaluator.expression.instantiate;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.ComplexVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.RealVariable;
import org.apache.commons.math3.complex.Complex;

public class ComplexInstantiateExpression implements InstantiateExpression {

    private final IStatement real;
    private final IStatement imaginary;

    public ComplexInstantiateExpression(IStatement real, IStatement imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Override
    public IVariable evaluate(IContext context) {
        IVariable var1 = real.evaluate(context);
        if (!(var1 instanceof RealVariable rv1))
            throw new IllegalArgumentException("Cannot instantiate a complex whose params are not real variables.");
        IVariable var2 = imaginary.evaluate(context);
        if (!(var2 instanceof RealVariable rv2))
            throw new IllegalArgumentException("Cannot instantiate a complex whose params are not real variables.");
        return new ComplexVariable(new Complex((Double) rv1.getValue(), (Double) rv2.getValue()));
    }
}
