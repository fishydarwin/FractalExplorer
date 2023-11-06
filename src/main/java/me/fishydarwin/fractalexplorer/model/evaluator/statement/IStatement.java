package me.fishydarwin.fractalexplorer.model.evaluator.statement;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public interface IStatement {
    IVariable evaluate(IContext context);
}
