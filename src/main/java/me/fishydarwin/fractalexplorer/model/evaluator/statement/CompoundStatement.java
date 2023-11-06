package me.fishydarwin.fractalexplorer.model.evaluator.statement;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public class CompoundStatement implements IStatement {

    private final IStatement statement1;
    private final IStatement statement2;

    public CompoundStatement(IStatement statement1, IStatement statement2) {
        this.statement1 = statement1;
        this.statement2 = statement2;
    }

    @Override
    public IVariable evaluate(IContext context) {
        statement1.evaluate(context);
        return statement2.evaluate(context);
    }
}
