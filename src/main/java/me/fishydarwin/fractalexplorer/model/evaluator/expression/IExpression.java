package me.fishydarwin.fractalexplorer.model.evaluator.expression;

import me.fishydarwin.fractalexplorer.model.evaluator.context.IContext;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

public interface IExpression {

    IVariable evaluate(IContext context);

}
