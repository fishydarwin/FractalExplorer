package me.fishydarwin.fractalexplorer.model.evaluator.context;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

import java.util.Set;

public interface IContext {

    void setVariable(String key, IVariable value);
    IVariable getVariable(String key);

    Set<String> variables();

}
