package edu.iris.dmc.sacpz.model;

public class AbstractPZ implements PZ {

	protected Double real;
	protected Double imaginary;

	public AbstractPZ(Double real, Double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public Double getReal() {
		return real;
	}

	public Double getImaginary() {
		return imaginary;
	}

	public void setReal(Double real) {
		this.real = real;
	}

	public void setImaginary(Double imaginary) {
		this.imaginary = imaginary;
	}

}
