package edu.iris.dmc.timeseries.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.timeseries.model.Segment.Type;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette100;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Btime;
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

	private Channel channel;

	private Character quality;/* Data quality indicator */

	private Collection<Segment> segments = new ArrayList<Segment>();

	public Timeseries() {
	}

	public Timeseries(String networkCode, String stationCode, String location,
			String channelCode) {
		this.networkCode = networkCode.trim();
		this.stationCode = stationCode.trim();
		this.location = location;
		this.channelCode = channelCode;
	}

	private int offset = 0;

	public void add(Timestamp startTimestamp, DataRecord record)
			throws UnsupportedCompressionType, CodecException,
			SeedFormatException {

		if (record == null || record.getData() == null) {
			return;
		}
		int format = -1;

		Blockette1000 b1000 = (Blockette1000) record.getUniqueBlockette(1000);
		format = b1000.getEncodingFormat();

		byte[] data = record.getData();

		Codec codec = new Codec();
		float sampleRate = record.getHeader().getSampleRate();
		int numberOfSamples = record.getHeader().getNumSamples();
		Blockette[] bs = record.getBlockettes(100);
		if (bs != null && bs.length > 0) {
			Blockette100 b100 = (Blockette100) bs[0];
			sampleRate = b100.getActualSampleRate();
		}
		DecompressedData dData = codec.decompress(format, data,
				numberOfSamples, false);

		Type thisType = Type.values()[dData.getType() - 1];

		boolean addNewSegment = true;

		double durationinInSeconds = ((numberOfSamples - 1) / (double) sampleRate);

		Timestamp endTime = startTimestamp;
		// Timestamp endTime = new Timestamp(startTimestamp.getTime());
		// endTime.setNanos(startTimestamp.getNanos());

		SimpleDateFormat dfm = new SimpleDateFormat("yyyy,D,HH:mm:ss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		endTime = Util.addSeconds(endTime, durationinInSeconds);
		endTime = Util.roundTime(endTime);

		double halfPeriodinMillis = ((1 / sampleRate) / 2) * 1000;

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Tolerable Rate: " + halfPeriodinMillis
					+ " Start Time: " + startTimestamp + " End Time: "
					+ endTime + " Sample Rate: " + sampleRate);
		}

		for (Segment segment : this.segments) {
			if (segment.getType() != thisType) {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Not same type segment, so skipping: "
							+ segment.getType() + "  " + dData.getType());
				}
				continue;
			}

			if (!Util.isRateTolerable(segment.getSamplerate(), sampleRate)) {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Rate is not tolerable: "
							+ segment.getSamplerate() + " " + sampleRate);
				}
				continue;
			}

			dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
			// if (endTime.getTime()<segment.getStartTime().getTime()) {
			if (endTime.before(segment.getStartTime())) {
				if (Math.abs(segment.getStartTime().getTime()
						- endTime.getTime()) <= halfPeriodinMillis) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("Appending to left: "
								+ segment.getStartTime() + " " + endTime + "  "
								+ halfPeriodinMillis);
					}
					segment.add(startTimestamp, endTime, dData, sampleRate,
							numberOfSamples, 0);
					addNewSegment = false;
					break;
				} /*
				 * else { if (logger.isLoggable(Level.FINER)) {
				 * logger.finer("Left Time is not tolerable: " +
				 * segment.getStartTime() + " " + endTime + "  " +
				 * halfPeriodinMillis); } }
				 */
			}

			// if (startTimestamp.getTime()>segment.getEndTime().getTime()) {
			if (startTimestamp.after(segment.getEndTime())) {
				if (Math.abs(segment.getExpectedNextSampleTime().getTime()
						- startTimestamp.getTime()) <= halfPeriodinMillis) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("Appending to right: "
								+ segment.getExpectedNextSampleTime() + " "
								+ startTimestamp + "  " + halfPeriodinMillis);
					}
					segment.addAfter(startTimestamp, endTime, dData,
							sampleRate, numberOfSamples);
					addNewSegment = false;
					break;
				} /*
				 * else { if (logger.isLoggable(Level.FINER)) {
				 * logger.finer("Right Time is not tolerable: " +
				 * segment.getExpectedNextSampleTime() + " " + startTimestamp +
				 * "  " + (segment.getExpectedNextSampleTime() .getTime() -
				 * startTimestamp.getTime()) + "   " + halfPeriodinMillis); } }
				 */
			}

		}

		if (addNewSegment) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Adding new segment: " + startTimestamp + "  "
						+ sampleRate + "  " + numberOfSamples);
			}
			Segment segment = new Segment(thisType, sampleRate);
			segment.setTimeseries(this);
			segment.add(startTimestamp, endTime, dData, sampleRate,
					numberOfSamples, 0);
			this.segments.add(segment);
		}

		offset += (data.length - 1);

		char character = record.getHeader().getTypeCode();

		if (this.quality == null) {
			this.quality = character;
		} else {
			if (!this.quality.equals(character)) {
				this.quality = 'M';
			}
		}

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

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public char getDataQuality() {
		return this.quality;
	}

	public Collection<Segment> getSegments() {
		return segments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((channelCode == null) ? 0 : channelCode.hashCode());
		result = prime * result;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((networkCode == null) ? 0 : networkCode.hashCode());
		result = prime * result
				+ ((stationCode == null) ? 0 : stationCode.hashCode());
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
		return "Timeseries [networkCode=" + networkCode + ", stationCode="
				+ stationCode + ", location=" + location + ", channelCode="
				+ channelCode + ", dataQuality=" + quality
				+ ", numberOfSegments=" + this.segments.size() + "]";
	}

}
