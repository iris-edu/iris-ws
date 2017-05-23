package edu.iris.dmc.criteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBuilder {

	private Map<String, String> params = new HashMap<>();

	public RequestBuilder add(String key, String value) {
		String text = this.params.get(key);
		if (text != null && !text.isEmpty()) {
			value = text + "," + value;
		}
		this.params.put(key, value);
		return this;
	}

	public RequestBuilder addNetwork(String code) {
		this.add("network", code);
		return this;
	}

	public RequestBuilder addStation(String code) {
		this.add("station", code);
		return this;
	}

	public RequestBuilder addChannel(String code) {
		this.add("channel", code);
		return this;
	}

	public RequestBuilder addLocation(String code) {
		this.add("location", code);
		return this;
	}

	public List<String> toQueryParameters() {
		return null;
	}

	public Map<String, String> getParams() {
		return params;
	}

}
