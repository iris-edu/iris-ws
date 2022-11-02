package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.TextStreamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class StationTextIteratorParser implements IterableStationParser {
	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;
	private BufferedReader reader;
	private Network network;
	private Station station;
	private String lineRead;

	public StationTextIteratorParser(InputStream is, OutputLevel level) {
		this.inputStream = is;
		this.level = level;
		this.reader = new BufferedReader(new InputStreamReader(this.inputStream));
	}

	String getLine() throws IOException {
		if (this.lineRead != null) {
			return this.lineRead;
		} else if (this.isClosed) {
			return null;
		} else {
			do {
				this.lineRead = this.reader.readLine();
			} while(this.lineRead != null && (this.lineRead.trim().isEmpty() || this.lineRead.startsWith("#")));

			return this.lineRead;
		}
	}

	public Station next() {
		String line = null;

		try {
			while((line = this.getLine()) != null) {
				String[] columns = line.split("\\|");
				if (columns.length < 7) {
					throw new IOException("Invalid format: [" + line + "]");
				}

				if (this.network == null || !this.network.getCode().equals(columns[0])) {
					this.network = new Network();
					this.network.setCode(columns[0]);
				}

				List list;
				if (this.level == OutputLevel.STATION) {
					list = Arrays.asList(Arrays.copyOfRange(columns, 1, columns.length));
					this.station = TextStreamUtil.buildStation(list);
					this.network.addStation(this.station);
					this.lineRead = null;
					return this.station;
				}

				if (this.station == null) {
					this.station = new Station();
					this.station.setCode(columns[1]);
					this.network.addStation(this.station);
					list = Arrays.asList(Arrays.copyOfRange(columns, 2, columns.length));
					this.station.addChannel(TextStreamUtil.buildChannel(list));
					this.lineRead = null;
				} else {
					if (!this.station.getCode().equals(columns[1])) {
						Station temp = this.station;
						this.station = null;
						return temp;
					}

					this.lineRead = null;
					list = Arrays.asList(Arrays.copyOfRange(columns, 2, columns.length));
					this.station.addChannel(TextStreamUtil.buildChannel(list));
				}
			}
		} catch (IOException var4) {
			System.err.println("StationTextIteratorParser: Unable to iterate lines, printing stack trace");
			var4.printStackTrace();
		}

		return this.station;
	}

	public boolean hasNext() {
		if (this.isClosed) {
			return false;
		} else {
			try {
				return this.getLine() != null;
			} catch (IOException var2) {
				System.err.println("StationTextIteratorParser: Unable to read buffer, printing stack trace");
				var2.printStackTrace();
				return false;
			}
		}
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() throws IOException {
		if (this.reader != null) {
			this.reader.close();
		}

		this.isClosed = true;
	}
}
