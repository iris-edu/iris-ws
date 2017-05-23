package edu.iris.dmc.sacpz.model;

import java.util.ArrayList;
import java.util.List;

public class PolesZeros {

	private List<Pole> poles = new ArrayList<Pole>();
	private List<Zero> zeros = new ArrayList<Zero>();

	public List<Pole> getPoles() {
		return poles;
	}

	public List<Zero> getZeros() {
		return zeros;
	}

	public void setPoles(List<Pole> poles) {
		this.poles = poles;
	}

	public void setZeros(List<Zero> zeros) {
		this.zeros = zeros;
	}

}
