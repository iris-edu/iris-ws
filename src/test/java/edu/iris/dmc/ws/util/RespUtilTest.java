package edu.iris.dmc.ws.util;

import edu.iris.dmc.TestFile;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RespUtilTest {

	@Test
	public void parse() {
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		List<Network> networks;
		try {
			networks = service.fetch(
					"http://service.iris.edu/fdsnws/station/1/query?net=AU&sta=MCQ&loc=--&cha=BHZ&starttime=2006-01-01&level=resp");

			Network networkIn = networks.get(0);
			Station stationIn = networkIn.getStations().get(0);
			List<Channel> channelsIn = stationIn.getChannels();
			Channel cIn1 = channelsIn.get(0);
			Channel cIn2 = channelsIn.get(1);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			RespUtil.write(os, networks);

			String text = os.toString();
			List<Station> list = RespUtil.parse(text);
			assertNotNull(list);
			assertEquals(1, list.size());
			Station s1 = list.get(0);
			assertEquals("MCQ", s1.getCode());
			List<Channel> channels = s1.getChannels();

			assertNotNull(channels);
			assertEquals(2, channels.size());
			Channel ch1 = channels.get(0);
			Channel ch2 = channels.get(1);
			assertEquals("BHZ", ch1.getCode());
			assertEquals("??", ch1.getLocationCode());
			assertEquals("2004-06-28T00:00:00",RespUtil.formatDate(ch1.getStartDate(),"yyyy-MM-dd'T'HH:mm:ss"));
			assertEquals("2008-04-27T23:59:59",RespUtil.formatDate(ch1.getEndDate(),"yyyy-MM-dd'T'HH:mm:ss"));
			assertEquals(cIn1.getStartDate(), ch1.getStartDate());
			assertEquals(cIn1.getEndDate(), ch1.getEndDate());

			assertEquals("BHZ", ch2.getCode());
			assertEquals("??", ch2.getLocationCode());
			assertEquals(cIn2.getStartDate(), ch2.getStartDate());
			assertEquals(cIn2.getEndDate(), ch2.getEndDate());
			
		} catch (NoDataFoundException | CriteriaException | IOException | ServiceNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void parseTD() throws Exception{
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		List<Network> networks;
		networks = service.fetch(
					"http://service.iris.edu/fdsnws/station/1/query?net=TD&sta=TD006&level=resp");


		ByteArrayOutputStream os = new ByteArrayOutputStream();
		RespUtil.write(os, networks);
		assertLinesEqual(FileUtils.readLines(TestFile.getFile("td.resp"), StandardCharsets.UTF_8),
				Arrays.asList(os.toString().split("\n")));
	}

	void assertLinesEqual(List<String> l1, List<String> l2)throws Exception{
		int i=0;
		int j = 0;
		while (i<l1.size()&&j<l2.size()) {
			String s1= l1.get(i).trim();
			String s2 = l2.get(i).trim();
			if(s1.isEmpty()){
				i++;
				continue;
			}
			if(s2.isEmpty()){
				j++;
				continue;
			}
			assertEquals(s1, s2);
			i++;
			j++;
		}
		for(;i<l1.size();i++){
			if(!l1.get(i).trim().isEmpty()){
				throw new AssertionError();
			}
		}

		for(;j<l2.size();j++){
			if(!l1.get(j).trim().isEmpty()){
				throw new AssertionError();
			}
		}
	}
}
