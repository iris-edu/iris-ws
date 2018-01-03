package edu.iris.dmc.extensions.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.sql.Timestamp;

import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Segment.Type;
import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * This class handles waveform data and meta data in (currently) a very SAC
 * oriented way. It also contains methods for creating an IRIS standard SAC file
 * name and a SeisFile SAC object.
 * 
 */
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

	// private float[] data;

	private List<Number> data = new ArrayList<Number>();

	private Type dataType;

	private Sacpz sacpz;

	// ACCESSORS

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String s) {
		network = s;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String s) {
		station = s;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String s) {
		location = s;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String s) {
		channel = s;
	}

	public Character getQuality() {
		return quality;
	}

	public void setQuality(Character c) {
		quality = c;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double d) {
		latitude = d;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double d) {
		longitude = d;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double d) {
		elevation = d;
	}

	public double getDepth() {
		return depth;
	}

	public void setDepth(double d) {
		depth = d;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double d) {
		azimuth = d;
	}

	public double getDip() {
		return dip;
	}

	public void setDip(double d) {
		dip = d;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String s) {
		instrument = s;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(double d) {
		sensitivity = d;
	}

	public double getSensitivityFrequency() {
		return sensitivityFrequency;
	}

	public void setSensitivityFrequency(double d) {
		sensitivityFrequency = d;
	}

	public String getSensitivityUnits() {
		return sensitivityUnits;
	}

	public void SetSensitivityUnits(String s) {
		sensitivityUnits = s;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(double d) {
		sampleRate = d;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int i) {
		sampleCount = i;
	}

	public List<Number> getData() {
		return data;
	};

	public void setData(List<Number> af) {
		data = af;
	};

	// CONSTRUCTORS

	public Sacpz getSacpz() {
		return sacpz;
	}

	public void setSacpz(Sacpz sacpz) {
		this.sacpz = sacpz;
	}

	public Type getDataType() {
		return this.dataType;
	}

	// Base constructor
	public Trace() {
	}

	// Create a new Trace object based off of a Metadata object, a Segment
	// object which
	// contains the waveform data and a quality character.

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
		this.sampleRate = s.getSamplerate();

		// Convert all types to floats.
		this.dataType = s.getType();

		switch (dataType) {

		case DOUBLE:
			List<Double> d = s.getDoubleData();
			for (double i : d) {
				this.data.add(i);
			}
			break;
		case FLOAT:
			List<Float> f = s.getFloatData();
			for (float i : f) {
				this.data.add(i);
			}
			break;
		case INTEGER:
			List<Integer> l = s.getIntData();
			for (int i : l) {
				this.data.add(i);
			}
			break;
		case SHORT:
			List<Short> sl = s.getShortData();
			for (short i : sl) {
				this.data.add(i);
			}
			break;
		default:
			System.err.println("Unknown data type in writeSacFile");
		}

	}

	public int[] getAsInt() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		}
		int[] temp = new int[size];
		for (int i = 0; i < size; i++) {
			temp[i] = this.data.get(i).intValue();
		}
		return temp;
	}

	public short[] getAsShort() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		}
		short[] temp = new short[size];
		for (int i = 0; i < size; i++) {
			temp[i] = this.data.get(i).shortValue();
		}
		return temp;
	}

	public float[] getAsFloat() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		}
		float[] temp = new float[size];
		for (int i = 0; i < size; i++) {
			temp[i] = this.data.get(i).floatValue();
		}
		return temp;
	}

	public double[] getAsDouble() {
		int size = this.data.size();
		if (size == 0) {
			return null;
		}
		double[] temp = new double[size];
		for (int i = 0; i < size; i++) {
			temp[i] = this.data.get(i).doubleValue();
		}
		return temp;
	}

	// Utility function for pretty printing.
	public String toString() {
		final DateFormat sdfm = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss:SSS");

		StringBuffer sb = new StringBuffer();

		sb.append(network + "," + station + "," + location + "," + channel
				+ ",");
		sb.append(latitude + "," + longitude + "," + elevation + "," + depth
				+ "," + azimuth + "," + dip + ",");

		sb.append(instrument + "," + sensitivity + "," + sensitivityFrequency
				+ "," + sensitivityUnits + "," + sampleRate + ",");
		sb.append(sdfm.format(startTime) + "," + sdfm.format(endTime) + "\n");
		if (data != null) {
			sb.append(" " + data.size() + " samples");
		} else {
			sb.append(" NO DATA");
		}
		return sb.toString();
	}

}
