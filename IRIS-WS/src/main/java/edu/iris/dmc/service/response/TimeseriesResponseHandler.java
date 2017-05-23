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

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.service.ResponseHandler;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.timeseries.model.Util;
import edu.iris.dmc.ws.util.DateUtil;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class TimeseriesResponseHandler implements ResponseHandler<Timeseries> {

	@Override
	public List<Timeseries> handle(InputStream inputStream) throws IOException {
		DataInputStream dis = null;
		Map<String, Timeseries> map = new HashMap<>();
		try {
			dis = new DataInputStream(inputStream);

			long start = System.currentTimeMillis();
			int count = 0;
			while (true) {
				try {
					SeedRecord sr = SeedRecord.read(dis);
					//byte microseconds = 0;
					if (sr instanceof DataRecord) {
						DataRecord dr = (DataRecord) sr;

						if (dr.getBlockettes(1000).length != 0) {
							// ControlHeader
							DataHeader header = (DataHeader) dr.getControlHeader();
							String network = header.getNetworkCode();
							String station = header.getStationIdentifier();
							String location = header.getLocationIdentifier();
							String channel = header.getChannelIdentifier();

							String key = createKey(network, station, location, channel);
							Timeseries timeseries = map.get(key);
							if (timeseries == null) {
								timeseries = Timeseries.from(network, station, location, channel);
								map.put(key, timeseries);
							}
							timeseries.add(dr,false);
						} else {
							// :TODO throw exception
						}
					} else {

					}
				} catch (EOFException eoe) {
					break;
				} catch (UnsupportedCompressionType e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SeedFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CodecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ArrayList<>(map.values());
	}

	private String createKey(String network, String station, String location, String channel) {
		StringBuilder builder = new StringBuilder();
		builder.append(network).append(station).append(location).append(channel);
		return builder.toString();
	}

	@Override
	public String getContentType() {
		return "application/octet-stream";
	}

}
