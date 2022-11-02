package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.TextStreamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		return this.isClosed;
	}

	public List<Network> parse() throws IOException {
		List<Network> networks = null;

		try {
			String line = null;
			networks = new ArrayList<>();
			Network network = null;
			Station station = null;
			String var5 = this.reader.readLine();

			while((line = this.reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					String[] columns = line.split("\\|");
					List<String> list;
					if (this.level == OutputLevel.NETWORK) {
						list = Arrays.asList(columns);
						networks.add(TextStreamUtil.buildNetwork(list));
					} else {
						if (this.level != OutputLevel.STATION && this.level != OutputLevel.CHANNEL) {
							throw new IOException("Level: ['" + this.level.toString() + "'] is not supported when format is TEXT.");
						}

						if (network == null || !network.getCode().equals(columns[0])) {
							network = new Network();
							network.setCode(columns[0]);
							networks.add(network);
						}

						if (this.level == OutputLevel.STATION) {
							list = Arrays.asList(Arrays.copyOfRange(columns, 1, columns.length));
							network.addStation(TextStreamUtil.buildStation(list));
						} else {
							if (station == null || !station.getCode().equals(columns[1])) {
								station = new Station();
								station.setCode(columns[1]);
								network.addStation(station);
							}

							list = Arrays.asList(Arrays.copyOfRange(columns, 2, columns.length));
							station.addChannel(TextStreamUtil.buildChannel(list));
						}
					}
				}
			}
		} finally {
			if (this.reader != null) {
				this.reader.close();
			}

		}

		return networks;
	}

	public void close() throws IOException {
		this.isClosed = true;
		if (this.reader != null) {
			this.reader.close();
		}

	}
}

