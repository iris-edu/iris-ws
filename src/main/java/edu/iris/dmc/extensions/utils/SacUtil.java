
package edu.iris.dmc.extensions.utils;

import edu.iris.dmc.extensions.entities.Trace;
import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

import java.util.Calendar;
import java.util.TimeZone;

public class SacUtil {
	public SacUtil() {
	}

	public static SacTimeSeries makeSac(Trace trace) throws Exception {
		if (trace == null) {
			return null;
		} else if (trace.getData() == null) {
			return null;
		} else if (trace.getSampleCount() == 0) {
			return null;
		} else {
			SacHeader sach = new SacHeader();
			sach.setBigEndian();
			sach.setKnetwk(trace.getNetwork());
			sach.setKstnm(trace.getStation());
			sach.setKhole(trace.getLocation());
			sach.setKcmpnm(trace.getChannel());
			sach.setNvhdr(6);
			sach.setLeven(1);
			sach.setIftype(1);
			sach.setDelta(1.0F / (float)trace.getSampleRate());
			sach.setNpts(trace.getSampleCount());
			sach.setStla((float)trace.getLatitude());
			sach.setStlo((float)trace.getLongitude());
			sach.setStel((float)trace.getElevation());
			sach.setStdp((float)trace.getDepth());
			sach.setCmpaz((float)trace.getAzimuth());
			sach.setCmpinc((float)trace.getDip());
			String instrument = trace.getInstrument();
			if (instrument.length() > 8) {
				instrument = instrument.substring(0, 8);
			}

			sach.setKinst(instrument);
			sach.setScale((float)trace.getSensitivity());
			TimeZone tz = TimeZone.getTimeZone("UTC");
			Calendar cal = Calendar.getInstance(tz);
			cal.setTime(trace.getStartTime());
			sach.setNzyear(cal.get(1));
			sach.setNzjday(cal.get(6));
			sach.setNzhour(cal.get(11));
			sach.setNzmin(cal.get(12));
			sach.setNzsec(cal.get(13));
			int msec = cal.get(14);
			sach.setNzmsec(msec);
			float nanos = (float)(trace.getStartTime().getNanos() - msec * 1000000);
			float submsec = nanos / 1000.0F;
			sach.setB(submsec / 1000000.0F);
			float e = (float)((double)(trace.getSampleCount() - 1) / trace.getSampleRate() + (double)(submsec / 1000000.0F));
			sach.setE(e);
			return new SacTimeSeries(sach, trace.getAsFloat());
		}
	}

	public static String getSacFilename(Trace trace) {
		StringBuffer sb = new StringBuffer();
		sb.append(trace.getNetwork() + "." + trace.getStation() + "." + trace.getLocation() + "." + trace.getChannel() + ".");
		if (trace.getQuality() == null) {
			sb.append("_.");
		} else {
			sb.append(trace.getQuality() + ".");
		}

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(trace.getStartTime());
		sb.append(cal.get(1) + ",");
		sb.append(String.format("%02d", cal.get(6)) + ",");
		sb.append(String.format("%02d", cal.get(11)) + ":");
		sb.append(String.format("%02d", cal.get(12)) + ":");
		sb.append(String.format("%02d", cal.get(13)) + ".SAC");
		return sb.toString();
	}
}
