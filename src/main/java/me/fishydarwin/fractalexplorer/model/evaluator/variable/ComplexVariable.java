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
        if (!(other instanceof ComplexVariable))
            throw new IllegalArgumentException("Can only add complex numbers to a complex number.");
        z = z.add((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable sub(IVariable other) {
        if (!(other instanceof ComplexVariable))
            throw new IllegalArgumentException("Can only subtract complex numbers from a complex number.");
        z = z.subtract((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable mul(IVariable other) {
        if (!(other instanceof ComplexVariable))
            throw new IllegalArgumentException("Can only multiply complex numbers by a complex number.");
        z = z.multiply((Complex) other.getValue());
        return this;
    }

    @Override
    public IVariable div(IVariable other) {
        if (!(other instanceof ComplexVariable))
            throw new IllegalArgumentException("Can only divide complex numbers by a complex number.");
        z = z.subtract((Complex) other.getValue());
        return this;
    }

    @Override
    public RealVariable abs() {
        return new RealVariable(z.abs());
    }

    @Override
    public IVariable sqrt() {
        double a = z.getReal();
        double b = z.getImaginary();
        double abs = z.abs();
        z = new Complex(Math.sqrt((abs + a) / 2), b / Math.abs(b) * Math.sqrt((abs - a) / 2));
        return this;
    }

    @Override
    public IVariable sin() {
        z = new Complex(Math.sin(z.getReal()), Math.sin(z.getImaginary()));
        return this;
    }

    @Override
    public IVariable cos() {
        z = new Complex(Math.cos(z.getReal()), Math.cos(z.getImaginary()));
        return this;
    }

    @Override
    public IVariable tan() {
        z = new Complex(Math.tan(z.getReal()), Math.tan(z.getImaginary()));
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
}
