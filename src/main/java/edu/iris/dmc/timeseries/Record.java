package edu.iris.dmc.timeseries;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import edu.sc.seis.seisFile.mseed.Btime;

public class Record {
	private Timestamp startTime;
	private Timestamp endTime;
	private float sampleRate;
	private int numberOfSamples;
	private int[] location;
	private Timestamp expectedNextSampleTime;

	public Record(Timestamp startTime, Timestamp endTime, float sampleRate,
			int numberOfSamples, int[] location) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.sampleRate = sampleRate;
		this.numberOfSamples = numberOfSamples;
		this.location = location;
		
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime.getTime() + Double.doubleToLongBits(sampleRate/(numberOfSamples)));
		this.expectedNextSampleTime = new Timestamp(cal.getTime().getTime());
	}

	public Timestamp getExpectedNextSampleTime() {
		return expectedNextSampleTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int[] getLocation() {
		return location;
	}

	public void setLocation(int[] location) {
		this.location = location;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

}
