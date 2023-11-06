package me.fishydarwin.fractalexplorer.model.evaluator.statement;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.expression.IExpression;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public class AssignStatement implements IStatement {

    private final String variableKey;
    private final IExpression expression;

    public AssignStatement(String variableKey, IExpression expression) {
        this.variableKey = variableKey;
        this.expression = expression;
    }

    @Override
    public IVariable evaluate(IContext context) {
        IVariable evaluation = expression.evaluate(context);
        context.setVariable(variableKey, evaluation.copy());
        return evaluation;
    }
}
