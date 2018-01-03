package edu.iris.dmc.timeseries.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.iris.dmc.seedcodec.DecompressedData;

public class Segment implements Serializable, Comparable<Segment> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6055844360696344359L;

	public enum Type {
		SHORT, INT24, INTEGER, FLOAT, DOUBLE
	};

	private Timeseries timeseries;
	private float samplerate; /* Nominal sample rate (Hz) */

	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp expectedNextSampleTime;

	private Type type;
	private int sampleCount = 0; /* Number of samples in trace segment */

	private List<int[]> intList = new ArrayList<int[]>();
	private List<short[]> shortList = new ArrayList<short[]>();
	private List<double[]> doubleList = new ArrayList<double[]>();
	private List<float[]> floatList = new ArrayList<float[]>();

	public Segment() {

	}

	public Segment(Type type, float sampleRate) {
		this.type = type;
		this.samplerate = sampleRate;
	}

	public Timestamp getExpectedNextSampleTime() {
		return expectedNextSampleTime;
	}

	void addAfter(Timestamp time/* Btime bStartTime */, Timestamp endTime,
			DecompressedData data, float sampleRate, int numberOfSamples) {

		int index = 0;
		if (this.type == Type.DOUBLE) {
			index = 1;
		} else if (this.type == Type.INTEGER) {
			index = 1;
		} else if (this.type == Type.SHORT) {
			index = 1;
		} else if (this.type == Type.FLOAT) {
			index = 1;
		}
		this.add(time, endTime, data, sampleRate, numberOfSamples, index);
	}

	void add(Timestamp time/* Btime bStartTime */, Timestamp endTime,
			DecompressedData data, float sampleRate, int numberOfSamples,
			int index) {

		// Calculate b end time
		long durationInMiliSecond = (long) ((numberOfSamples - 1) / sampleRate) * 1000;

		// Timestamp startTime = Util.toTime(bStartTime);
		// long durationinMiliSecond = (long) ((numberOfSamples - 1) /
		// sampleRate) * 1000;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime() + durationInMiliSecond);

		double d = (numberOfSamples / sampleRate) * 1000;
		cal.setTimeInMillis(time.getTime() + (long) d);
		Timestamp expectedTime = new Timestamp(cal.getTimeInMillis());
		this.sampleCount += numberOfSamples;

		int type = data.getType();
		if (this.startTime == null) {// A newly constructed segment

			// this.bStartTime = bStartTime;
			this.startTime = time;
			this.endTime = endTime;
			this.expectedNextSampleTime = expectedTime;

			switch (type) {
			case DecompressedData.INTEGER:
				this.intList.add(data.getAsInt());
				break;
			case DecompressedData.SHORT:
				this.shortList.add(data.getAsShort());
				break;
			case DecompressedData.FLOAT:
				this.floatList.add(data.getAsFloat());
				break;
			case DecompressedData.DOUBLE:
				this.doubleList.add(data.getAsDouble());
				break;
			default: // throw error
				break;
			}
			return;
		}

		if (endTime.after(this.endTime)) {
			this.endTime = endTime;
			this.expectedNextSampleTime = expectedTime;
		}

		if (startTime.before(this.startTime)) {
			this.startTime = time;
		}

		// this.intData = insertChunck(data, index);

		switch (type) {
		case DecompressedData.INTEGER:
			insertRecord(data.getAsInt(), index);
			break;
		case DecompressedData.SHORT:
			insertRecord(data.getAsShort(), index);
			break;
		case DecompressedData.FLOAT:
			insertRecord(data.getAsFloat(), index);
			break;
		case DecompressedData.DOUBLE:
			insertRecord(data.getAsDouble(), index);
			break;
		default: // throw error
			break;
		}
	}

	private void insertRecord(short[] data, int index) {
		if (index > 0) {
			this.shortList.add(data);
		} else {
			this.shortList.add(index, data);
		}
	}

	/*
	 * private short[] insertRecord(short[] data, int index) { short[] result =
	 * new short[this.shortData.length + data.length];
	 * 
	 * System.arraycopy(this.shortData, 0, result, 0, index);
	 * System.arraycopy(this.shortData, index, result, index + data.length,
	 * this.shortData.length - index); System.arraycopy(data, 0, result, index,
	 * data.length);
	 * 
	 * return result; }
	 */

	// List<Integer> l = new ArrayList<Integer>();
	private void insertRecord(int[] data, int index) {
		if (index > 0) {
			this.intList.add(data);
		} else {
			this.intList.add(index, data);
		}
	}

	/*
	 * private int[] insertRecord(int[] data, int index) { int[] result = new
	 * int[this.intData.length + data.length];
	 * 
	 * System.arraycopy(this.intData, 0, result, 0, index);
	 * System.arraycopy(this.intData, index, result, index + data.length,
	 * this.intData.length - index); System.arraycopy(data, 0, result, index,
	 * data.length);
	 * 
	 * return result; }
	 */

	private void insertRecord(double[] data, int index) {
		if (index > 0) {
			this.doubleList.add(data);
		} else {
			this.doubleList.add(index, data);
		}
	}

	/*
	 * private double[] insertRecord(double[] data, int index) { double[] result
	 * = new double[this.doubleData.length + data.length];
	 * 
	 * System.arraycopy(this.doubleData, 0, result, 0, index);
	 * System.arraycopy(this.doubleData, index, result, index + data.length,
	 * this.doubleData.length - index); System.arraycopy(data, 0, result, index,
	 * data.length);
	 * 
	 * return result; }
	 */

	/*
	 * private float[] insertRecord(float[] data, int index) { float[] result =
	 * new float[this.floatData.length + data.length];
	 * 
	 * System.arraycopy(this.floatData, 0, result, 0, index);
	 * System.arraycopy(this.floatData, index, result, index + data.length,
	 * this.floatData.length - index); System.arraycopy(data, 0, result, index,
	 * data.length);
	 * 
	 * return result; }
	 */

	private void insertRecord(float[] data, int index) {
		if (index > 0) {
			this.floatList.add(data);
		} else {
			this.floatList.add(index, data);
		}
	}

	// datasamples; /* Data samples, 'samplecount' of type 'sampletype'*/

	public void setTimeseries(Timeseries ts) {
		this.timeseries = ts;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public float getSamplerate() {
		return samplerate;
	}

	public void setSamplerate(float samplerate) {
		this.samplerate = samplerate;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public Type getType() {
		return type;
	}

	public List<Integer> getIntData() {
		List<Integer> l = new ArrayList<Integer>();
		for (int[] i : this.intList) {
			for (int n : i) {
				l.add(n);
			}
		}
		return l;
	}

	public List<Short> getShortData() {
		List<Short> l = new ArrayList<Short>();
		for (short[] i : this.shortList) {
			for (short n : i) {
				l.add(n);
			}
		}
		return l;
	}

	public List<Float> getFloatData() {
		List<Float> l = new ArrayList<Float>();
		for (float[] i : this.floatList) {
			for (float n : i) {
				l.add(n);
			}
		}
		return l;
	}

	public List<Double> getDoubleData() {
		List<Double> l = new ArrayList<Double>();
		for (double[] i : this.doubleList) {
			for (double n : i) {
				l.add(n);
			}
		}
		return l;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	public int compareTo(Segment ts) {
		if (ts == null) {
			return -1;
		}
		if (this.startTime.after(ts.getStartTime())) {
			return 1;
		} else if (this.startTime.before(ts.getStartTime())) {
			return -1;
		} else {
			return 0;
		}
	}

}
