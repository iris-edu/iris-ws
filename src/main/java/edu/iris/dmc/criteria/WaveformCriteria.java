
package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WaveformCriteria implements Criteria {
	private List<String> selections = new ArrayList<>();
	private Quality quality = null;
	private Boolean longestOnly;
	private Integer minimumLength = null;
	private boolean distinctRequests = false;
	private Map<String, String> params = new HashMap<>();

	public WaveformCriteria() {
	}

	public List<String> toUrlParams() throws CriteriaException, UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();
		if (this.longestOnly != null) {
			stringBuilder.append("longestonly=").append(this.longestOnly).append("\n");
		}

		if (this.minimumLength != null) {
			stringBuilder.append("minimumlength=").append(this.minimumLength).append("\n");
		}

		if (this.quality != null) {
			stringBuilder.append("quality=").append(this.quality).append("\n");
		}

		String header = stringBuilder.toString();
		List<String> postList = new ArrayList<>();
		if (this.distinctRequests) {

			for (String selection : this.selections) {
				String buff = header +
						selection;
				postList.add(buff);
			}
		} else {
			StringBuilder buff = new StringBuilder();
			buff.append(header);
			boolean b = false;

			for (String selection : this.selections) {
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

	public WaveformCriteria add(String net, String sta, String loc, String cha, Date start, Date end) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String location = loc.trim();
		if ("".equals(location)) {
			location = "--";
		}

		this.selections.add(net + " " + sta + " " + location + " " + cha + " " + sdf.format(start) + " " + sdf.format(end));
		return this;
	}

	public WaveformCriteria setQuality(Quality quality) {
		this.quality = quality;
		return this;
	}

	public WaveformCriteria setMinimumSegmentLength(int seconds) {
		this.minimumLength = seconds;
		return this;
	}

	public WaveformCriteria setLongestSegmentOnly(boolean longestOnly) {
		this.longestOnly = longestOnly;
		return this;
	}

	public WaveformCriteria makeDistinctRequests(boolean distinctRequests) {
		this.distinctRequests = distinctRequests;
		return this;
	}

	public boolean isDistinctRequests() {
		return this.distinctRequests;
	}

	public String toString() {
		return "DataSelectCriteria [selection=" + this.selections + ", quality=" + this.quality + ", longestOnly=" + this.longestOnly + ", minimumLength=" + this.minimumLength + "]";
	}

	public void reset() {
		this.longestOnly = false;
		this.quality = null;
		this.selections.clear();
		this.minimumLength = null;
		this.distinctRequests = false;
	}

	public Quality getQuality() {
		return this.quality;
	}

	public boolean isLongestSegmentOnly() {
		return this.longestOnly;
	}

	public Integer getMinimumSegmentLength() {
		return this.minimumLength;
	}
}
