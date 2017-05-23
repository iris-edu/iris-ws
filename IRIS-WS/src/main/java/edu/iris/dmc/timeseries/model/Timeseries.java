package edu.iris.dmc.timeseries.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.Type;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class Timeseries implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7762713350314515989L;

	Logger logger = Logger.getLogger("edu.iris.dmc.ws.seed.Timeseries");

	private String networkCode; /* Network designation */
	private String stationCode; /* Station designation */
	private String location; /* Location designation */
	private String channelCode; /* Channel designation */
	private int totalNumberOfSamples;
	private int actualNumberOfSamples;

	private Channel channel;

	private char quality;/* Data quality indicator */

	private Type type;
	private float min = Integer.MAX_VALUE;
	private float max = Integer.MIN_VALUE;
	private List<Segment> segments = new ArrayList<Segment>();

	private Timeseries(String networkCode, String stationCode, String location, String channelCode) {
		this.networkCode = networkCode.trim();
		this.stationCode = stationCode.trim();
		this.location = location;
		this.channelCode = channelCode;
	}

	public static Timeseries from(String networkCode, String stationCode, String location, String channelCode) {
		return new Timeseries(networkCode, stationCode, location, channelCode);
	}

	public void add(DataRecord dataRecord, boolean reduce)
			throws UnsupportedCompressionType, CodecException, SeedFormatException {
		DataHeader dataHeader = dataRecord.getHeader();
		char quality = dataHeader.getTypeCode();
		if (this.quality == '\u0000') {
			this.quality = quality;
		} else {
			if (this.quality != quality) {
				this.quality = 'M';
			}
		}
		Blockette1000 b1000 = (Blockette1000) dataRecord.getUniqueBlockette(1000);
		Type type = Type.from(b1000.getEncodingFormat());

		if (this.type == null) {
			this.type = type;
		} else {
			if (!this.type.equals(type)) {
				throw new UnsupportedCompressionType("Expected " + this.type.name() + " but found " + type.name());
			}
		}

		DecompressedDataRecord decompressedDataRecord = DecompressedDataRecord.from(dataRecord, reduce);

		//logger.info("Adding data record: " + reduce);
		this.totalNumberOfSamples += decompressedDataRecord.getNumberOfSamples();
		this.actualNumberOfSamples += decompressedDataRecord.getRecord().getData().length;
		if (min > decompressedDataRecord.getMinumumValue()) {
			min = decompressedDataRecord.getMinumumValue();
		}
		if (max < decompressedDataRecord.getMaximumValue()) {
			max = decompressedDataRecord.getMaximumValue();
		}

		if (this.segments.isEmpty()) {
			createAndAdd(decompressedDataRecord);
		} else {
			for (Segment segment : this.segments) {
				if (!Util.isSampleRateTolerable(segment.getSamplerate(), decompressedDataRecord.getSampleRate())) {
					//this.createAndAdd(decompressedDataRecord);
					break;
				} else {
					int index = segment.add(decompressedDataRecord);
					if (index > 0) {
						return;
					}
				}
			}
			this.createAndAdd(decompressedDataRecord);
		}
	}

	private void createAndAdd(DecompressedDataRecord decompressedDataRecord) {
		Segment segment = Segment.from(type, decompressedDataRecord.getSampleRate());
		segment.add(decompressedDataRecord);
		segment.setTimeseries(this);
		this.segments.add(segment);
	}

	public long getStartTime() {
		Segment s = this.segments.get(0);
		return s.getStartTime();
	}

	public long getEndTime() {
		Segment s = this.segments.get(this.segments.size() - 1);
		return s.getEndTime();
	}

	public float getSampleRate() {
		Segment s = this.segments.get(0);
		return s.getSamplerate();
	}

	public long getTotalNumberOfSamples() {
		return this.totalNumberOfSamples;
	}

	public long getActualNumberOfSamples() {
		return this.actualNumberOfSamples;
	}

	public Type getType() {
		return type;
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getLocation() {
		return location;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public char getDataQuality() {
		return this.quality;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelCode == null) ? 0 : channelCode.hashCode());
		result = prime * result;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((networkCode == null) ? 0 : networkCode.hashCode());
		result = prime * result + ((stationCode == null) ? 0 : stationCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timeseries other = (Timeseries) obj;
		if (channelCode == null) {
			if (other.channelCode != null)
				return false;
		} else if (!channelCode.equals(other.channelCode))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (networkCode == null) {
			if (other.networkCode != null)
				return false;
		} else if (!networkCode.equals(other.networkCode))
			return false;
		if (stationCode == null) {
			if (other.stationCode != null)
				return false;
		} else if (!stationCode.equals(other.stationCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Timeseries [networkCode=" + networkCode + ", stationCode=" + stationCode + ", location=" + location
				+ ", channelCode=" + channelCode + ", dataQuality=" + quality + ", numberOfSegments="
				+ this.segments.size() + "]";
	}

}
