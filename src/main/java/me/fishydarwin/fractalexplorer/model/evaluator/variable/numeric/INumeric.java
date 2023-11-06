package me.fishydarwin.fractalexplorer.model.evaluator.variable.numeric;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.IVariable;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.RealVariable;

public interface INumeric {

    Class<? extends IVariable> variableType();
    IVariable grab();

    IVariable add(IVariable other);
    IVariable sub(IVariable other);
    IVariable mul(IVariable other);
    IVariable div(IVariable other);

    RealVariable abs();
    IVariable sqrt();

    IVariable sin();
    IVariable cos();
    IVariable tan();

    RealVariable real();
    RealVariable imaginary();

}
