package edu.iris.dmc.ws.service.station;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TextParserTest {

	@Test
	public void parseTest() throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		List<Network> networks;
		try(NetworkIterator it = service.iterateNetworks(StationCriteria.builder().netCode("1B").build(), OutputLevel.NETWORK);) {
			while (it.hasNext()) {
				Network n = it.next();
				System.out.println(n + "   " + n.getTotalNumberStations());
			}
		}
	}
}
