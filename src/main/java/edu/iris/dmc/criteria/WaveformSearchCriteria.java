package edu.iris.dmc.criteria;

import java.text.ParseException;
import java.util.Date;

/**
 * WaveFormCriteria is a simple criteria builder to retrieve timeseries list
 * 
 */
public class WaveformSearchCriteria extends CodeSearchCriteria {

	private WaveformSearchCriteria(String network, String station, String location, String channel, Date start,
			Date end) {
		super(network, station, location, channel, start, end);
	}

	public static WaveformSearchCriteria of(String network, String station, String location, String channel,
			String start, String end) throws ParseException {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Start & End times are required");
		}
		Date startTime = Strings.format(start);
		Date endTime = Strings.format(end);
		return new WaveformSearchCriteria(network, station, location, channel, startTime, endTime);
	}

	public static WaveformSearchCriteria of(String network, String station, String location, String channel, Date start,
			Date end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Start & End times are required");
		}
		return new WaveformSearchCriteria(network, station, location, channel, start, end);
	}

	public WaveformSearchCriteria of() {
		return this;
	}

	public WaveformSearchCriteria minimumLength(Integer num) {
		this.add("minimumLength", num);
		return this;
	}

	public WaveformSearchCriteria longestOnly(Boolean bool) {
		this.add("longestonly", bool.toString());
		return this;
	}

	public WaveformSearchCriteria setQuality(Quality quality) {
		this.add("quality", quality.name());
		return this;
	}

}
