package edu.iris.dmc.criteria;

import edu.iris.dmc.ws.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RespCriteria is a simple criteria builder to retrieve Resp data
 * 
 */
public class RespCriteria implements Criteria {

	private Date startTime;
	private Date endTime;
	private String netCode;
	private String staCode;
	private String chanCode;
	private String locCode;

	@Override
	public Map<String, List<String>> toMapUrlParameters() {
		Map<String, List<String>> map = new TreeMap<>();
		return map;
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
			string.append("loc=" + this.locCode);
			and = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

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
	public RespCriteria setStation(String staCode) {
		if (this.staCode == null) {
			this.staCode = staCode;
		} else {
			this.staCode = this.staCode +
					"," + staCode;
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
	public RespCriteria setNetwork(String netCode) {
		if (this.netCode == null) {
			this.netCode = netCode;
		} else {
			this.netCode = this.netCode +
					"," + netCode;
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
	public RespCriteria setChannel(String channel) {
		if (this.chanCode == null) {
			this.chanCode = channel;
		} else {
			this.chanCode = this.chanCode +
					"," + channel;
		}
		return this;
	}

	/**
	 * Set location restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param location
	 * @return
	 */
	public RespCriteria setLocation(String location) {
		if (location != null) {
			location = location.replace(" ", "-");
		}
		if (this.locCode == null) {
			this.locCode = location;
		} else {
			this.locCode = this.locCode +
					"," + location;
		}
		return this;
	}

	/**
	 * Set location restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param location
	 * @return
	 */
	public RespCriteria setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	/**
	 * Set location restriction to constrain the result. Wild card (* ?)
	 * accepted. This method can be chained.
	 * 
	 * @param location
	 * @return
	 */
	public RespCriteria setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	/**
	 * Will reset the criteria object to its defaults clearing all other
	 * properties.
	 */

	public void reset() {
		this.startTime = null;
		this.endTime = null;
		this.netCode = null;
		this.staCode = null;
		this.chanCode = null;
		this.locCode = null;
	}

}
