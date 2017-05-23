package edu.iris.dmc.service.station.parser;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public interface StationParser extends Closeable {
	public List<Network> parse()
			throws IOException;
}
