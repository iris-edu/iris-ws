package edu.iris.dmc.timeseries;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.Type;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette100;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class DecompressedDataRecord {

	private final Type type;
	private final int numberOfSamples;
	private final float sampleRate;
	private final long startTime;
	private final long endTime;
	private final long expectedNextSampleTime;

	private DecompressedData record;

	private DecompressedDataRecord(DataRecord dataRecord, boolean reduce) throws CodecException, SeedFormatException {
		DataHeader dataHeader = dataRecord.getHeader();
		this.numberOfSamples = dataHeader.getNumSamples();
		Blockette[] bs = dataRecord.getBlockettes(100);
		if (bs != null && bs.length > 0) {
			Blockette100 b100 = (Blockette100) bs[0];
			this.sampleRate = b100.getActualSampleRate();
		} else {
			this.sampleRate = dataHeader.getSampleRate();
		}

		byte microseconds = 0;
		bs = dataRecord.getBlockettes(1001);
		if (bs.length > 0) {
			Blockette1001 b1001 = (Blockette1001) bs[0];
			microseconds = b1001.getMicrosecond();
		}
		this.startTime = Util.toLongTime(dataHeader.getStartBtime(), dataHeader.getActivityFlags(),
				dataHeader.getTimeCorrection(), microseconds);

		double durationinInSeconds = ((dataHeader.getNumSamples() - 1) / (double) this.sampleRate);

		this.endTime = Util.addSecondsToLong(this.startTime, durationinInSeconds);

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		long durationInMiliSecond = (long) ((this.numberOfSamples - 1) / this.sampleRate) * 1000;
		cal.setTimeInMillis(this.startTime + durationInMiliSecond);

		double d = (dataHeader.getNumSamples() / this.sampleRate) * 1000;
		cal.setTimeInMillis(this.startTime + (long) d);
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		this.expectedNextSampleTime = ts.getTime();
		Blockette1000 b1000 = (Blockette1000) dataRecord.getUniqueBlockette(1000);
		this.type = Type.from(b1000.getEncodingFormat());
		this.record = DecompressedData.of(this.type, dataRecord.getData(), this.numberOfSamples, reduce, false);
	}

	public static DecompressedDataRecord from(DataRecord dataRecord) throws SeedFormatException, CodecException {
		return DecompressedDataRecord.from(dataRecord, false);
	}

	public static DecompressedDataRecord from(DataRecord dataRecord, boolean reduce)
			throws SeedFormatException, CodecException {
		return new DecompressedDataRecord(dataRecord, reduce);
	}

	public Type getType() {
		return type;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getExpectedNextSampleTime() {
		return expectedNextSampleTime;
	}

	public DecompressedData getRecord() {
		return record;
	}

	public float getMinumumValue() {
		return this.record.getMin();
	}

	public float getMaximumValue() {
		return this.record.getMax();
	}
}
