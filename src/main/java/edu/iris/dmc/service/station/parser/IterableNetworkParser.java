package edu.iris.dmc.service.station.parser;

import java.io.Closeable;
import java.util.Iterator;

import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;

public interface IterableNetworkParser extends Iterator<Network>, Closeable {

}
