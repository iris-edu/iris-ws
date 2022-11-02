package edu.iris.dmc.criteria;

import edu.iris.dmc.ws.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class StationCriteria implements Criteria {
	private Date startBefore;
	private Date startAfter;
	private Date endBefore;
	private Date endAfter;
	private Date startTime;
	private Date endTime;
	private Date updatedAfter;
	private String netCode;
	private String staCode;
	private String chanCode;
	private String locCode;
	private Double minimumLatitude;
	private Double maximumLatitude;
	private Double minimumLongitude;
	private Double maximumLongitude;
	private Double latitude;
	private Double longitude;
	private Double minimumRadius;
	private Double maximumRadius;
	private OutputLevel level;
	private OutputFormat format;
	private boolean includeAvailability;
	private boolean includeRestricted;
	private boolean matchTimeSeries;
	private Map<String, String> params;

	public StationCriteria() {
		this.format = OutputFormat.XML;
		this.includeAvailability = false;
		this.includeRestricted = true;
		this.matchTimeSeries = false;
		this.params = new HashMap<>();
	}

	public StationCriteria setMinimumLatitude(Double minimumLatitude) {
		this.minimumLatitude = minimumLatitude;
		return this;
	}

	public StationCriteria setMaximumLatitude(Double maximumLatitude) {
		this.maximumLatitude = maximumLatitude;
		return this;
	}

	public StationCriteria setMinimumLongitude(Double minimumLongitude) {
		this.minimumLongitude = minimumLongitude;
		return this;
	}

	public StationCriteria setMaximumLongitude(Double maximumLongitude) {
		this.maximumLongitude = maximumLongitude;
		return this;
	}

	public StationCriteria setUpdatedAfter(Date updatedAfter) {
		this.updatedAfter = updatedAfter;
		return this;
	}

	public StationCriteria setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public StationCriteria setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	public StationCriteria setMinimumRadius(Double minimumRadius) {
		this.minimumRadius = minimumRadius;
		return this;
	}

	public StationCriteria setMaximumRadius(Double maximumRadius) {
		this.maximumRadius = maximumRadius;
		return this;
	}

	public StationCriteria setStartTime(Date start) {
		this.startTime = start;
		return this;
	}

	public StationCriteria setEndTime(Date end) {
		this.endTime = end;
		return this;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public boolean isIncludeAvailability() {
		return this.includeAvailability;
	}

	public void setIncludeAvailability(boolean includeAvailability) {
		this.includeAvailability = includeAvailability;
	}

	public boolean isIncludeRestricted() {
		return this.includeRestricted;
	}

	public void setIncludeRestricted(boolean includeRestricted) {
		this.includeRestricted = includeRestricted;
	}

	public boolean isMatchTimeSeries() {
		return this.matchTimeSeries;
	}

	public void setMatchTimeSeries(boolean matchTimeSeries) {
		this.matchTimeSeries = matchTimeSeries;
	}

	public OutputLevel getLevel() {
		return this.level;
	}

	public void setLevel(OutputLevel level) {
		this.level = level;
	}

	public OutputFormat getFormat() {
		return this.format;
	}

	public void setFormat(OutputFormat format) {
		this.format = format;
	}

	public List<String> toUrlParams(OutputLevel level) throws CriteriaException {
		this.level = level;
		return this.toUrlParams();
	}

	public List<String> toUrlParams() throws CriteriaException {
		StringBuilder string = new StringBuilder();
		boolean and = false;
		if (this.netCode != null) {
			string.append("net=").append(this.netCode);
			and = true;
		}

		if (this.staCode != null) {
			if (and) {
				string.append("&");
			}

			string.append("sta=").append(this.staCode);
			and = true;
		}

		if (this.chanCode != null) {
			if (and) {
				string.append("&");
			}

			string.append("cha=").append(this.chanCode);
			and = true;
		}

		if (this.locCode != null) {
			if (and) {
				string.append("&");
			}

			string.append("loc=").append(this.locCode);
			and = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (this.updatedAfter != null) {
			if (and) {
				string.append("&");
			}

			string.append("updatedafter=").append(sdf.format(this.updatedAfter));
			and = true;
		}

		if (this.startTime != null) {
			if (and) {
				string.append("&");
			}

			string.append("starttime=");
			string.append(DateUtil.format(this.startTime));
			and = true;
		}

		if (this.endTime != null) {
			if (and) {
				string.append("&");
			}

			string.append("endtime=");
			string.append(DateUtil.format(this.endTime));
			and = true;
		}

		if (this.endBefore != null) {
			if (and) {
				string.append("&");
			}

			string.append("endbefore=").append(DateUtil.format(this.endBefore));
			and = true;
		}

		if (this.endAfter != null) {
			if (and) {
				string.append("&");
			}

			string.append("endafter=").append(DateUtil.format(this.endAfter));
			and = true;
		}

		if (this.startBefore != null) {
			if (and) {
				string.append("&");
			}

			string.append("startbefore=").append(DateUtil.format(this.startBefore));
			and = true;
		}

		if (this.startAfter != null) {
			if (and) {
				string.append("&");
			}

			string.append("startafter=").append(DateUtil.format(this.startAfter));
			and = true;
		}

		if (this.maximumRadius != null) {
			if (and) {
				string.append("&");
			}

			string.append("maxradius=").append(this.maximumRadius);
			and = true;
		}

		if (this.minimumRadius != null) {
			if (and) {
				string.append("&");
			}

			string.append("minradius=").append(this.minimumRadius);
			and = true;
		}

		if (this.latitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("latitude=").append(this.latitude);
			and = true;
		}

		if (this.longitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("longitude=").append(this.longitude);
			and = true;
		}

		if (this.minimumLatitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("minlatitude=").append(this.minimumLatitude);
			and = true;
		}

		if (this.maximumLatitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("maxlatitude=" + this.maximumLatitude);
			and = true;
		}

		if (this.minimumLongitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("minlongitude=").append(this.minimumLongitude);
			and = true;
		}

		if (this.maximumLongitude != null) {
			if (and) {
				string.append("&");
			}

			string.append("maxlongitude=" + this.maximumLongitude);
			and = true;
		}

		if (this.includeAvailability) {
			if (and) {
				string.append("&");
			}

			string.append("includeavailability=true");
			and = true;
		}

		if (!this.includeRestricted) {
			if (and) {
				string.append("&");
			}

			string.append("includerestricted=false");
			and = true;
		}

		if (this.matchTimeSeries) {
			if (and) {
				string.append("&");
			}

			string.append("matchtimeseries=true");
			and = true;
		}

		if (this.level != null) {
			if (and) {
				string.append("&");
			}

			string.append("level=" + this.level.toString());
			and = true;
		}

		if (!this.params.isEmpty()) {
			for(Iterator<Entry<String, String>> var4 = this.params.entrySet().iterator(); var4.hasNext(); and = true) {
				Entry<String, String> entry = var4.next();
				if (and) {
					string.append("&");
				}

				string.append((String) entry.getKey()).append("=").append((String) entry.getValue());
			}
		}

		if (and) {
			string.append("&");
		}

		string.append("format=").append(this.format.toString());
		List<String> l = new ArrayList<>();
		l.add(string.toString());
		return l;
	}

	public StationCriteria addStation(String staCode) {
		if (this.staCode == null) {
			this.staCode = staCode;
		} else {
			this.staCode = this.staCode + "," + staCode;
		}

		return this;
	}

	public StationCriteria addNetwork(String netCode) {
		if (this.netCode == null) {
			this.netCode = netCode;
		} else {
			this.netCode = this.netCode + "," + netCode;
		}

		return this;
	}

	public StationCriteria addChannel(String channel) {
		if (this.chanCode == null) {
			this.chanCode = channel;
		} else {
			this.chanCode = this.chanCode + "," + channel;
		}

		return this;
	}

	public StationCriteria addLocation(String location) {
		if (location != null) {
			location = location.replace(" ", "-");
		}

		if (this.locCode == null) {
			this.locCode = location;
		} else {
			this.locCode = this.locCode + "," + location;
		}

		return this;
	}

	public StationCriteria setEndBefore(Date date) {
		this.endBefore = date;
		return this;
	}

	public StationCriteria setEndAfter(Date date) {
		this.endAfter = date;
		return this;
	}

	public StationCriteria setStartBefore(Date date) {
		this.startBefore = date;
		return this;
	}

	public StationCriteria setStartAfter(Date date) {
		this.startAfter = date;
		return this;
	}

	public void add(String key, String value) {
		this.params.put(key, value);
	}

	public void reset() {
		this.startBefore = null;
		this.startAfter = null;
		this.endBefore = null;
		this.endAfter = null;
		this.startTime = null;
		this.endTime = null;
		this.updatedAfter = null;
		this.netCode = null;
		this.staCode = null;
		this.chanCode = null;
		this.locCode = null;
		this.minimumLatitude = null;
		this.maximumLatitude = null;
		this.minimumLongitude = null;
		this.maximumLongitude = null;
		this.latitude = null;
		this.longitude = null;
		this.minimumRadius = null;
		this.maximumRadius = null;
		this.level = OutputLevel.STATION;
		this.format = OutputFormat.XML;
		this.params.clear();
	}

	public Date getStartBefore() {
		return this.startBefore;
	}

	public Date getStartAfter() {
		return this.startAfter;
	}

	public Date getEndBefore() {
		return this.endBefore;
	}

	public Date getEndAfter() {
		return this.endAfter;
	}

	public Date getUpdatedAfter() {
		return this.updatedAfter;
	}

	public String[] getNetworks() {
		return this.netCode == null ? null : this.netCode.split(",");
	}

	public String[] getStations() {
		return this.staCode == null ? null : this.staCode.split(",");
	}

	public String[] getChannels() {
		return this.chanCode == null ? null : this.chanCode.split(",");
	}

	public String[] getLocations() {
		return this.locCode == null ? null : this.locCode.replace("-", " ").split(",");
	}

	public String toString() {
		return "StationCriteria [startBefore=" + this.startBefore + ", startAfter=" + this.startAfter + ", endBefore=" + this.endBefore + ", endAfter=" + this.endAfter + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", updatedAfter=" + this.updatedAfter + ", netCode=" + this.netCode + ", staCode=" + this.staCode + ", chanCode=" + this.chanCode + ", locCode=" + this.locCode + ", minimumLatitude=" + this.minimumLatitude + ", maximumLatitude=" + this.maximumLatitude + ", minimumLongitude=" + this.minimumLongitude + ", maximumLongitude=" + this.maximumLongitude + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", minimumRadius=" + this.minimumRadius + ", maximumRadius=" + this.maximumRadius + ", includeAvailability=" + this.includeAvailability + ", includeRestricted=" + this.includeRestricted + ", matchTimeSeries=" + this.matchTimeSeries + "]";
	}
}
