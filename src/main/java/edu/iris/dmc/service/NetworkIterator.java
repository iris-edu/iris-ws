package edu.iris.dmc.service;

import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.station.parser.IterableNetworkParser;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;

public class NetworkIterator implements Iterator<Network>, Closeable {
	private boolean isClosed;
	private HttpURLConnection connection;
	private IterableNetworkParser parser;

	public NetworkIterator(IterableNetworkParser parser) {
		this((HttpURLConnection)null, parser);
	}

	public NetworkIterator(HttpURLConnection connection, IterableNetworkParser parser) {
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

	public Network next() {
		return (Network)this.parser.next();
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
