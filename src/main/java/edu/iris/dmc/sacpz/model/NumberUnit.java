package edu.iris.dmc.sacpz.model;

public class NumberUnit {

	private String unit;
	private Double value;
	
	public NumberUnit(){}
	
	public NumberUnit(String unit, Double value) {
		super();
		this.unit = unit;
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
