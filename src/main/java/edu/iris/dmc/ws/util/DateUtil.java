package edu.iris.dmc.ws.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	private static final String SHORT_DATE = "yyyy-MM-dd";
	private static final String[] patterns = new String[]{"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd' 'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd' 'HH:mm:ss.SSS", "yyyy,DDD,HH:mm:ss", "yyyy,DDD,HH:mm:ss"};

	public DateUtil() {
	}

	public static Date parseAny(String dateString) throws ParseException {
		if (dateString != null && !dateString.trim().isEmpty()) {
			for (String pattern : patterns) {
				try {
					if (dateString.length() <= pattern.length()) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
						simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						return simpleDateFormat.parse(dateString);
					}
				} catch (ParseException var6) {
				}
			}

			throw new ParseException("Unable to parse the date: " + dateString, -1);
		} else {
			return null;
		}
	}

	public static Date parse(String string, String pattern) throws ParseException {
		if (string == null) {
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sdf.parse(string);
		}
	}

	public static String format(Date date) {
		if (date == null) {
			return null;
		} else {
			String LONGDATE = "yyyy-MM-dd'T'HH:mm:ss";
			return format(date, LONGDATE);
		}
	}

	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		} else if (pattern == null) {
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sdf.format(date);
		}
	}

	public static String now(String pattern) {
		if (pattern == null) {
			return null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sdf.format(System.currentTimeMillis());
		}
	}
}
