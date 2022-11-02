package edu.iris.dmc.extensions.entities;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

	public Metadata() {
	}

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

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date t) {
		this.startDate = t;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date t) {
		this.endDate = t;
	}

	public double getLat() {
		return this.lat;
	}

	public void setLat(double d) {
		this.lat = d;
	}

	public double getLon() {
		return this.lon;
	}

	public void setLon(double d) {
		this.lon = d;
	}

	public double getElev() {
		return this.elev;
	}

	public void setElev(double d) {
		this.elev = d;
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

	public double getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(double d) {
		this.sampleRate = d;
	}

	public double getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(double d) {
		this.sensitivity = d;
	}

	public double getSensFreq() {
		return this.sensFreq;
	}

	public void setSensFreq(double d) {
		this.sensFreq = d;
	}

	public Character getQuality() {
		return this.quality;
	}

	public void setQuality(Character c) {
		this.quality = c;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.network + "," + this.station + "," + this.location + "," + this.channel + ",");
		sb.append(this.lat + "," + this.lon + "," + this.elev + "," + this.depth + "," + this.azimuth + "," + this.dip + ",");
		sb.append(this.instrument + "," + this.sensitivity + "," + this.sensFreq + "," + this.sensUnits + "," + this.sampleRate);
		if (this.startDate != null) {
			sb.append("," + sdf.format(this.startDate));
		}

		if (this.endDate != null) {
			sb.append("," + sdf.format(this.endDate));
		}

		sb.append("\n");
		return sb.toString();
	}

	public static List<Metadata> parseMetadata(List<Network> networks) {
		if (networks == null) {
			return null;
		} else {
			List<Metadata> lmd = new ArrayList();
			Iterator var2 = networks.iterator();

			while(var2.hasNext()) {
				Network n = (Network)var2.next();
				Iterator var4 = n.getStations().iterator();

				while(var4.hasNext()) {
					Station s = (Station)var4.next();
					String network = n.getCode();
					String station = s.getCode();

					Metadata md;
					for(Iterator var8 = s.getChannels().iterator(); var8.hasNext(); lmd.add(md)) {
						Channel c = (Channel)var8.next();
						md = new Metadata();
						md.network = network.trim();
						md.station = station.trim();
						String location = c.getLocationCode();
						if (location == null || location.trim().equals("")) {
							location = "--";
						}

						md.location = location.trim();
						md.channel = c.getCode().trim();
						md.lat = c.getLatitude()==null?0:c.getLatitude().getValue();
						md.lon = c.getLongitude()==null?0:c.getLongitude().getValue();
						md.elev = c.getElevation()==null?0:c.getElevation().getValue();
						md.depth = c.getDepth()==null?0:c.getDepth().getValue();
						md.azimuth = c.getAzimuth()==null?0:c.getAzimuth().getValue();
						md.dip = c.getDip()==null?0:c.getDip().getValue();
						md.instrument = c.getSensor()==null?null:c.getSensor().getType();
						Sensitivity sens = c.getResponse()==null?null:c.getResponse().getInstrumentSensitivity();
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
							md.startDate = c.getStartDate();
						}

						if (c.getEndDate() != null) {
							md.endDate = c.getEndDate();
						}
					}
				}
			}

			return lmd;
		}
	}
}
