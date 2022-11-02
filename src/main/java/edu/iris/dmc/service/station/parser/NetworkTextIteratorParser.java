package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.DateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.ParseException;

public class NetworkTextIteratorParser implements IterableNetworkParser {
	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;
	private BufferedReader reader;
	private Network network;
	private Station station;
	private String lineRead;

	public NetworkTextIteratorParser(InputStream is, OutputLevel level) {
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

	public Network next() {
		try {
			String line = this.getLine();
			String[] columns = line.split("\\|");
			if (columns.length < 3) {
				throw new IOException("Invalid format: [" + line + "]");
			} else {
				this.network = new Network();
				this.network.setCode(columns[0]);
				this.network.setDescription(columns[1]);
				this.network.setStartDate(DateUtil.parseAny(columns[2]));

				String text;
				text = columns[3];
				if (text != null && !text.trim().isEmpty()) {
					this.network.setEndDate(DateUtil.parseAny(columns[3]));
				}

				if (columns.length > 4) {
					text = columns[4];
					this.network.setTotalNumberStations(new BigInteger(text));
				}

				this.lineRead = null;
				return this.network;
			}
		} catch (IOException e) {
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
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