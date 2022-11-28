package edu.iris.dmc.timeseries.model;

import java.sql.Timestamp;
import java.util.Calendar;

public class Record {
	private Timestamp startTime;
	private Timestamp endTime;
	private float sampleRate;
	private int numberOfSamples;
	private int[] location;
	private Timestamp expectedNextSampleTime;

	public Record(Timestamp startTime, Timestamp endTime, float sampleRate, int numberOfSamples, int[] location) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.sampleRate = sampleRate;
		this.numberOfSamples = numberOfSamples;
		this.location = location;
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime.getTime() + Double.doubleToLongBits((double)(sampleRate / (float)numberOfSamples)));
		this.expectedNextSampleTime = new Timestamp(cal.getTime().getTime());
	}

	public Timestamp getExpectedNextSampleTime() {
		return this.expectedNextSampleTime;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int[] getLocation() {
		return this.location;
	}

	public void setLocation(int[] location) {
		this.location = location;
	}

	public float getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getNumberOfSamples() {
		return this.numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}
}
