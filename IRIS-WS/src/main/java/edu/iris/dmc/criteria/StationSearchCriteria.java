package edu.iris.dmc.criteria;

import java.util.Date;

public class StationSearchCriteria extends SimpleGeoCriteria {

	private StationSearchCriteria(String network, String station, String location, String channel) {
		super();
		this.addNetwork(network);
		this.addStation(station);
		this.addLocation(location);
		this.addChannel(channel);
	}

	public static StationSearchCriteria of(String network, String station, String location, String channel) {
		return new StationSearchCriteria(network, station, location, channel);
	}

	public StationSearchCriteria addNetwork(String code) {
		if (code == null || code.isEmpty()) {
			return this;
		}
		this.add("network", code);
		return this;
	}

	public StationSearchCriteria addStation(String code) {
		if (code == null || code.isEmpty()) {
			return this;
		}
		this.add("station", code);
		return this;
	}

	public StationSearchCriteria addChannel(String code) {
		if (code == null || code.isEmpty()) {
			return this;
		}
		this.add("channel", code);
		return this;
	}

	public StationSearchCriteria addLocation(String code) {
		if (code == null || code.isEmpty()) {
			return this;
		}
		code = code.replace("  ", "--");
		this.add("location", code);
		return this;
	}

	public StationSearchCriteria setEndBefore(Date date) {
		this.add("endbefore", date);
		return this;
	}

	public StationSearchCriteria setEndAfter(Date date) {
		this.add("endafter", date);
		return this;
	}

	public StationSearchCriteria setStartBefore(Date date) {
		this.add("startbefore", date);
		return this;
	}

	public StationSearchCriteria setStartAfter(Date date) {
		this.add("startafter", date);
		return this;
	}

	public StationSearchCriteria format(OutputFormat format) {
		return this;
	}

	public StationSearchCriteria level(OutputLevel level) {
		this.add("level", level.name());
		return this;
	}

	public StationSearchCriteria matchTimeSeries(boolean matchTimeSeries) {
		return this;
	}

	public StationSearchCriteria includeAvailability(boolean includeAvailability) {
		return this;
	}

	public StationSearchCriteria includeRestricted(boolean includeRestricted) {
		return this;
	}

}
