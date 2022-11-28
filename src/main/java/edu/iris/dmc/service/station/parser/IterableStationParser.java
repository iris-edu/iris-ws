package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.fdsn.station.model.Station;

import java.io.Closeable;
import java.util.Iterator;

public interface IterableStationParser extends Iterator<Station>, Closeable {

}
