package edu.iris.dmc.seedcodec;

public class DecompressedStruct {

	private float[] data;
	private float min;
	private float max;

	private DecompressedStruct(float[] data, float min, float max) {
		super();
		this.data = data;
		this.min = min;
		this.max = max;
	}

	public static DecompressedStruct of(float[] data, float min, float max){
		return new DecompressedStruct(data,min,max);
	}

	public float[] getData() {
		return data;
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

}
