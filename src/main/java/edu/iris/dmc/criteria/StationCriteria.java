package edu.iris.dmc.criteria;

import edu.iris.dmc.ws.util.DateUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Builder
@Getter
@Setter
public class StationCriteria implements Criteria {
	private Date startBefore;
	private Date startAfter;
	private Date endBefore;
	private Date endAfter;
	private Date startTime;
	private Date endTime;
	private Date updatedAfter;
	@Singular
	private List<String> netCodes;
	@Singular
	private List<String> staCodes;
	@Singular
	private List<String> chanCodes;
	@Singular
	private List<String>locCodes;
	private Double minimumLatitude;
	private Double maximumLatitude;
	private Double minimumLongitude;
	private Double maximumLongitude;
	private Double latitude;
	private Double longitude;
	private Double minimumRadius;
	private Double maximumRadius;
	private OutputLevel level;
	@Builder.Default
	private OutputFormat format=OutputFormat.XML;
	private boolean includeAvailability;
	@Builder.Default
	private boolean includeRestricted=true;
	private boolean matchTimeSeries;
	@Singular
	private Map<String, String> params;

	@Override
	public Map<String, List<String>> toMapUrlParameters(){
		Map<String, List<String>> map = new TreeMap<>();
		map.put("format", Collections.singletonList(format.toString()));
		map.put("includerestricted", Collections.singletonList(includeRestricted?"yes":"no"));
		map.put("matchtimeSeries", Collections.singletonList(matchTimeSeries ?"yes":"no"));
		map.put("includeavailability", Collections.singletonList(includeAvailability ?"yes":"no"));
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
	public List<String> toUrlParams(OutputLevel level) throws CriteriaException {
		this.level = level;
		return this.toUrlParams();
	}

	public List<String> toUrlParams() throws CriteriaException {
		StringBuilder string = new StringBuilder();
		boolean and = false;
		if (this.netCodes != null && !this.netCodes.isEmpty()) {
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

			string.append("cha=").append(String.join(",",this.chanCodes));
			and = true;
		}

		if (this.locCodes != null&&!this.locCodes.isEmpty()) {
			if (and) {
				string.append("&");
			}

			string.append("loc=").append(String.join(",",this.locCodes));
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

			string.append("maxlatitude=").append(this.maximumLatitude);
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

			string.append("level=").append(this.level.toString());
			and = true;
		}

		if (this.params!=null&&!this.params.isEmpty()) {
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
		this.netCodes = null;
		this.staCodes = null;
		this.chanCodes = null;
		this.locCodes = null;
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
}
