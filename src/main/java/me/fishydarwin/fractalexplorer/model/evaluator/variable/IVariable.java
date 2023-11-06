package me.fishydarwin.fractalexplorer.model.evaluator.variable;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.IVariableType;

public interface IVariable {
    IVariableType getVariableType();
    Object getValue();

    IVariable copy();
}
