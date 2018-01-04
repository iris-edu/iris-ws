package edu.iris.dmc.service.station.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.ws.util.TextStreamUtil;

public class NetworkTextIteratorParser implements IterableNetworkParser {

	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;

	private BufferedReader reader;
	private Network network;
	private Station station;

	public NetworkTextIteratorParser(InputStream is, OutputLevel level) {
		this.inputStream = is;
		this.level = level;
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
	}

	private String lineRead;

	String getLine() throws IOException {
		if (this.lineRead != null) {
			return this.lineRead;
		}
		if (this.isClosed) {
			return null;
		}
		for (;;) {
			this.lineRead = this.reader.readLine();
			if (this.lineRead == null) {
				break;
			}
			if (this.lineRead.trim().isEmpty() || this.lineRead.startsWith("#")) {

			} else {
				break;
			}
		}
		return this.lineRead;
	}

	// @Override
	public Network next() {
		String line;
		try {
			line = getLine();

			String[] columns = line.split("\\|");
			if (columns.length < 3) {
				throw new IOException("Invalid format: [" + line + "]");
			}

			network = new Network();
			network.setCode(columns[0]);
			network.setDescription(columns[1]);

			try {
				Date start = TextStreamUtil.toDate(columns[2]);
				network.setStartDate(start);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				String text = columns[3];
				if (text != null && !text.trim().isEmpty()) {
					Date end = TextStreamUtil.toDate(columns[3]);
					network.setEndDate(end);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (columns.length > 4) {
				String text = columns[4];
				network.setTotalNumberStations(new BigInteger(text));
			}

			this.lineRead = null;
			return network;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// @Override
	public boolean hasNext() {
		if (this.isClosed) {
			return false;
		}
		try {
			if (getLine() != null) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("StationTextIteratorParser: Unable to read buffer, printing stack trace");
			e1.printStackTrace();
			return false;
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	// @Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	// @Override
	public void close() throws IOException {
		if (this.reader != null) {
			this.reader.close();
		}
		this.isClosed = true;

	}

}
