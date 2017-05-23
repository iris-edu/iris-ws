package edu.iris.dmc.service.station.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.Station;

public abstract class AbstractStationParser implements StationParser {

	protected InputStream inputStream;
	private boolean isClosed = false;
	protected OutputLevel level;

	public AbstractStationParser(InputStream inputStream, OutputLevel level) {
		this.inputStream = inputStream;
		this.level = level;
	}


	public boolean isClosed() {
		return isClosed;
	}
	//@Override
	public void close() throws IOException {
		isClosed = true;
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
