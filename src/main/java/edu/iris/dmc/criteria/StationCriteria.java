package edu.iris.dmc.criteria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import edu.iris.dmc.ws.util.DateUtil;

/**
 * StationCriteria is a simple criteria builder to retrieve networks
 * 
 */
public class StationCriteria implements Criteria {

	private Date startBefore;
	private Date startAfter;
	private Date endBefore;
	private Date endAfter;

	// protected Date[] timeWindow;

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
	private OutputFormat format = OutputFormat.XML;

	private boolean includeAvailability = false;
	private boolean includeRestricted = true;
	private boolean matchTimeSeries = false;
	private Map<String, String> params = new HashMap<String, String>();

	/**
	 * Set minimum latitude in degrees
	 * 
	 * @param minimumLatitude
	 * @return
	 */
	public StationCriteria setMinimumLatitude(Double minimumLatitude) {
		this.minimumLatitude = minimumLatitude;
		return this;
	}

	/**
	 * Set maximum latitude in degrees
	 * 
	 * @param maximumLatitude
	 * @return
	 */
	public StationCriteria setMaximumLatitude(Double maximumLatitude) {
		this.maximumLatitude = maximumLatitude;
		return this;
	}

	/**
	 * Set minimum longitude in degrees
	 * 
	 * @param miniumumLonitude
	 * @return
	 */
	public StationCriteria setMinimumLongitude(Double minimumLongitude) {
		this.minimumLongitude = minimumLongitude;
		return this;
	}

	/**
	 * Set maximum longitude in degrees
	 * 
	 * @param maximumLongitude
	 * @return
	 */
	public StationCriteria setMaximumLongitude(Double maximumLongitude) {
		this.maximumLongitude = maximumLongitude;
		return this;
	}

	/**
	 * Set when were stations last updated
	 * 
	 * @param updatedAfter
	 * @return
	 */
	public StationCriteria setUpdatedAfter(Date updatedAfter) {
		this.updatedAfter = updatedAfter;
		return this;
	}

	/**
	 * Set latitude in degrees, used in combination with radius
	 * 
	 * @param latitude
	 * @return
	 */
	public StationCriteria setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	/**
	 * Set longitude in degrees, used in combination with radius
	 * 
	 * @param longitude
	 * @return
	 */
	public StationCriteria setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	/**
	 * Set the minimum radius in degrees
	 * 
	 * @param minumumRadius
	 * @return
	 */
	public StationCriteria setMinimumRadius(Double minimumRadius) {
		this.minimumRadius = minimumRadius;
		return this;
	}

	/**
	 * Set the maximum radius in degrees
	 * 
	 * @param maximumRadius
	 * @return
	 */
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
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public boolean isIncludeAvailability() {
		return includeAvailability;
	}

	public void setIncludeAvailability(boolean includeAvailability) {
		this.includeAvailability = includeAvailability;
	}

	public boolean isIncludeRestricted() {
		return includeRestricted;
	}

	public void setIncludeRestricted(boolean includeRestricted) {
		this.includeRestricted = includeRestricted;
	}

	public boolean isMatchTimeSeries() {
		return matchTimeSeries;
	}

	public void setMatchTimeSeries(boolean matchTimeSeries) {
		this.matchTimeSeries = matchTimeSeries;
	}

	public OutputLevel getLevel() {
		return level;
	}

	public void setLevel(OutputLevel level) {
		this.level = level;
	}

	public OutputFormat getFormat() {
		return format;
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
			string.append("net=" + this.netCode);
			and = true;
		}
		if (this.staCode != null) {
			if (and) {
				string.append("&");
			}
			string.append("sta=" + this.staCode);
			and = true;
		}

		if (this.chanCode != null) {
			if (and) {
				string.append("&");
			}
			string.append("cha=" + this.chanCode);
			and = true;
		}

		if (this.locCode != null) {
			if (and) {
				string.append("&");
			}
			string.append("loc=" + this.locCode);
			and = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		if (this.updatedAfter != null) {
			if (and) {
				string.append("&");
			}
			string.append("updatedafter=")
					.append(sdf.format(this.updatedAfter));
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
			string.append("endbefore=" + DateUtil.format(this.endBefore));
			and = true;
		}

		if (this.endAfter != null) {
			if (and) {
				string.append("&");
			}
			string.append("endafter=" + DateUtil.format(this.endAfter));
			and = true;
		}

		if (this.startBefore != null) {
			if (and) {
				string.append("&");
			}
			string.append("startbefore=" + DateUtil.format(this.startBefore));
			and = true;
		}

		if (this.startAfter != null) {
			if (and) {
				string.append("&");
			}
			string.append("startafter=" + DateUtil.format(this.startAfter));
			and = true;
		}

		if (maximumRadius != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxradius=" + this.maximumRadius);
			and = true;
		}

		if (minimumRadius != null) {
			if (and) {
				string.append("&");
			}
			string.append("minradius=" + this.minimumRadius);
			and = true;
		}

		if (latitude != null) {
			if (and) {
				string.append("&");
			}
			string.append("latitude=" + this.latitude);
			and = true;
		}
		if (longitude != null) {
			if (and) {
				string.append("&");
			}
			string.append("longitude=" + this.longitude);
			and = true;
		}

		if (minimumLatitude != null) {
			if (and) {
				string.append("&");
			}
			string.append("minlatitude=" + this.minimumLatitude);
			and = true;
		}

		if (maximumLatitude != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxlatitude=" + this.maximumLatitude);
			and = true;
		}

		if (minimumLongitude != null) {
			if (and) {
				string.append("&");
			}
			string.append("minlongitude=" + this.minimumLongitude);
			and = true;
		}

		if (maximumLongitude != null) {
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
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (and) {
					string.append("&");
				}
				string.append(entry.getKey() + "=" + entry.getValue());
				and = true;
			}
		}
		if (and) {
			string.append("&");
		}
		string.append("format=" + this.format.toString());
		List<String> l = new ArrayList<String>();
		l.add(string.toString());
		return l;
	}

	/**
	 * Add station restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param staCode
	 * @return
	 */
	public StationCriteria addStation(String staCode) {
		if (this.staCode == null) {
			this.staCode = staCode;
		} else {
			this.staCode = new StringBuilder(this.staCode)
					.append("," + staCode).toString();
		}

		return this;
	}

	/**
	 * Add network restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param netCode
	 * @return
	 */
	public StationCriteria addNetwork(String netCode) {
		if (this.netCode == null) {
			this.netCode = netCode;
		} else {
			this.netCode = new StringBuilder(this.netCode)
					.append("," + netCode).toString();
		}
		return this;
	}

	/**
	 * Add channel restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param channel
	 * @return
	 */
	public StationCriteria addChannel(String channel) {
		if (this.chanCode == null) {
			this.chanCode = channel;
		} else {
			this.chanCode = new StringBuilder(this.chanCode).append(
					"," + channel).toString();
		}
		return this;
	}

	/**
	 * Add location restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param location
	 * @return criteria
	 */
	public StationCriteria addLocation(String location) {
		if (location != null) {
			location = location.replace(" ", "-");
		}
		if (this.locCode == null) {
			this.locCode = location;
		} else {
			this.locCode = new StringBuilder(this.locCode).append(
					"," + location).toString();
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

	/**
	 * Introduce new key/value parameters
	 * 
	 * @param key
	 * @param value
	 * @return the criteria
	 */
	public void add(String key, String value) {
		params.put(key, value);
	}

	/**
	 * Will reset the criteria object to its defaults clearing all other
	 * properties.
	 */

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
		return startBefore;
	}

	public Date getStartAfter() {
		return startAfter;
	}

	public Date getEndBefore() {
		return endBefore;
	}

	public Date getEndAfter() {
		return endAfter;
	}

	public Date getUpdatedAfter() {
		return updatedAfter;
	}

	public String[] getNetworks() {
		if (this.netCode == null) {
			return null;
		}
		return netCode.split(",");
	}

	public String[] getStations() {
		if (this.staCode == null) {
			return null;
		}
		return staCode.split(",");
	}

	public String[] getChannels() {
		if (this.chanCode == null) {
			return null;
		}
		return chanCode.split(",");
	}

	public String[] getLocations() {
		if (this.locCode == null) {
			return null;
		}
		return locCode.replace("-", " ").split(",");
	}

	@Override
	public String toString() {
		return "StationCriteria [startBefore=" + startBefore + ", startAfter="
				+ startAfter + ", endBefore=" + endBefore + ", endAfter="
				+ endAfter + ", startTime=" + startTime + ", endTime="
				+ endTime + ", updatedAfter=" + updatedAfter + ", netCode="
				+ netCode + ", staCode=" + staCode + ", chanCode=" + chanCode
				+ ", locCode=" + locCode + ", minimumLatitude="
				+ minimumLatitude + ", maximumLatitude=" + maximumLatitude
				+ ", minimumLongitude=" + minimumLongitude
				+ ", maximumLongitude=" + maximumLongitude + ", latitude="
				+ latitude + ", longitude=" + longitude + ", minimumRadius="
				+ minimumRadius + ", maximumRadius=" + maximumRadius
				+ ", includeAvailability=" + includeAvailability
				+ ", includeRestricted=" + includeRestricted
				+ ", matchTimeSeries=" + matchTimeSeries + "]";
	}

	/*
	 * @Override public String toString() { return
	 * "StationCriteria [startBefore=" + startBefore + ", startAfter=" +
	 * startAfter + ", endBefore=" + endBefore + ", endAfter=" + endAfter +
	 * ", startTime=" + startTime + ", endTime=" + endTime + ", updatedAfter=" +
	 * updatedAfter + ", netCode=" + netCode + ", staCode=" + staCode +
	 * ", chanCode=" + chanCode + ", locCode=" + locCode + ", minLat=" +
	 * minimumLatitude + ", maxLat=" + maximumLatitude + ", minLon=" +
	 * minimumLongitude + ", maxLon=" + maximumLongitude + ", lat=" + latitude +
	 * ", lon=" + longitude + ", minRadius=" + minimumRadius + ", maxRadius=" +
	 * maximumRadius + "]"; }
	 */

}
