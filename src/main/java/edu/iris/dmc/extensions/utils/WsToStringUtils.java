package edu.iris.dmc.extensions.utils;

import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;

public final class WsToStringUtils {
	
	public static String timeSeriesToString(Timeseries ts) {
		if (ts == null) return "";
		StringBuilder sb = new StringBuilder();
		sb.append("TIMESERIES: ");
		sb.append(" Network  : " + ts.getNetworkCode());
		sb.append(" Station  : " + ts.getStationCode());
		sb.append(" Channel  : " + ts.getChannelCode());
		sb.append(" Location : " + ts.getLocation());
		sb.append(" Quality  : " + ts.getDataQuality());
		sb.append("\n");
		for (Segment s: ts.getSegments()) {
			sb.append(segmentToString(s));
		}
		
		 return sb.toString();
	}
	
	public static String segmentToString(Segment s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(" SEGMENT: ");
		sb.append("  Type:         " + s.getType());
		sb.append("  Sample Rate:  " + s.getSamplerate());
		sb.append("  Count:        " + s.getSampleCount());
		sb.append("  Start Time:   " + s.getStartTime());
		sb.append("  StartTime ns: " + s.getStartTime().getNanos());
		sb.append("  End Time:     " + s.getEndTime());
		sb.append("  End Time ns: " + s.getEndTime().getNanos() +  "\n");
		return sb.toString();
	}
}
