//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.timeseries.model;

import edu.sc.seis.seisFile.mseed.Btime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
	private static Logger logger = Logger.getLogger("edu.iris.dmc.ws.seed.Util");

	public Util() {
	}

	public static Timestamp toTime(Btime bTime, byte activityFlags, int timeCorrection, byte microseconds) {
		long totalNanoSeconds = (long)(microseconds * 1000 + bTime.getTenthMilli() * 100000);
		Timestamp timeStamp = null;
		if (activityFlags == 0) {
			long timeCorrectionInNano = (long)timeCorrection * 100000L;
			totalNanoSeconds += timeCorrectionInNano;
		}

		int overflow = (int)(totalNanoSeconds / 1000000000L);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(1, bTime.getYear());
		cal.set(6, bTime.getDayOfYear());
		cal.set(11, bTime.getHour());
		cal.set(12, bTime.getMin());
		if (overflow == 0) {
			cal.set(13, bTime.getSec());
			timeStamp = new Timestamp(cal.getTime().getTime());
		} else {
			cal.set(13, bTime.getSec() + overflow);
			timeStamp = new Timestamp(cal.getTime().getTime());
		}

		timeStamp.setNanos(0);
		int remainderNanos = (int)(totalNanoSeconds % 1000000000L);
		if (remainderNanos != 0) {
			if (remainderNanos > 0) {
				timeStamp.setNanos(remainderNanos);
			} else {
				cal.set(13, cal.get(13) - 1);
				timeStamp = new Timestamp(cal.getTime().getTime());
				timeStamp.setNanos(1000000000 + remainderNanos);
			}
		}

		SimpleDateFormat dfm = new SimpleDateFormat("yyyy,D,HH:mm:ss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		return roundTime(timeStamp);
	}

	public static Timestamp roundTime(Timestamp timestamp) {
		int nanos = timestamp.getNanos();
		nanos = Math.round((float)(nanos / 1000)) * 1000;
		if (nanos != 0) {
			if (nanos > 0 && nanos < 1000000000) {
				timestamp.setNanos(nanos);
			} else {
				timestamp.setNanos(0);
				timestamp = new Timestamp(timestamp.getTime() + 1000L);
			}
		}

		return timestamp;
	}

	public static Timestamp addSeconds(Timestamp tstamp, double seconds) {
		Timestamp tstampClone = (Timestamp)tstamp.clone();
		if (seconds == 0.0D) {
			return tstampClone;
		} else {
			double dtmp = Math.abs(seconds);
			long isecs = (long)dtmp;
			double dfrac = dtmp - (double)isecs;
			int inano = (int)Math.round(dfrac * 1.0E9D);
			int tnano = tstampClone.getNanos();
			long tsecs = tstampClone.getTime() / 1000L;
			int newnano;
			long newsecs;
			if (seconds > 0.0D) {
				newnano = inano + tnano;
				newsecs = isecs + tsecs;
				if (newnano > 999999999) {
					newnano -= 1000000000;
					++newsecs;
				}
			} else {
				if (inano > tnano) {
					--tsecs;
					tnano += 1000000000;
				}

				newsecs = tsecs - isecs;
				newnano = tnano - inano;
			}

			tstampClone.setTime(newsecs * 1000L);
			tstampClone.setNanos(newnano);
			return tstampClone;
		}
	}

	public static boolean isRateTolerable(float a, float b) {
		return Math.abs(1.0D - (double)(a / b)) < 1.0E-4D;
	}

	public static boolean isRightTimeTolerable(Timestamp end, Timestamp expected, double period) {
		long temp = end.getTime() - expected.getTime();
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Comparing: " + end + " " + expected + "  " + period + "  " + temp);
		}

		return (double)Math.abs(temp) <= period;
	}

	public static boolean isLeftTimeTolerable(Timestamp start, Timestamp expected, double period) {
		return (double)Math.abs(start.getTime() - expected.getTime()) <= period;
	}
}
