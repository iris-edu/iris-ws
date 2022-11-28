package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractStationParser implements StationParser {
	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;

	public AbstractStationParser(InputStream inputStream, OutputLevel level) {
		this.inputStream = inputStream;
		this.level = level;
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public void close() throws IOException {
		this.isClosed = true;
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		}

	}
}
