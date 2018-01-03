package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * StationCriteria is a simple criteria builder to retrieve networks
 * 
 */
public class SacpzCriteria implements Criteria {

	private Date time;

	// protected Date[] timeWindow;

	private Date startTime;
	private Date endTime;

	private String netCode;
	private String staCode;
	private String chanCode;
	private String locCode;

	public SacpzCriteria setStartTime(Date start) {
		this.startTime = start;
		return this;
	}

	public SacpzCriteria setTime(Date time) {
		this.time = time;
		return this;
	}

	public SacpzCriteria setEndTime(Date end) {
		this.endTime = end;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getTime() {
		return time;
	}

	public Date getEndTime() {
		return endTime;
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

		if (this.startTime != null) {

			if (and) {
				string.append("&");
			}
			string.append("starttime=");
			string.append(sdf.format(this.startTime));
			and = true;
		}

		if (this.endTime != null) {

			if (and) {
				string.append("&");
			}
			string.append("endtime=");
			string.append(sdf.format(this.endTime));

			and = true;
		}

		if (this.time != null) {
			if (and) {
				string.append("&");
			}
			string.append("time=" + sdf.format(this.time));
			and = true;
		}

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
	public SacpzCriteria addStation(String staCode) {
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
	public SacpzCriteria addNetwork(String netCode) {
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
	public SacpzCriteria addChannel(String channel) {
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
	 * @return
	 */
	public SacpzCriteria addLocation(String location) {
		if(location==null){
			return this;
		}
		if("  ".equals(location)){
			location="--";
		}
		if (this.locCode == null) {
			this.locCode = location;
		} else {
			this.locCode = new StringBuilder(this.locCode).append(
					"," + location).toString();
		}
		return this;
	}

	/**
	 * Will reset the criteria object to its defaults clearing all other
	 * properties.
	 */

	public void reset() {

		this.time = null;
		this.startTime = null;
		this.endTime = null;
		this.netCode = null;
		this.staCode = null;
		this.chanCode = null;
		this.locCode = null;

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
		return "SacpzCriteria [time=" + time + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", net=" + netCode
				+ ", sta=" + staCode + ", cha=" + chanCode
				+ ", loc=" + locCode + "]";
	}

}
