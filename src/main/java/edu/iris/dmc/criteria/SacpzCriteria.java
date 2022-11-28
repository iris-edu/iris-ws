
package edu.iris.dmc.criteria;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Builder
@Getter
@Setter
@ToString
public class SacpzCriteria implements Criteria {
	private Date time;
	private Date startTime;
	private Date endTime;
	@Singular
	private List<String> netCodes;
	@Singular
	private List<String> staCodes;
	@Singular
	private List<String> chanCodes;
	@Singular
	private List<String> locCodes;


	public Map<String, List<String>> toMapUrlParameters(){
		Map<String, List<String>> map = new TreeMap<>();
		if(this.netCodes!=null&&!this.netCodes.isEmpty()) {
			map.put("network", this.netCodes);
		}
		if(this.staCodes!=null&&!this.staCodes.isEmpty()) {
			map.put("station", this.staCodes);
		}
		if(this.chanCodes!=null&&!this.chanCodes.isEmpty()) {
			map.put("channel", this.chanCodes);
		}
		if(this.locCodes!=null&&!this.locCodes.isEmpty()) {
			map.put("location", this.locCodes);
		}
		return map;
	}

	public List<String> toUrlParams() throws CriteriaException {
		StringBuilder string = new StringBuilder();
		boolean and = false;
		if (this.netCodes != null&&!this.netCodes.isEmpty()) {
			string.append("net=").append(String.join(",", this.netCodes));
			and = true;
		}

		if (this.staCodes != null&&!this.staCodes.isEmpty()) {
			if (and) {
				string.append("&");
			}

			string.append("sta=").append(String.join(",", this.staCodes));
			and = true;
		}

		if (this.chanCodes != null&&!this.chanCodes.isEmpty()) {
			if (and) {
				string.append("&");
			}

			string.append("cha=").append(String.join(",", this.chanCodes));
			and = true;
		}

		if (this.locCodes != null&&!this.locCodes.isEmpty()) {
			if (and) {
				string.append("&");
			}

			string.append("loc=").append(String.join(",", this.locCodes));
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

		List<String> l = new ArrayList<>();
		l.add(string.toString());
		return l;
	}


	public void reset() {
		this.time = null;
		this.startTime = null;
		this.endTime = null;
		this.netCodes = null;
		this.staCodes = null;
		this.chanCodes = null;
		this.locCodes = null;
	}
}
