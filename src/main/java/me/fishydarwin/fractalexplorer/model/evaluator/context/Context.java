package me.fishydarwin.fractalexplorer.model.evaluator.context;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class Context implements IContext {

    private final Map<String, IVariable> variables = new HashMap<>();

    @Override
    public void setVariable(String key, IVariable value) {
        variables.put(key, value);
    }

    @Override
    public IVariable getVariable(String key) {
        return variables.get(key);
    }

    @Override
    public Set<String> variables() {
        return variables.keySet();
    }

}
