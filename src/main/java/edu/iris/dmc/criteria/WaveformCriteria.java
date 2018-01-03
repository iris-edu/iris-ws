package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * WaveFormCriteria is a simple criteria builder to retrieve timeseries list
 * 
 */
public class WaveformCriteria implements Criteria {

	private List<String> selections = new ArrayList<String>();

	private Quality quality = null;
	private Boolean longestOnly;
	private Integer minimumLength = null;
	private boolean distinctRequests = false;
	private Map<String, String> params = new HashMap<String, String>();

	public List<String> toUrlParams() throws CriteriaException,
			UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();

		if (longestOnly != null) {
			stringBuilder.append("longestonly=").append(longestOnly)
					.append("\n");
		}

		if (minimumLength != null) {
			stringBuilder.append("minimumlength=").append(minimumLength)
					.append("\n");
		}

		if (quality != null) {
			stringBuilder.append("quality=").append(quality).append("\n");
		}

		// the header might look like:
		//
		// quality Q
		// minimumlength 86400
		// longestonly
		//
		//
		String header = stringBuilder.toString();

		// this is a list of serarate post requests.
		// If distinctrequests is false, the list will only contain one entry.
		List<String> postList = new ArrayList<String>();

		if (distinctRequests) {
			for (String selection : selections) {
				StringBuilder buff = new StringBuilder();
				buff.append(header);
				buff.append(selection);
				postList.add(buff.toString());
			}
		} else {
			StringBuilder buff = new StringBuilder();
			buff.append(header);
			boolean b = false;
			for (String selection : selections) {
				if (b) {
					buff.append("\n");
				}
				b = true;
				buff.append(selection);
			}
			postList.add(buff.toString());
		}

		return postList;
	}

	/**
	 * Add a search entry to constrain the result to be retrieved
	 * 
	 * @param net
	 * @param sta
	 * @param loc
	 * @param cha
	 * @param start
	 * @param end
	 * @return
	 */
	public WaveformCriteria add(String net, String sta, String loc, String cha,
			Date start, Date end) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String location = loc.trim();
		if ("".equals(location)) {
			location = "--";
		}

		this.selections.add(net + " " + sta + " " + location + " " + cha + " "
				+ sdf.format(start) + " " + sdf.format(end));
		return this;
	}

	/**
	 * Default is B (best). E indicates everything/all qualities. M is the same
	 * as B.
	 * 
	 * @param quality
	 * @return
	 */
	public WaveformCriteria setQuality(Quality quality) {
		this.quality = quality;
		return this;
	}

	/**
	 * Enforce minimum segment length in seconds. Only time-series segments of
	 * this length or longer will be returned. No minimum segment length
	 * specified by default
	 * 
	 * @param seconds
	 * @return
	 */
	public WaveformCriteria setMinimumSegmentLength(int seconds) {
		this.minimumLength = seconds;
		return this;
	}

	/**
	 * Limit to longest segment only. For each time-series selection, only the
	 * longest segment is returned. Default=true
	 * 
	 * @param longestOnly
	 * @return
	 */
	public WaveformCriteria setLongestSegmentOnly(boolean longestOnly) {
		this.longestOnly = longestOnly;
		return this;
	}

	/**
	 * Will trigger separate requests for every entry. Segments will not be
	 * merged. Default=false
	 * 
	 * @param distinctRequests
	 * @return
	 */
	public WaveformCriteria makeDistinctRequests(boolean distinctRequests) {
		this.distinctRequests = distinctRequests;
		return this;
	}

	public boolean isDistinctRequests() {
		return this.distinctRequests;
	}

	public String toString() {
		return "DataSelectCriteria [selection=" + selections + ", quality="
				+ quality + ", longestOnly=" + longestOnly + ", minimumLength="
				+ minimumLength + "]";
	}

	/**
	 * Will reset the criteria to its defaults
	 */
	public void reset() {
		this.longestOnly = false;
		this.quality = null;

		this.selections.clear();
		this.minimumLength = null;
		this.distinctRequests = false;

	}

	/**
	 * Returns the Quality type
	 * 
	 * @return
	 */
	public Quality getQuality() {
		return quality;
	}

	/**
	 * Returns whether longestSegmentOnly is true or false
	 * 
	 * @return
	 */
	public boolean isLongestSegmentOnly() {
		return longestOnly;
	}

	/**
	 * return the minimum segment length in seconds
	 * 
	 * @return
	 */
	public Integer getMinimumSegmentLength() {
		return this.minimumLength;
	}

}
