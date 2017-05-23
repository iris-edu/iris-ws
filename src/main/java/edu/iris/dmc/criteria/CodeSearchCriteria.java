package edu.iris.dmc.criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CodeSearchCriteria extends SimpleCriteria {

	private List<String> selections = new ArrayList<String>();

	protected CodeSearchCriteria(String network, String station, String location, String channel, Date start,
			Date end) {
		super();
		this.addNetwork(network);
		this.addStation(station);
		this.addLocation(location);
		this.addChannel(channel);
		this.setStartTime(start);
		this.setEndTime(end);
	}

	public static CodeSearchCriteria of(String network, String station, String location, String channel, Date start,
			Date end) {
		return new CodeSearchCriteria(network, station, location, channel, start, end);
	}

	public CodeSearchCriteria addDistinct(String network, String station, String location, String channel) {
		StringBuilder builder = new StringBuilder();
		builder.append(network);
		builder.append(" ").append(station);
		builder.append(" ").append(location);
		builder.append(" ").append(channel);
		this.selections.add(builder.toString());
		return this;
	}

	public CodeSearchCriteria addNetwork(String code) {
		this.add("network", code);
		return this;
	}

	public CodeSearchCriteria addStation(String code) {
		this.add("station", code);
		return this;
	}

	public CodeSearchCriteria addChannel(String code) {
		this.add("channel", code);
		return this;
	}

	public CodeSearchCriteria addLocation(String code) {
		this.add("location", code);
		return this;
	}

	public CodeSearchCriteria setEndBefore(Date date) {
		this.add("endbefore", date);
		return this;
	}

	public CodeSearchCriteria setEndAfter(Date date) {
		this.add("endafter", date);
		return this;
	}

	public CodeSearchCriteria setStartBefore(Date date) {
		this.add("startbefore", date);
		return this;
	}

	public CodeSearchCriteria setStartAfter(Date date) {
		this.add("startafter", date);
		return this;
	}

	public CodeSearchCriteria format(OutputFormat format) {
		return this;
	}

	public CodeSearchCriteria level(OutputLevel level) {
		return this;
	}

	public CodeSearchCriteria matchTimeSeries(boolean matchTimeSeries) {
		return this;
	}

	public CodeSearchCriteria includeAvailability(boolean includeAvailability) {
		return this;
	}

	public CodeSearchCriteria includeRestricted(boolean includeRestricted) {
		return this;
	}
}
