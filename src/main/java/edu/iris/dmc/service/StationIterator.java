package edu.iris.dmc.service;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;

import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.service.station.parser.IterableStationParser;

public class StationIterator implements Iterator<Station>, Closeable {

	private boolean isClosed = false;

	private HttpURLConnection connection;
	private IterableStationParser parser;

	public StationIterator(IterableStationParser parser) {
		this(null, parser);
	}

	public StationIterator(HttpURLConnection connection,
			IterableStationParser parser) {
		this.connection = connection;
		this.parser = parser;
	}

	//@Override
	public boolean hasNext() {
		boolean more = this.parser.hasNext();
		if (!more) {
			this.close();
		}
		return more;
	}

	//@Override
	public Station next() {
		return this.parser.next();
	}

	//@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"StationIterator.remove() is not supported");
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void close() {
		if (this.connection != null) {
			this.connection.disconnect();
		}

		if (this.parser != null) {
			try {
				this.parser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isClosed = true;
	}

}
