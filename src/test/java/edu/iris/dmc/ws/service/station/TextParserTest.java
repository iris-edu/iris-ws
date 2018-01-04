package edu.iris.dmc.ws.service.station;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.NetworkIterator;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;

public class TextParserTest {

	@Test
	public void parseTest() throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		List<Network> networks;

		StationCriteria c = new StationCriteria();
		c.addNetwork("1B");
		NetworkIterator it = service.iterateNetworks(c, OutputLevel.NETWORK);
		while(it.hasNext()){
			Network n=it.next();
			System.out.println(n+"   "+n.getTotalNumberStations());
		}
		it.close();

		//networks = service.fetch("http://service.iris.edu/fdsnws/station/1/query?net=8X&sta=CA01A&level=sta");

		//Network networkIn = networks.get(0);
	}
}
