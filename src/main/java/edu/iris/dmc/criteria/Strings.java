package edu.iris.dmc.criteria;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Strings {

	public static boolean isNullOrEmpty(String text) {
		return text != null && !text.isEmpty();
	}

	public static String format(long time) {
		Timestamp ts = new Timestamp(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(ts);
	}

	public static String format(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}

	public static Date format(String text) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (text.length() > 19) {
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		}

		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.parse(text);
	}
}
