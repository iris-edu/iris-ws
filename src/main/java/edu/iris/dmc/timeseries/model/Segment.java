package edu.iris.dmc.timeseries.model;

import edu.iris.dmc.seedcodec.DecompressedData;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Segment implements Serializable, Comparable<Segment> {
	private static final long serialVersionUID = 6055844360696344359L;

	public static enum Type {
		SHORT,
		INT24,
		INTEGER,
		FLOAT,
		DOUBLE;

		private Type() {
		}
	}


	private float samplerate;
	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp expectedNextSampleTime;
	private Segment.Type type;
	private int sampleCount = 0;
	private final List<int[]> intList = new ArrayList<>();
	private final List<short[]> shortList = new ArrayList<>();
	private final List<double[]> doubleList = new ArrayList<>();
	private final List<float[]> floatList = new ArrayList<>();

	public Segment() {
	}

	public Segment(Segment.Type type, float sampleRate) {
		this.type = type;
		this.samplerate = sampleRate;
	}

	public Timestamp getExpectedNextSampleTime() {
		return this.expectedNextSampleTime;
	}

	void addAfter(Timestamp time, Timestamp endTime, DecompressedData data, float sampleRate, int numberOfSamples) {
		int index = 0;
		if (this.type == Segment.Type.DOUBLE) {
			index = 1;
		} else if (this.type == Segment.Type.INTEGER) {
			index = 1;
		} else if (this.type == Segment.Type.SHORT) {
			index = 1;
		} else if (this.type == Segment.Type.FLOAT) {
			index = 1;
		}

		this.add(time, endTime, data, sampleRate, numberOfSamples, index);
	}

	void add(Timestamp time, Timestamp endTime, DecompressedData data, float sampleRate, int numberOfSamples, int index) {
		long durationInMiliSecond = (long)((float)(numberOfSamples - 1) / sampleRate) * 1000L;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime() + durationInMiliSecond);
		double d = (double)((float)numberOfSamples / sampleRate * 1000.0F);
		cal.setTimeInMillis(time.getTime() + (long)d);
		Timestamp expectedTime = new Timestamp(cal.getTimeInMillis());
		this.sampleCount += numberOfSamples;
		int type = data.getType();
		if (this.startTime == null) {
			this.startTime = time;
			this.endTime = endTime;
			this.expectedNextSampleTime = expectedTime;
			switch(type) {
				case 1:
					this.shortList.add(data.getAsShort());
				case 2:
				default:
					break;
				case 3:
					this.intList.add(data.getAsInt());
					break;
				case 4:
					this.floatList.add(data.getAsFloat());
					break;
				case 5:
					this.doubleList.add(data.getAsDouble());
			}

		} else {
			if (endTime.after(this.endTime)) {
				this.endTime = endTime;
				this.expectedNextSampleTime = expectedTime;
			}

			if (this.startTime.before(this.startTime)) {
				this.startTime = time;
			}

			switch(type) {
				case 1:
					this.insertRecord(data.getAsShort(), index);
				case 2:
				default:
					break;
				case 3:
					this.insertRecord(data.getAsInt(), index);
					break;
				case 4:
					this.insertRecord(data.getAsFloat(), index);
					break;
				case 5:
					this.insertRecord(data.getAsDouble(), index);
			}

		}
	}

	private void insertRecord(short[] data, int index) {
		if (index > 0) {
			this.shortList.add(data);
		} else {
			this.shortList.add(index, data);
		}

	}

	private void insertRecord(int[] data, int index) {
		if (index > 0) {
			this.intList.add(data);
		} else {
			this.intList.add(index, data);
		}

	}

	private void insertRecord(double[] data, int index) {
		if (index > 0) {
			this.doubleList.add(data);
		} else {
			this.doubleList.add(index, data);
		}

	}

	private void insertRecord(float[] data, int index) {
		if (index > 0) {
			this.floatList.add(data);
		} else {
			this.floatList.add(index, data);
		}

	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public float getSamplerate() {
		return this.samplerate;
	}

	public void setSamplerate(float samplerate) {
		this.samplerate = samplerate;
	}

	public int getSampleCount() {
		return this.sampleCount;
	}

	public Segment.Type getType() {
		return this.type;
	}

	public List<Integer> getIntData() {
		List<Integer> l = new ArrayList<>();

		for (int[] ints : this.intList) {
			for (int n : ints) {
				l.add(n);
			}
		}

		return l;
	}

	public List<Short> getShortData() {
		List<Short> l = new ArrayList<>();
		for (short[] shorts : this.shortList) {
			for (short n : shorts) {
				l.add(n);
			}
		}
		return l;
	}

	public List<Float> getFloatData() {
		List<Float> l = new ArrayList<>();
		for (float[] floats : this.floatList) {
			for (float n : floats) {
				l.add(n);
			}
		}
		return l;
	}

	public List<Double> getDoubleData() {
		List<Double> l = new ArrayList<>();
		for (double[] doubles : this.doubleList) {
			for (double n : doubles) {
				l.add(n);
			}
		}
		return l;
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (this.startTime == null ? 0 : this.startTime.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			Segment other = (Segment)obj;
			if (this.startTime == null) {
				return other.startTime == null;
			} else return this.startTime.equals(other.startTime);
		}
	}

	public int compareTo(Segment ts) {
		if (ts == null) {
			return -1;
		} else if (this.startTime.after(ts.getStartTime())) {
			return 1;
		} else {
			return this.startTime.before(ts.getStartTime()) ? -1 : 0;
		}
	}
}
