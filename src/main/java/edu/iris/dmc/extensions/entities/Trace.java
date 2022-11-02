
package edu.iris.dmc.extensions.entities;

import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Segment.Type;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Trace {
	private String network;
	private String station;
	private String location;
	private String channel;
	private Character quality;
	private double latitude;
	private double longitude;
	private double elevation;
	private double depth;
	private double azimuth;
	private double dip;
	private String instrument;
	private double sensitivity;
	private double sensitivityFrequency;
	private String sensitivityUnits;
	private Timestamp startTime;
	private Timestamp endTime;
	private double sampleRate;
	private int sampleCount;
	private List<Number> data = new ArrayList<>();
	private Type dataType;
	private Sacpz sacpz;

	public String getNetwork() {
		return this.network;
	}

	public void setNetwork(String s) {
		this.network = s;
	}

	public String getStation() {
		return this.station;
	}

	public void setStation(String s) {
		this.station = s;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String s) {
		this.location = s;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String s) {
		this.channel = s;
	}

	public Character getQuality() {
		return this.quality;
	}

	public void setQuality(Character c) {
		this.quality = c;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double d) {
		this.latitude = d;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double d) {
		this.longitude = d;
	}

	public double getElevation() {
		return this.elevation;
	}

	public void setElevation(double d) {
		this.elevation = d;
	}

	public double getDepth() {
		return this.depth;
	}

	public void setDepth(double d) {
		this.depth = d;
	}

	public double getAzimuth() {
		return this.azimuth;
	}

	public void setAzimuth(double d) {
		this.azimuth = d;
	}

	public double getDip() {
		return this.dip;
	}

	public void setDip(double d) {
		this.dip = d;
	}

	public String getInstrument() {
		return this.instrument;
	}

	public void setInstrument(String s) {
		this.instrument = s;
	}

	public double getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(double d) {
		this.sensitivity = d;
	}

	public double getSensitivityFrequency() {
		return this.sensitivityFrequency;
	}

	public void setSensitivityFrequency(double d) {
		this.sensitivityFrequency = d;
	}

	public String getSensitivityUnits() {
		return this.sensitivityUnits;
	}

	public void SetSensitivityUnits(String s) {
		this.sensitivityUnits = s;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public double getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(double d) {
		this.sampleRate = d;
	}

	public int getSampleCount() {
		return this.sampleCount;
	}

	public void setSampleCount(int i) {
		this.sampleCount = i;
	}

	public List<Number> getData() {
		return this.data;
	}

	public void setData(List<Number> af) {
		this.data = af;
	}

	public Sacpz getSacpz() {
		return this.sacpz;
	}

	public void setSacpz(Sacpz sacpz) {
		this.sacpz = sacpz;
	}

	public Type getDataType() {
		return this.dataType;
	}

	public Trace() {
	}

	public Trace(Metadata md, Segment s, Character qual) {
		this.quality = qual;
		this.network = md.network;
		this.station = md.station;
		this.location = md.location;
		this.channel = md.channel;
		this.latitude = md.lat;
		this.longitude = md.lon;
		this.elevation = md.elev;
		this.depth = md.depth;
		this.azimuth = md.azimuth;
		this.dip = md.dip;
		this.instrument = md.instrument;
		this.sensitivity = md.sensitivity;
		this.sensitivityFrequency = md.sensFreq;
		this.sensitivityUnits = md.sensUnits;
		this.startTime = s.getStartTime();
		this.endTime = s.getEndTime();
		this.sampleCount = s.getSampleCount();
		this.sampleRate = (double)s.getSamplerate();
		this.dataType = s.getType();
		switch(this.dataType) {
			case DOUBLE:
				List<Double> d = s.getDoubleData();
				this.data.addAll(d);
				return;
			case FLOAT:
				List<Float> f = s.getFloatData();
				this.data.addAll(f);
				return;
			case INTEGER:
				List<Integer> l = s.getIntData();
				this.data.addAll(l);
				return;
			case SHORT:
				this.data.addAll(s.getShortData());
				return;
			default:
				System.err.println("Unknown data type in writeSacFile");
		}
	}

	public int[] getAsInt() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		} else {
			int[] temp = new int[size];

			for(int i = 0; i < size; ++i) {
				temp[i] = ((Number)this.data.get(i)).intValue();
			}

			return temp;
		}
	}

	public short[] getAsShort() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		} else {
			short[] temp = new short[size];

			for(int i = 0; i < size; ++i) {
				temp[i] = ((Number)this.data.get(i)).shortValue();
			}

			return temp;
		}
	}

	public float[] getAsFloat() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		} else {
			float[] temp = new float[size];

			for(int i = 0; i < size; ++i) {
				temp[i] = ((Number)this.data.get(i)).floatValue();
			}

			return temp;
		}
	}

	public double[] getAsDouble() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		} else {
			double[] temp = new double[size];

			for(int i = 0; i < size; ++i) {
				temp[i] = ((Number)this.data.get(i)).doubleValue();
			}

			return temp;
		}
	}

	public String toString() {
		DateFormat sdfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS");
		StringBuffer sb = new StringBuffer();
		sb.append(this.network + "," + this.station + "," + this.location + "," + this.channel + ",");
		sb.append(this.latitude + "," + this.longitude + "," + this.elevation + "," + this.depth + "," + this.azimuth + "," + this.dip + ",");
		sb.append(this.instrument + "," + this.sensitivity + "," + this.sensitivityFrequency + "," + this.sensitivityUnits + "," + this.sampleRate + ",");
		sb.append(sdfm.format(this.startTime) + "," + sdfm.format(this.endTime) + "\n");
		if (this.data != null) {
			sb.append(" " + this.data.size() + " samples");
		} else {
			sb.append(" NO DATA");
		}

		return sb.toString();
	}
}
