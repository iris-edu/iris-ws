package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleCriteria implements Criteria, Formattable {

	private Map<String, String> params = new HashMap<>();

	protected SimpleCriteria() {

	}

	public SimpleCriteria setStartTime(Date startTime) {
		if (startTime == null) {
			return this;
		}
		this.add("starttime", startTime);
		return this;
	}

	public SimpleCriteria setEndTime(Date endTime) {
		if (endTime == null) {
			return this;
		}
		this.add("endtime", endTime);
		return this;

	}

	public SimpleCriteria setUpdatedAfter(Date date) {
		this.add("updatedafter", date);
		return this;
	}

	public SimpleCriteria add(String key, Integer number) {
		this.add(key, number.toString());
		return this;
	}

	public SimpleCriteria add(String key, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		this.params.put(key, sdf.format(date));
		return this;
	}

	public SimpleCriteria add(String key, String value) {
		if (value == null) {
			return this;
		}
		String text = this.params.get(key);
		if (text != null && !text.isEmpty()) {
			value = text + "," + value;
		}
		this.params.put(key, value);
		return this;
	}

	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public String toUrlQuery() throws CriteriaException, UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : this.params.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			builder.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return builder.toString();
	}

	@Override
	public void formatTo(Formatter arg0, int arg1, int arg2, int arg3) {

	}

}
