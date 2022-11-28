package edu.iris.dmc.sacpz.model;

import java.util.ArrayList;
import java.util.List;

public class PolesZeros {
	private List<Pole> poles = new ArrayList<>();
	private List<Zero> zeros = new ArrayList<>();

	public PolesZeros() {
	}

	public List<Pole> getPoles() {
		return this.poles;
	}

	public List<Zero> getZeros() {
		return this.zeros;
	}

	public void setPoles(List<Pole> poles) {
		this.poles = poles;
	}

	public void setZeros(List<Zero> zeros) {
		this.zeros = zeros;
	}
}

