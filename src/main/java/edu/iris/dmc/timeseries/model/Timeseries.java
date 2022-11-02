//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.timeseries.model;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.seedcodec.Codec;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.timeseries.model.Segment.Type;
import edu.sc.seis.seisFile.mseed.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Timeseries implements Serializable {
	private static final long serialVersionUID = -7762713350314515989L;
	Logger logger = Logger.getLogger("edu.iris.dmc.ws.seed.Timeseries");
	private String networkCode;
	private String stationCode;
	private String location;
	private String channelCode;
	private Channel channel;
	private Character quality;
	private Collection<Segment> segments = new ArrayList<>();
	private int offset = 0;

	public Timeseries() {
	}

	public Timeseries(String networkCode, String stationCode, String location, String channelCode) {
		this.networkCode = networkCode.trim();
		this.stationCode = stationCode.trim();
		this.location = location;
		this.channelCode = channelCode;
	}

	public void add(Timestamp startTimestamp, DataRecord record) throws UnsupportedCompressionType, CodecException, SeedFormatException {
		if (record != null && record.getData() != null) {
			int numberOfSamples = record.getHeader().getNumSamples();
			if (numberOfSamples >= 1) {
				Blockette1000 b1000 = (Blockette1000)record.getUniqueBlockette(1000);
				int format = b1000.getEncodingFormat();
				byte[] data = record.getData();
				Codec codec = new Codec();
				float sampleRate = record.getHeader().getSampleRate();
				Blockette[] bs = record.getBlockettes(100);
				if (bs != null && bs.length > 0) {
					Blockette100 b100 = (Blockette100)bs[0];
					sampleRate = b100.getActualSampleRate();
				}

				DecompressedData dData = codec.decompress(format, data, numberOfSamples, false);
				Type thisType = Type.values()[dData.getType() - 1];
				boolean addNewSegment = true;
				double durationinInSeconds = (double)(numberOfSamples - 1) / (double)sampleRate;
				Timestamp endTime = Util.addSeconds(startTimestamp, durationinInSeconds);
				endTime = Util.roundTime(endTime);
				double halfPeriodinMillis = (double)(1.0F / sampleRate / 2.0F * 1000.0F);
				if (this.logger.isLoggable(Level.FINER)) {
					this.logger.finer("Tolerable Rate: " + halfPeriodinMillis + " Start Time: " + startTimestamp + " End Time: " + endTime + " Sample Rate: " + sampleRate);
				}

				Iterator var18 = this.segments.iterator();

				while(var18.hasNext()) {
					Segment segment = (Segment)var18.next();
					if (segment.getType() != thisType) {
						if (this.logger.isLoggable(Level.FINER)) {
							this.logger.finer("Not same type segment, so skipping: " + segment.getType() + "  " + dData.getType());
						}
					} else if (!Util.isRateTolerable(segment.getSamplerate(), sampleRate)) {
						if (this.logger.isLoggable(Level.FINER)) {
							this.logger.finer("Rate is not tolerable: " + segment.getSamplerate() + " " + sampleRate);
						}
					} else {
						if (endTime.before(segment.getStartTime()) && (double)Math.abs(segment.getStartTime().getTime() - endTime.getTime()) <= halfPeriodinMillis) {
							if (this.logger.isLoggable(Level.FINER)) {
								this.logger.finer("Appending to left: " + segment.getStartTime() + " " + endTime + "  " + halfPeriodinMillis);
							}

							segment.add(startTimestamp, endTime, dData, sampleRate, numberOfSamples, 0);
							addNewSegment = false;
							break;
						}

						if (startTimestamp.after(segment.getEndTime()) && (double)Math.abs(segment.getExpectedNextSampleTime().getTime() - startTimestamp.getTime()) <= halfPeriodinMillis) {
							if (this.logger.isLoggable(Level.FINER)) {
								this.logger.finer("Appending to right: " + segment.getExpectedNextSampleTime() + " " + startTimestamp + "  " + halfPeriodinMillis);
							}

							segment.addAfter(startTimestamp, endTime, dData, sampleRate, numberOfSamples);
							addNewSegment = false;
							break;
						}
					}
				}

				if (addNewSegment) {
					if (this.logger.isLoggable(Level.FINER)) {
						this.logger.finer("Adding new segment: " + startTimestamp + "  " + sampleRate + "  " + numberOfSamples);
					}

					Segment segment = new Segment(thisType, sampleRate);
					segment.setTimeseries(this);
					segment.add(startTimestamp, endTime, dData, sampleRate, numberOfSamples, 0);
					this.segments.add(segment);
				}

				this.offset += data.length - 1;
				char character = record.getHeader().getTypeCode();
				if (this.quality == null) {
					this.quality = character;
				} else if (!this.quality.equals(character)) {
					this.quality = 'M';
				}

			}
		}
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getNetworkCode() {
		return this.networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getStationCode() {
		return this.stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getChannelCode() {
		return this.channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public char getDataQuality() {
		return this.quality;
	}

	public Collection<Segment> getSegments() {
		return this.segments;
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (this.channelCode == null ? 0 : this.channelCode.hashCode());
		result = 31 * result;
		result = 31 * result + (this.location == null ? 0 : this.location.hashCode());
		result = 31 * result + (this.networkCode == null ? 0 : this.networkCode.hashCode());
		result = 31 * result + (this.stationCode == null ? 0 : this.stationCode.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			Timeseries other = (Timeseries)obj;
			if (this.channelCode == null) {
				if (other.channelCode != null) {
					return false;
				}
			} else if (!this.channelCode.equals(other.channelCode)) {
				return false;
			}

			if (this.location == null) {
				if (other.location != null) {
					return false;
				}
			} else if (!this.location.equals(other.location)) {
				return false;
			}

			if (this.networkCode == null) {
				if (other.networkCode != null) {
					return false;
				}
			} else if (!this.networkCode.equals(other.networkCode)) {
				return false;
			}

			if (this.stationCode == null) {
				if (other.stationCode != null) {
					return false;
				}
			} else if (!this.stationCode.equals(other.stationCode)) {
				return false;
			}

			return true;
		}
	}

	public String toString() {
		return "Timeseries [networkCode=" + this.networkCode + ", stationCode=" + this.stationCode + ", location=" + this.location + ", channelCode=" + this.channelCode + ", dataQuality=" + this.quality + ", numberOfSegments=" + this.segments.size() + "]";
	}
}
