package me.fishydarwin.fractalexplorer.model.evaluator.statement;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public class VariableStatement implements IStatement {

    private final String variableKey;

    public VariableStatement(String variableKey) {
        this.variableKey = variableKey;
    }

    @Override
    public IVariable evaluate(IContext context) {
        return context.getVariable(variableKey);
    }

}
