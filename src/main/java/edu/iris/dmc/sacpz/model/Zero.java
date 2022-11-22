package edu.iris.dmc.sacpz.model;

public class Zero extends AbstractPZ {
	public Zero(Double real, Double imaginary) {
		super(real, imaginary);
	}

	public static Zero of(Double real, Double imaginary){
		return new Zero(real, imaginary);
	}
}
