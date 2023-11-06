package me.fishydarwin.fractalexplorer.model.evaluator.expression;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public class VariableExpression implements IExpression {

    private final String variableKey;

    public VariableExpression(String variableKey) {
        this.variableKey = variableKey;
    }

    @Override
    public IVariable evaluate(IContext context) {
        return context.getVariable(variableKey);
    }

}
