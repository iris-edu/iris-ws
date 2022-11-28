package edu.iris.dmc.service;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import org.junit.Test;

import java.util.List;

public class StationServiceTest {

	@Test
	public void fetch() throws Exception {

		StationService service = ServiceUtil.getInstance().getStationService();
		List<Network> networks=service.fetch("https://fdsnws.raspberryshakedata.com/fdsnws/station/1/query?format=text&level=channel");
	}

	@Test
	public void fetchIU() throws Exception {

		StationService service = ServiceUtil.getInstance().getStationService();
		List<Network> networks=service.fetch("https://service.iris.edu/fdsnws/station/1/query?net=IU&sta=ANMO&loc=  ");
		Criteria criteria = StationCriteria.builder().netCode("IU").locCode(" ").build();
		System.out.println(criteria.toUrlParams().get(0));
		ServiceUtil.getInstance().getStationService().fetch(criteria);
	}
}
