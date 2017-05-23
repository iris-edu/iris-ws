package edu.iris.dmc.seedcodec;

public enum Type {

	ASCII(0), SHORT(1), INT24(2), INTEGER(3), FLOAT(4), DOUBLE(5), STEIM1(10), STEIM2(11), CDSN(16), SRO(30), DWWSSN(
			32);

	private int value;

	Type(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	public static Type from(String value) {
		switch (value) {
		case "ASCII":
			return ASCII;
		case "SHORT":
			return SHORT;
		case "INT24":
			return INT24;
		case "INTEGER":
			return INTEGER;
		case "FLOAT":
			return FLOAT;
		case "DOUBLE":
			return DOUBLE;
		case "STEIM1":
			return STEIM1;
		case "STEIM2":
			return STEIM2;
		case "CDSN":
			return CDSN;
		case "SRO":
			return SRO;
		case "DWWSSN":
			return DWWSSN;
		default:
			throw new IllegalArgumentException("Invalid type: " + value);
		}
	}
	public static Type from(int value) {
		switch (value) {
		case 0:
			return ASCII;
		case 1:
			return SHORT;
		case 2:
			return INT24;
		case 3:
			return INTEGER;
		case 4:
			return FLOAT;
		case 5:
			return DOUBLE;
		case 10:
			return STEIM1;
		case 11:
			return STEIM2;
		case 16:
			return CDSN;
		case 30:
			return SRO;
		case 32:
			return DWWSSN;
		default:
			throw new IllegalArgumentException("Invalid type: " + value);
		}
	}
}
