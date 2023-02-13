package edu.iris.dmc.service;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.Network;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class StationServiceTest {

	@Test
	public void fetch() throws Exception {

		StationService service = ServiceUtil.getInstance().getStationService();
		List<Network> networks=service.fetch("https://fdsnws.raspberryshakedata.com/fdsnws/station/1/query?format=text&level=channel");
	}

	@Test
	public void fetchIU() throws Exception {

		for( int i=0;i<1000;i++) {
			StationService service = ServiceUtil.getInstance().getStationService();
			List<Network> networks = service.fetch("https://service.iris.edu/fdsnws/station/1/query?net=IU&cha=HH*,BH*");
			System.out.println(i);
			//Criteria criteria = StationCriteria.builder().netCode("IU").locCode(" ").build();
			//System.out.println(criteria.toUrlParams().get(0));
			//ServiceUtil.getInstance().getStationService().fetch(criteria);
		}
	}

	@Test
	void fetchQuery()throws Exception{
		URL url = new URL("https://service.iris.edu/fdsnws/station/1/query?net=IU&cha=HH*,BH*");
		for( int i=0;i<1000;i++) {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "test_station");
			connection.setRequestProperty("Accept", "application/xml");
			//connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.connect();
			int responseCode = connection.getResponseCode();
			System.out.println(connection.getHeaderFields());

			System.out.println(url + ":" + connection.getContentEncoding());
			try (InputStream inputStream = responseCode != 200 ? connection.getErrorStream() :
					("gzip".equals(connection.getContentEncoding()) ?
							new GZIPInputStream(connection.getInputStream()) : connection.getInputStream());) {
				String text = new BufferedReader(
						new InputStreamReader(inputStream, StandardCharsets.UTF_8))
						.lines()
						.collect(Collectors.joining("\n"));
				System.out.println(text);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}
		}


	}
}
