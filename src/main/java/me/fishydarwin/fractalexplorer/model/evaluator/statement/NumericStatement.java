package me.fishydarwin.fractalexplorer.model.evaluator.statement;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.numeric.INumeric;

public class NumericStatement implements IStatement {

    private final INumeric directVariable;

    public NumericStatement(INumeric directVariable) {
        this.directVariable = directVariable;
    }

    @Override
    public IVariable evaluate(IContext context) {
        return directVariable.grab();
    }

}
