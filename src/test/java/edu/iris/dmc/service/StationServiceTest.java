package edu.iris.dmc.service;

import edu.iris.dmc.fdsn.station.model.Network;
import org.junit.Test;

import java.util.List;

public class StationServiceTest {

	@Test
	public void fetch() throws Exception {

		StationService service = ServiceUtil.getInstance().getStationService();
		List<Network> networks=service.fetch("https://fdsnws.raspberryshakedata.com/fdsnws/station/1/query?format=text&level=channel");
	}
}
