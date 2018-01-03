package edu.iris.dmc.extensions.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;

/**
 * A class in which to store relevant meta data (channel epochs to be exact)
 * returned by the Station web service. This data can be used with the waveform
 * service to ensure that requested data segments don't span channel epochs.
 * Also used in the population of the Trace elements once the waveform data is
 * acquired.
 * 
 */
public class Metadata {

	static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	protected String network;
	protected String station;
	protected String location;
	protected String channel;
	protected Date startDate;
	protected Date endDate;
	protected double lat;
	protected double lon;
	protected double elev;
	protected double depth;
	protected double azimuth;
	protected double dip;
	protected String instrument;
	protected double sampleRate;
	protected double sensitivity;
	protected double sensFreq;
	protected String sensUnits;
	protected Character quality;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date t) {
		startDate = t;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date t) {
		endDate = t;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double d) {
		lat = d;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double d) {
		lon = d;
	}

	public double getElev() {
		return elev;
	}

	public void setElev(double d) {
		elev = d;
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

	public double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(double d) {
		sampleRate = d;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(double d) {
		sensitivity = d;
	}

	public double getSensFreq() {
		return sensFreq;
	}

	public void setSensFreq(double d) {
		sensFreq = d;
	}

	public Character getQuality() {
		return quality;
	}

	public void setQuality(Character c) {
		quality = c;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(network + "," + station + "," + location + "," + channel
				+ ",");
		sb.append(lat + "," + lon + "," + elev + "," + depth + "," + azimuth
				+ "," + dip + ",");

		sb.append(instrument + "," + sensitivity + "," + sensFreq + ","
				+ sensUnits + "," + sampleRate);

		if (startDate != null) {
			sb.append("," + sdf.format(startDate));
		}

		if (endDate != null) {
			sb.append("," + sdf.format(endDate));
		}
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Parses the returned network objects from the station service into a
	 * simple list of Metadata objects.
	 * 
	 * @param networks
	 * @return List<Metadata>
	 */
	public static List<Metadata> parseMetadata(List<Network> networks) {
		if (networks == null)
			return null;

		List<Metadata> lmd = new ArrayList<Metadata>();

		for (Network n : networks) {
			for (Station s : n.getStations()) {
				String network = n.getCode();
				String station = s.getCode();
				for (Channel c : s.getChannels()) {

					Metadata md = new Metadata();
					md.network = network.trim();
					md.station = station.trim();

					String location = c.getLocationCode();
					if ((location == null) || (location.trim().equals("")))
						location = "--";
					md.location = location.trim();
					md.channel = c.getCode().trim();

					md.lat = c.getLatitudeValue();
					md.lon = c.getLongitudeValue();
					md.elev = c.getElevationValue();
					md.depth = c.getDepthValue();
					md.azimuth = c.getAzimuthValue();
					md.dip = c.getDipValue();// + 90.f;

					md.instrument = c.getSensor().getType();

					Sensitivity sens = c.getResponse()
							.getInstrumentSensitivity();
					if (sens != null) {
						if (sens.getValue() != null) {
							md.sensitivity = sens.getValue();
						}
						if (sens.getFrequency() != null) {
							md.sensFreq = sens.getFrequency();
						}
						if (sens.getInputUnits() != null) {
							md.sensUnits = sens.getInputUnits().getName();
						}
						if (c.getSampleRateValue() != null) {
							md.sampleRate = c.getSampleRateValue();
						}
					}

					if (c.getStartDate() != null) {
						md.startDate = c.getStartDate().toGregorianCalendar()
								.getTime();
					}
					if (c.getEndDate() != null) {
						md.endDate = c.getEndDate().toGregorianCalendar()
								.getTime();
					}
					lmd.add(md);
				}
			}
		}
		return lmd;
	}
}
