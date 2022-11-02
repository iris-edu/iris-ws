package edu.iris.dmc.event.model;

import edu.iris.quake.model.TimeQuantity;
import edu.iris.quake.model.WaveformStreamID;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class Pick {

	private edu.iris.quake.model.Pick basePick;

	private String pickId;
	private Date time;
	
	private String network;
	private String station;
	private String channel;
	private String location;

	public Pick() {
	}

	public Pick(edu.iris.quake.model.Pick basePick) {
		this.basePick = basePick;

		if (this.basePick != null) {
			this.pickId = this.basePick.getPublicID();

			for (JAXBElement element : basePick.getCommentOrTimeOrWaveformID()) {
				if ("time".equalsIgnoreCase(element.getName().getLocalPart())) {

					TimeQuantity tq = (TimeQuantity) element.getValue();
					for (JAXBElement e : tq
							.getValueOrUncertaintyOrLowerUncertainty()) {
						if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
							XMLGregorianCalendar t = (XMLGregorianCalendar) e
									.getValue();
							this.time = t.toGregorianCalendar().getTime();
						}
					}
				}
				if ("waveformid".equalsIgnoreCase(element.getName().getLocalPart())) {
					WaveformStreamID waveFormId = (WaveformStreamID) element.getValue();
					this.channel=waveFormId.getChannelCode();
					this.location=waveFormId.getLocationCode();
					this.network=waveFormId.getNetworkCode();
					this.station=waveFormId.getStationCode();
				}
			}
		}

	}

	public String getPickId() {
		return pickId;
	}

	public void setPickId(String pickId) {
		this.pickId = pickId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Pick [pickId=" + pickId + ", time=" + time + ", network="
				+ network + ", station=" + station + ", channel=" + channel
				+ ", location=" + location + "]";
	}
	
	

}
