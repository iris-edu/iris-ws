package edu.iris.dmc.sacpz.model;

public class Pole extends AbstractPZ {
	public Pole(Double real, Double imaginary) {
		super(real, imaginary);
	}

	public static Pole of(Double real, Double imaginary){
		return new Pole(real, imaginary);
	}
}

