package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.fdsn.station.model.Network;

import java.io.Closeable;
import java.util.Iterator;

public interface IterableNetworkParser extends Iterator<Network>, Closeable {

}
