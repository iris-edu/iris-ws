package edu.iris.dmc.service;

import java.util.List;

import org.junit.Test;

import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import edu.iris.dmc.ws.util.RespUtil;

public class StationServiceTest {

	@Test
	public void fetch() throws Exception {

		StationService service = ServiceUtil.getInstance().getStationService();
		List<Network> networks=service.fetch("https://fdsnws.raspberryshakedata.com/fdsnws/station/1/query?format=text&level=channel");
	}
}
