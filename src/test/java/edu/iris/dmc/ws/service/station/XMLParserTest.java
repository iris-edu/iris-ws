package edu.iris.dmc.ws.service.station;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.service.NetworkIterator;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;

public class XMLParserTest {

	@Test
	public void parseTest() throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		FileInputStream fis=new FileInputStream(new File("/Users/Suleiman/iu-test.xml"));
		List<Network> networks = service.load(fis);
				//.fetch("http://service.iris.edu/fdsnws/station/1/query?net=IU&sta=ANMO&channel=BHZ&loc=--&level=cha");
		for(Network n:networks){
			System.out.println(n);
			for(Station s:n.getStations()){
			System.out.println(s.getStartDate());
			}
		}

		fis.close();
		// networks =
		// service.fetch("http://service.iris.edu/fdsnws/station/1/query?net=8X&sta=CA01A&level=sta");

		// Network networkIn = networks.get(0);
	}
}
