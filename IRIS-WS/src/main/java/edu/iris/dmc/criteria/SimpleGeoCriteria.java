package edu.iris.dmc.criteria;

public class SimpleGeoCriteria extends SimpleCriteria {

	protected SimpleGeoCriteria() {
		super();
	}

	public static SimpleGeoCriteria of() {
		return new SimpleGeoCriteria();
	}

	public SimpleGeoCriteria setMaximumRadius(Double value) {
		return this;
	}

	public SimpleGeoCriteria setMinimumRadius(Double value) {
		return this;
	}

	public SimpleGeoCriteria setLongitude(Double value) {
		return this;
	}

	public SimpleGeoCriteria setLatitude(Double value) {
		return this;
	}

	public SimpleGeoCriteria setMinimumLongitude(Double value) {
		return this;
	}

	public SimpleGeoCriteria setMaximumLongitude(Double value) {
		return this;
	}

	public SimpleGeoCriteria setMaximumLatitude(Double value) {
		return this;
	}

	public SimpleGeoCriteria setMinimumLatitude(Double value) {
		return this;
	}

}
