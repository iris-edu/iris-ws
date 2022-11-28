package edu.iris.dmc.criteria;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EventCriteria is a simple criteria builder to retrieve events
 * 
 */
public class EventCriteria implements Criteria {

	private String eventId;

	private Double minLat;
	private Double maxLat;
	private Double minLon;
	private Double maxLon;
	private Double minMag;
	private Double maxMag;

	private Double lat;
	private Double lon;
	private Double minRadius;
	private Double maxRadius;

	private Double minDepth;
	private Double maxDepth;

	private String magnitudeType;
	private String catalog;
	private String contributor;
	private boolean includeAllMagnitudes = false;
	private boolean includeAllOrigins = false;
	// private boolean includeallMagnitudesIsSet = false;
	private boolean includeArrivals = false;

	private Date startTime;
	private Date endTime;
	private Date updatedAfter;

	private Integer limit;
	private Integer offset;
	private EventOrder orderBy;

	private Map<String, String> params = new HashMap<String, String>();

	@Override
	public Map<String, List<String>> toMapUrlParameters() {
		Map<String, List<String>> map = new TreeMap<>();
		return map;
	}

	public EventCriteria setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	public EventCriteria setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	/**
	 * Set when origins were last updated
	 * 
	 * @param updatedAfter
	 * @return
	 */
	public EventCriteria setUpdatedAfter(Date updatedAfter) {
		this.updatedAfter = updatedAfter;
		return this;
	}

	/**
	 * Limit the number of events retrieved depending on the orderBy attribute
	 * 
	 * @param numberOfEvents
	 * @return
	 */
	public EventCriteria setFetchLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * mark the first event offset
	 * 
	 * @param offset
	 * @return
	 */
	public EventCriteria setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}

	public EventCriteria orderBy(EventOrder orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	/**
	 * Specify magnitude type. Some common types (there are many) include:Ml -
	 * local (Richter) magnitude, Ms - surface magnitude, mb - body wave
	 * magnitude, Mw - moment magnitude
	 * 
	 * @param magnitudeType
	 * @return
	 */
	public EventCriteria setMagnitudeType(String magnitudeType) {
		this.magnitudeType = magnitudeType;
		return this;
	}

	/**
	 * Results will include any origins which contain the specified catalog
	 * text, i.e. "PDE" will match "NEIC PDE"
	 * 
	 * @param catalog
	 * @return the criteria
	 */
	public EventCriteria setCatalog(String catalog) {
		this.catalog = catalog;
		return this;
	}

	/**
	 * Results will include any origins which contain the specified contributor
	 * text, i.e. "NEIC" will match "NEIC PDE-Q".
	 * 
	 * @param contributor
	 * @return the criteria
	 */
	public EventCriteria setContributor(String contributor) {
		this.contributor = contributor;
		return this;
	}

	/**
	 * This will include all the magnitudes in search and print criteria. If
	 * magnitudes do not exist for a certain origin, the search algorithm will
	 * consider it a miss and therefore will not include the event. Default=true
	 * 
	 * @param includeallMagnitudes
	 * @return the criteria
	 */
	public EventCriteria includeAllMagnitudes(boolean includeAllMagnitudes) {
		this.includeAllMagnitudes = includeAllMagnitudes;
		return this;
	}

	/**
	 * This will include all the magnitudes in search and print criteria. If
	 * magnitudes do not exist for a certain origin, the search algorithm will
	 * consider it a miss and therefore will not include the event. Default=true
	 * 
	 * @param includeallMagnitudes
	 * @return the criteria
	 */
	public EventCriteria includeAllOrigins(boolean includeAllOrigins) {
		this.includeAllOrigins = includeAllOrigins;
		return this;
	}

	/**
	 * This will include all the arrival information including picks.
	 * Default=false
	 * 
	 * @param includeallMagnitudes
	 * @return the criteria
	 */
	public EventCriteria includeArrivals(boolean includeArrivals) {
		this.includeArrivals = includeArrivals;
		return this;
	}

	/**
	 * This MUST be used in conjunction with the setLongitude and
	 * setMaximumRadius parameters.
	 * 
	 * @param latitude
	 * @return the criteria
	 */
	public EventCriteria setLatitude(Double latitude) {
		this.lat = latitude;
		return this;
	}

	/**
	 * This MUST be used in conjunction with the setLatitude and
	 * setMaximumRadius parameters.
	 * 
	 * @param longitude
	 * @return the criteria
	 */
	public EventCriteria setLongitude(Double longitude) {
		this.lon = longitude;
		return this;
	}

	/**
	 * This optional parameter allows for the exclusion of events that are
	 * closer than minumumRadius degrees from the specified lat/lon point. This
	 * MUST be used in conjunction with the lat, lon, and maximumRadius
	 * parameters and is subject to the same restrictions. If this parameter
	 * isn't specified, then it defaults to 0.0 degrees.
	 * 
	 * @param minimumRadius
	 * @return the criteria
	 */
	public EventCriteria setMinimumRadius(Double minumumRadius) {
		this.minRadius = minumumRadius;
		return this;
	}

	/**
	 * Specify the maximum radius, in degrees. Only earthquakes within
	 * maximumRadius degrees of the lat/lon point will be retrieved. This MUST
	 * be used in conjunction with the lat and lon parameters.
	 * 
	 * @param maximumRadius
	 * @return the criteria
	 */
	public EventCriteria setMaximumRadius(Double maximumRadius) {
		this.maxRadius = maximumRadius;
		return this;
	}

	/**
	 * Set minimum Depth in kilometers
	 * 
	 * @param minimumDepth
	 * @return the criteria
	 */
	public EventCriteria setMinimumDepth(Double minumumDepth) {
		this.minDepth = minumumDepth;
		return this;
	}

	/**
	 * Set maximum depth in kilometers
	 * 
	 * @param maximumDepth
	 * @return the criteria
	 */
	public EventCriteria setMaximumDepth(Double maximumDepth) {
		this.maxDepth = maximumDepth;
		return this;
	}

	public EventCriteria setEventId(String id) {
		this.eventId = id;
		return this;
	}

	public EventCriteria setMinimumMagnitude(Double minimumMagnitude) {
		this.minMag = minimumMagnitude;
		return this;
	}

	public EventCriteria setMaximumMagnitude(Double maximumMagnitude) {
		this.maxMag = maximumMagnitude;
		return this;
	}

	/**
	 * In degrees
	 * 
	 * @param minimumLatitude
	 * @return the criteria
	 */
	public EventCriteria setMinimumLatitude(Double minimumLatitude) {
		this.minLat = minimumLatitude;
		return this;
	}

	/**
	 * In degrees
	 * 
	 * @param maximumLatitude
	 * @return the criteria
	 */
	public EventCriteria setMaximumLatitude(Double maximumLatitude) {
		this.maxLat = maximumLatitude;
		return this;
	}

	/**
	 * In degrees
	 * 
	 * @param minimumLongitude
	 * @return the criteria
	 */
	public EventCriteria setMinimumLongitude(Double minimumLongitude) {
		this.minLon = minimumLongitude;
		return this;
	}

	/**
	 * In degrees
	 * 
	 * @param maximumLongitude
	 * @return the criteria
	 */
	public EventCriteria setMaximumLongitude(Double maximumLongitude) {
		this.maxLon = maximumLongitude;
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

	public List<String> toUrlParams() throws CriteriaException {

		StringBuilder string = new StringBuilder();
		boolean and = false;
		if (this.eventId != null) {
			string.append("eventid=" + this.eventId);
			and = true;
		}

		if (and) {
			string.append("&");
		}

		if (minLat != null) {
			if (and) {
				string.append("&");
			}
			string.append("minlat=" + this.minLat);
			and = true;
		}

		if (maxLat != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxlat=" + this.maxLat);
			and = true;
		}

		if (minLon != null) {
			if (and) {
				string.append("&");
			}
			string.append("minlon=" + this.minLon);
			and = true;
		}

		if (maxLon != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxlon=" + this.maxLon);
			and = true;
		}

		if (minMag != null) {
			if (and) {
				string.append("&");
			}
			string.append("minmag=" + this.minMag);
			and = true;
		}

		if (maxMag != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxmag=" + this.maxMag);
			and = true;
		}

		if (minDepth != null) {
			if (and) {
				string.append("&");
			}
			string.append("mindepth=" + this.minDepth);
			and = true;
		}

		if (maxDepth != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxdepth=" + this.maxDepth);
			and = true;
		}

		if (maxRadius != null) {
			if (and) {
				string.append("&");
			}
			string.append("maxradius=" + this.maxRadius);
			and = true;
		}

		if (minRadius != null) {
			if (and) {
				string.append("&");
			}
			string.append("minradius=" + this.minRadius);
			and = true;
		}

		if (lat != null) {
			if (and) {
				string.append("&");
			}
			string.append("lat=" + this.lat);
			and = true;
		}
		if (lon != null) {
			if (and) {
				string.append("&");
			}
			string.append("lon=" + this.lon);
			and = true;
		}

		if (magnitudeType != null) {
			if (and) {
				string.append("&");
			}
			string.append("magtype=" + this.magnitudeType);
			and = true;
		}

		if (and) {
			string.append("&");
		}

		if (this.catalog != null) {
			if (and) {
				string.append("&");
			}
			string.append("catalog=" + catalog);
			and = true;
		}

		if (this.contributor != null) {
			if (and) {
				string.append("&");
			}
			string.append("contributor=" + contributor);
			and = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (this.startTime != null) {
			if (and) {
				string.append("&");
			}

			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			string.append("starttime=").append(sdf.format(this.startTime));
			and = true;
		}

		if (this.endTime != null) {
			if (and) {
				string.append("&");
			}
			string.append("endtime=").append(sdf.format(this.endTime));
			and = true;
		}

		sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (this.updatedAfter != null) {
			if (and) {
				string.append("&");
			}

			string.append("updatedafter=")
					.append(sdf.format(this.updatedAfter));
			and = true;
		}

		if (this.limit != null) {
			if (and) {
				string.append("&");
			}
			string.append("limit=").append(limit);
			and = true;
		}

		if (this.offset != null) {
			if (and) {
				string.append("&");
			}
			string.append("offset=").append(offset);
			and = true;
		}

		if (this.orderBy != null) {
			if (and) {
				string.append("&");
			}
			string.append("orderby=").append(orderBy);
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

		String s = string.toString();
		if (!s.contains("includearrivals")) {
			if (and) {
				string.append("&");
			}
			if (this.includeArrivals) {
				string.append("includearrivals=true");
				and = true;
			} else {
				string.append("includearrivals=false");
				and = true;
			}
		}
		if (!s.contains("includeallmagnitudes")) {
			if (and) {
				string.append("&");
			}

			if (this.includeAllMagnitudes) {
				string.append("includeallmagnitudes=true");
				and = true;
			} else {
				string.append("includeallmagnitudes=false");
				and = true;
			}
		}

		if (!s.contains("includeallorigins")) {
			if (and) {
				string.append("&");
			}
			if (this.includeAllOrigins) {
				string.append("includeallorigins=true");
				and = true;
			} else {
				string.append("includeallorigins=false");
				and = true;
			}
		}

		List<String> l = new ArrayList<String>();
		l.add(string.toString());
		return l;
	}

	public void reset() {
		this.eventId = null;
		this.minLat = null;
		this.maxLat = null;
		this.minLon = null;
		this.maxLon = null;
		this.minMag = null;
		this.maxMag = null;
		this.lat = null;
		this.lon = null;
		this.minRadius = null;
		this.maxRadius = null;
		this.minDepth = null;
		this.maxDepth = null;
		this.magnitudeType = null;
		this.catalog = null;
		this.contributor = null;
		this.startTime = null;
		this.endTime = null;
		this.updatedAfter = null;
		this.limit = null;
		this.orderBy = null;
		this.includeAllMagnitudes = false;
		this.includeAllOrigins = false;
		this.includeArrivals = false;
		this.params.clear();
	}

}
