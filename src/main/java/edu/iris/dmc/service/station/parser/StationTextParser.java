package edu.iris.dmc.service.station.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.TextStreamUtil;

public class StationTextParser extends AbstractStationParser {

	private BufferedReader reader;
	private boolean isClosed = false;
	protected OutputLevel level;

	public StationTextParser(InputStream inputStream, OutputLevel level) {
		super(inputStream, level);
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.level = level;
	}

	public boolean isClosed() {
		return isClosed;
	}

	//@Override
	public List<Network> parse() throws IOException {
		List<Network> networks = null;
		try {

			String line = null;
			// #Network | Description | StartTime | EndTime |TotalStations
			// #Network | Station | Latitude | Longitude | Elevation |SiteName
			// // | StartTime | EndTime
			// #Network | Station | Location |Channel | Latitude | Longitude |
			// // Elevation | Depth | Azimuth | Dip |Instrument | Scale | //
			// ScaleFreq | ScaleUnits | SampleRate | StartTime | EndTime
			networks = new ArrayList<Network>();
			Network network = null;
			Station station = null;
			String header = this.reader.readLine();

			while ((line = this.reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				String[] columns = line.split("\\|");
				if (level == OutputLevel.NETWORK) {
					List<String> list = Arrays.asList(columns);
					networks.add(TextStreamUtil.buildNetwork(list));
				} else if (level == OutputLevel.STATION
						|| level == OutputLevel.CHANNEL) {
					if (network == null
							|| !network.getCode().equals(columns[0])) {
						network = new Network();
						network.setCode(columns[0]);
						networks.add(network);
					}
					if (level == OutputLevel.STATION) {
						List<String> list = Arrays.asList(Arrays.copyOfRange(
								columns, 1, columns.length));
						network.addStation(TextStreamUtil.buildStation(list));
					} else {
						if (station == null
								|| !station.getCode().equals(columns[1])) {
							station = new Station();
							station.setCode(columns[1]);
							network.addStation(station);
						}
						List<String> list = Arrays.asList(Arrays.copyOfRange(
								columns, 2, columns.length));
						station.addChannel(TextStreamUtil.buildChannel(list));
					}
				} else {
					throw new IOException("Level: ['" + level.toString()
							+ "'] is not supported when format is TEXT.");
				}

			}
		} finally {
			if (this.reader != null) {
				this.reader.close();
			}
		}
		return networks;
	}

	@Override
	public void close() throws IOException {
		this.isClosed = true;
		if (this.reader != null) {
			this.reader.close();
		}
	}
}
