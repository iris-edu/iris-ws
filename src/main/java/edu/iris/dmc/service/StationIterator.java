package edu.iris.dmc.service;

import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.service.station.parser.IterableStationParser;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;

public class StationIterator implements Iterator<Station>, Closeable {
	private boolean isClosed;
	private HttpURLConnection connection;
	private IterableStationParser parser;

	public StationIterator(IterableStationParser parser) {
		this((HttpURLConnection)null, parser);
	}

	public StationIterator(HttpURLConnection connection, IterableStationParser parser) {
		this.isClosed = false;
		this.connection = connection;
		this.parser = parser;
	}

	public boolean hasNext() {
		boolean more = this.parser.hasNext();
		if (!more) {
			this.close();
		}

		return more;
	}

	public Station next() {
		return (Station)this.parser.next();
	}

	public void remove() {
		throw new UnsupportedOperationException("StationIterator.remove() is not supported");
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public void close() {
		if (this.connection != null) {
			this.connection.disconnect();
		}

		if (this.parser != null) {
			try {
				this.parser.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		}

		this.isClosed = true;
	}
}