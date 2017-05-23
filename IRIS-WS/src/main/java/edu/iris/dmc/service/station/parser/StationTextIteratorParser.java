package edu.iris.dmc.service.station.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.TextStreamUtil;

public class StationTextIteratorParser implements IterableStationParser {

	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;

	private BufferedReader reader;
	private Network network;
	private Station station;

	public StationTextIteratorParser(InputStream is, OutputLevel level) {
		this.inputStream = is;
		this.level = level;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
	}

	//@Override
	public Station next() {
		String line = null;
		try {
			while ((line = this.reader.readLine()) != null) {
				if (line.trim().isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] columns = line.split("\\|");
				if (columns.length < 7) {
					throw new IOException("Invalid format: [" + line + "]");
				}

				if (network == null || !network.getCode().equals(columns[0])) {
					network = new Network();
					network.setCode(columns[0]);
				}

				if (level == OutputLevel.STATION) {
					List<String> list = Arrays.asList(Arrays.copyOfRange(
							columns, 1, columns.length));
					station = TextStreamUtil.buildStation(list);
					network.addStation(station);
					return station;
				} else {
					if (station == null) {
						station = new Station();
						station.setCode(columns[1]);
						network.addStation(station);
						List<String> list = Arrays.asList(Arrays.copyOfRange(
								columns, 2, columns.length));
						station.addChannel(TextStreamUtil.buildChannel(list));
					} else {
						if (!station.getCode().equals(columns[1])) {
							Station temp = station;
							station = new Station();
							station.setCode(columns[1]);
							network.addStation(station);
							List<String> list = Arrays.asList(Arrays
									.copyOfRange(columns, 2, columns.length));
							station.addChannel(TextStreamUtil
									.buildChannel(list));
							return temp;
						}
						List<String> list = Arrays.asList(Arrays.copyOfRange(
								columns, 2, columns.length));
						station.addChannel(TextStreamUtil.buildChannel(list));
					}

				}
			}
		} catch (IOException e) {
			System.err
					.println("StationTextIteratorParser: Unable to iterate lines, printing stack trace");
			e.printStackTrace();
		}
		return station;
	}

	//@Override
	public boolean hasNext() {
		if(this.isClosed){
			return false;
		}
		try {
			return this.reader.ready();
		} catch (IOException e) {
			System.err
					.println("StationTextIteratorParser: Unable to read buffer, printing stack trace");
			e.printStackTrace();
		}
		if (!this.isClosed) {
			try {
				this.close();
			} catch (IOException e) {
				System.err
						.println("StationTextIteratorParser: Unable to close file");
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isClosed() {
		return isClosed;
	}

	//@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	//@Override
	public void close() throws IOException {
		if (this.reader != null) {
			this.reader.close();
		}
		this.isClosed = true;

	}

}
