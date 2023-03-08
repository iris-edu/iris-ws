
package edu.iris.dmc.criteria;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
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

	public SacpzCriteria addNetwork(String netCode) {
		if(netCode==null){
			return this;
		}
		String[]array = netCode.split(",");
		if(array.length>0){
			if(this.netCodes==null){
				this.netCodes=new ArrayList<>();
			}
			this.netCodes.addAll(Arrays.asList(array));
		}
		return this;
	}

	public SacpzCriteria addStation(String staCode) {
		if(staCode==null){
			return this;
		}
		String[]array = staCode.split(",");
		if(array.length>0){
			if(this.staCodes==null){
				this.staCodes=new ArrayList<>();
			}
			this.staCodes.addAll(Arrays.asList(array));
		}
		return this;
	}

	public SacpzCriteria addChannel(String channel) {
		if(channel==null){
			return this;
		}
		String[]array = channel.split(",");
		if(array.length>0){
			if(this.chanCodes==null){
				this.chanCodes=new ArrayList<>();
			}
			this.chanCodes.addAll(Arrays.asList(array));
		}
		return this;
	}

	public SacpzCriteria addLocation(String location) {
		if(location==null){
			return this;
		}
		location = location.replace(" ", "-");
		String[]array = location.split(",");
		if(array.length>0){
			if(this.locCodes==null){
				this.locCodes=new ArrayList<>();
			}
			this.locCodes.addAll(Arrays.asList(array));
		}
		return this;
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
