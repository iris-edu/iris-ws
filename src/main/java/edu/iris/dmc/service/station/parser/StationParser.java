package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.fdsn.station.model.Network;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;


public interface StationParser extends Closeable {
	List<Network> parse() throws IOException;
}
