package me.fishydarwin.fractalexplorer.model.evaluator.expression.instantiate;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.RealVariable;

public class RealInstantiateExpression implements InstantiateExpression {

    private final IStatement real;

    public RealInstantiateExpression(IStatement real) {
        this.real = real;
    }

    @Override
    public IVariable evaluate(IContext context) {
        IVariable var = real.evaluate(context);
        if (!(var instanceof RealVariable rv))
            throw new IllegalArgumentException("Cannot instantiate a real whose param is not a real variable.");
        return new RealVariable((Double) rv.getValue());
    }

}
