
package edu.iris.dmc.criteria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SacpzCriteria implements Criteria {
	private Date time;
	private Date startTime;
	private Date endTime;
	private String netCode;
	private String staCode;
	private String chanCode;
	private String locCode;

	public SacpzCriteria() {
	}

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
		return this.startTime;
	}

	public Date getTime() {
		return this.time;
	}

	public Date getEndTime() {
		return this.endTime;
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

		String pattern = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
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

			string.append("time=").append(sdf.format(this.time));
			and = true;
		}

		List<String> l = new ArrayList();
		l.add(string.toString());
		return l;
	}

	public SacpzCriteria addStation(String staCode) {
		if (this.staCode == null) {
			this.staCode = staCode;
		} else {
			this.staCode = this.staCode + "," + staCode;
		}

		return this;
	}

	public SacpzCriteria addNetwork(String netCode) {
		if (this.netCode == null) {
			this.netCode = netCode;
		} else {
			this.netCode = this.netCode + "," + netCode;
		}

		return this;
	}

	public SacpzCriteria addChannel(String channel) {
		if (this.chanCode == null) {
			this.chanCode = channel;
		} else {
			this.chanCode = this.chanCode + "," + channel;
		}

		return this;
	}

	public SacpzCriteria addLocation(String location) {
		if (location == null) {
			return this;
		} else {
			if ("  ".equals(location)) {
				location = "--";
			}

			if (this.locCode == null) {
				this.locCode = location;
			} else {
				this.locCode = this.locCode + "," + location;
			}

			return this;
		}
	}

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
		return "SacpzCriteria [time=" + this.time + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", net=" + this.netCode + ", sta=" + this.staCode + ", cha=" + this.chanCode + ", loc=" + this.locCode + "]";
	}
}
