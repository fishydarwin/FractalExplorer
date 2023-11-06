package me.fishydarwin.fractalexplorer.model.evaluator.variable;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.numeric.INumeric;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.IVariableType;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.RealVariableType;

public class RealVariable implements IVariable, INumeric {

    private double r;

    public RealVariable(double r) {
        this.r = r;
    }

    public RealVariable() {
        this(0);
    }

    @Override
    public IVariableType getVariableType() {
        return new RealVariableType();
    }

    @Override
    public Object getValue() {
        return r;
    }

    @Override
    public IVariable copy() {
        return new RealVariable(r);
    }

    @Override
    public Class<RealVariable> variableType() {
        return RealVariable.class;
    }

    @Override
    public RealVariable grab() {
        return this;
    }

    @Override
    public IVariable add(IVariable other) {
        if (!(other instanceof RealVariable))
            throw new IllegalArgumentException("Can only add real numbers to a real number.");
        r = r + (double) other.getValue();
        return this;
    }

    @Override
    public IVariable sub(IVariable other) {
        if (!(other instanceof RealVariable))
            throw new IllegalArgumentException("Can only subtract real numbers from a real number.");
        r = r - (double) other.getValue();
        return this;
    }

    @Override
    public IVariable mul(IVariable other) {
        if (!(other instanceof RealVariable))
            throw new IllegalArgumentException("Can only multiply real numbers by a real number.");
        r = r * (double) other.getValue();
        return this;
    }

    @Override
    public IVariable div(IVariable other) {
        if (!(other instanceof RealVariable))
            throw new IllegalArgumentException("Can only divide real numbers by a real number.");
        r = r / (double) other.getValue();
        return this;
    }

    @Override
    public RealVariable abs() {
        return new RealVariable(Math.abs(r));
    }

    @Override
    public IVariable sqrt() {
        r = Math.sqrt(r);
        return this;
    }

    @Override
    public IVariable sin() {
        r = Math.sin(r);
        return this;
    }

    @Override
    public IVariable cos() {
        r = Math.cos(r);
        return this;
    }

    @Override
    public IVariable tan() {
        r = Math.tan(r);
        return this;
    }

    @Override
    public RealVariable real() {
        return new RealVariable(r);
    }

    @Override
    public RealVariable imaginary() {
        return new RealVariable(0);
    }
}
