package me.fishydarwin.fractalexplorer.model.evaluator.variable;

import me.fishydarwin.fractalexplorer.model.evaluator.variable.numeric.INumeric;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.ComplexVariableType;
import me.fishydarwin.fractalexplorer.model.evaluator.variable.type.IVariableType;
import org.apache.commons.math3.complex.Complex;

public class ComplexVariable implements IVariable, INumeric {

    private Complex z;

    public ComplexVariable(Complex z) {
        this.z = z;
    }

    public ComplexVariable(double real, double imaginary) {
        this(new Complex(real, imaginary));
    }

    public ComplexVariable() {
        this(new Complex(0, 0));
    }

    @Override
    public IVariableType getVariableType() {
        return new ComplexVariableType();
    }

    @Override
    public Object getValue() {
        return z;
    }


    @Override
    public IVariable copy() {
        return new ComplexVariable(z);
    }

    @Override
    public Class<ComplexVariable> variableType() {
        return ComplexVariable.class;
    }

    @Override
    public ComplexVariable grab() {
        return this;
    }

    @Override
    public IVariable add(IVariable other) {
        if (!(other instanceof ComplexVariable)) {
            z = z.add((Double) other.getValue());
            return this;
        }
        z = z.add((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable sub(IVariable other) {
        if (!(other instanceof ComplexVariable)) {
            z = z.subtract((Double) other.getValue());
            return this;
        }
        z = z.subtract((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable mul(IVariable other) {
        if (!(other instanceof ComplexVariable)) {
            z = z.multiply((Double) other.getValue());
            return this;
        }
        z = z.multiply((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable div(IVariable other) {
        if (!(other instanceof ComplexVariable)) {
            z = z.divide((Double) other.getValue());
            return this;
        }
        z = z.divide((Complex) other.getValue());
        return this;
    }

    @Override
    public RealVariable abs() {
        return new RealVariable(z.abs());
    }

    @Override
    public IVariable sqrt() {
        z = z.sqrt();
        return this;
    }

    @Override
    public IVariable sin() {
        z = z.sin();
        return this;
    }

    @Override
    public IVariable cos() {
        z = z.cos();
        return this;
    }

    @Override
    public IVariable tan() {
        z = z.tan();
        return this;
    }

    @Override
    public RealVariable real() {
        return new RealVariable(z.getReal());
    }

    @Override
    public RealVariable imaginary() {
        return new RealVariable(z.getImaginary());
    }

    @Override
    public IVariable ln() {
        z = z.log();
        return this;
    }

    @Override
    public IVariable pow(IVariable other) {
        if (other instanceof RealVariable) {
            z = z.pow((Double) other.getValue());
            return this;
        }
        z.pow((Complex) other.getValue());
        return this;
    }
}
