package edu.iris.dmc.extensions.utils;

import java.util.Calendar;
import java.util.TimeZone;

import edu.iris.dmc.extensions.entities.Trace;
import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

public class SacUtil {
	
	/**
	 * Creates a SacTimeSeries object (This is a SAC representation of the data
	 * via Seisfile objects [SacHeader, SacTimeSeries]
	 */
	public static SacTimeSeries makeSac(Trace trace) throws Exception {
		if (trace == null) {
			return null;
		}
		if (trace.getData() == null)
			return null;
		if (trace.getSampleCount() == 0)
			return null;

		SacHeader sach = new SacHeader();
		sach.setBigEndian();// .setLittleEndian();

		sach.setKnetwk(trace.getNetwork());
		sach.setKstnm(trace.getStation());
		sach.setKhole(trace.getLocation());
		sach.setKcmpnm(trace.getChannel());

		sach.setNvhdr(6); // Header V6
		sach.setLeven(1); // Evenly spaced data
		sach.setIftype(1); // Time-series data

		sach.setDelta(1.f / (float) trace.getSampleRate());
		sach.setNpts(trace.getSampleCount());

		// Some such metadata insertion
		sach.setStla((float) trace.getLatitude());
		sach.setStlo((float) trace.getLongitude());
		sach.setStel((float) trace.getElevation());
		sach.setStdp((float) trace.getDepth());
		sach.setCmpaz((float) trace.getAzimuth());
		sach.setCmpinc((float) trace.getDip());

		// Truncate the instrument string if necessary
		String instrument = trace.getInstrument();
		if (instrument.length() > 8) {
			instrument = instrument.substring(0, 8);
		}
		sach.setKinst(instrument);
		sach.setScale((float) trace.getSensitivity());

		// Do the reference time computation
		TimeZone tz = TimeZone.getTimeZone("UTC");
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(trace.getStartTime());

		sach.setNzyear(cal.get(Calendar.YEAR));
		sach.setNzjday(cal.get(Calendar.DAY_OF_YEAR));
		sach.setNzhour(cal.get(Calendar.HOUR_OF_DAY));
		sach.setNzmin(cal.get(Calendar.MINUTE));
		sach.setNzsec(cal.get(Calendar.SECOND));
		int msec = cal.get(Calendar.MILLISECOND);
		sach.setNzmsec(msec);

		float nanos = trace.getStartTime().getNanos() - msec * 1000000;
		float submsec = nanos / 1000.f;
		sach.setB(submsec / 1000000.f);

		float e = (float) ((trace.getSampleCount() - 1) / trace.getSampleRate() + (submsec / 1000000.f));
		sach.setE(e);

		return new SacTimeSeries(sach, trace.getAsFloat());
	}

	/**
	 * Creates an IRIS 'approved' mseed2sac type file name for this Trace data
	 */
	public static String getSacFilename(Trace trace) {
		StringBuffer sb = new StringBuffer();

		sb.append(trace.getNetwork() + "." + trace.getStation() + "."
				+ trace.getLocation() + "." + trace.getChannel() + ".");
		if (trace.getQuality() == null) {
			sb.append("_.");
		} else {
			sb.append(trace.getQuality() + ".");
		}

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(trace.getStartTime());

		sb.append(cal.get(Calendar.YEAR) + ",");
		sb.append(String.format("%02d", cal.get(Calendar.DAY_OF_YEAR)) + ",");
		sb.append(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":");
		sb.append(String.format("%02d", cal.get(Calendar.MINUTE)) + ":");
		sb.append(String.format("%02d", cal.get(Calendar.SECOND)) + ".SAC");
		return sb.toString();
	}
}
