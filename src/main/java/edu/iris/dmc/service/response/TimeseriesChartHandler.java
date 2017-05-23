package edu.iris.dmc.service.response;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.service.ResponseHandler;
import edu.iris.dmc.timeseries.model.Util;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class TimeseriesChartHandler implements ResponseHandler<TimeseriesBufferedImage> {

	private Logger logger = Logger.getLogger(TimeseriesChartHandler.class.getName());

	private int width;
	private int height;
	private boolean reduce;
	private boolean showRecordLine;
	private boolean layered;

	public TimeseriesChartHandler() {
		this(1250, 400);
	}

	public TimeseriesChartHandler(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void showRecordLine(boolean showRecordLine) {
		this.showRecordLine = showRecordLine;
	}

	public void layered(boolean layered) {
		this.layered = layered;
	}

	public void reduce(boolean reduce) {
		this.reduce = reduce;
	}

	@Override
	public String getContentType() {
		return "image/png";
	}

	@Override
	public List<TimeseriesBufferedImage> handle(InputStream inputStream) throws IOException {
		logger.info("Handeling.......");
		DataInputStream dis = null;
		Map<String, TimeseriesBufferedImage> map = new HashMap<String, TimeseriesBufferedImage>();
		try {
			dis = new DataInputStream(inputStream);
			while (true) {
				try {
					SeedRecord sr = SeedRecord.read(dis);
					byte microseconds = 0;
					if (sr instanceof DataRecord) {
						DataRecord dr = (DataRecord) sr;
						Blockette[] bs = dr.getBlockettes(1001);
						if (bs.length > 0) {
							Blockette1001 b1001 = (Blockette1001) bs[0];
							microseconds = b1001.getMicrosecond();
						}

						if (dr.getBlockettes(1000).length != 0) {
							// ControlHeader
							DataHeader header = (DataHeader) dr.getControlHeader();

							String key = header.getNetworkCode() + header.getStationIdentifier();
							if (!layered) {
								key = key + header.getLocationIdentifier() + header.getChannelIdentifier();
							}

							TimeseriesBufferedImage timeseries = map.get(key);

							if (timeseries == null) {
								timeseries = TimeseriesBufferedImage.from(header.getNetworkCode(),
										header.getStationIdentifier(), header.getLocationIdentifier(),
										header.getChannelIdentifier(), this.width, this.height);
								timeseries.showRecordLine(this.showRecordLine);
								map.put(key, timeseries);
							}
							Timestamp startTime = Util.toTime(header.getStartBtime(), header.getActivityFlags(),
									header.getTimeCorrection(), microseconds);
							timeseries.add(startTime, dr, reduce);
						} else {
							// :TODO throw exception
						}
					} else {

					}
				} catch (EOFException eoe) {
					break;
				} catch (UnsupportedCompressionType e) {
					throw new IOException(e);
				} catch (SeedFormatException e) {
					throw new IOException(e);
				} catch (CodecException e) {
					throw new IOException(e);
				}
			}
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new ArrayList<>(map.values());
	}
}
