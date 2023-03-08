package edu.iris.dmc.service;

import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.Station;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class StationServiceTest {


	@Test
	public void fetchAll() throws Exception {


		try(FileWriter writer=new FileWriter(new File("/Users/yazan/all_nodes.text"))) {
			Set<Node> nodes = new HashSet<>();
			StationService service = ServiceUtil.getInstance().getStationService();
			List<Network> networks = service.fetch("https://service.iris.edu/fdsnws/station/1/query?net=*&level=network");
			for (Network network : networks) {
				List<Network> networkStations = service.fetch("https://service.iris.edu/fdsnws/station/1/query?net=" + network.getCode() + "&level=station");
				for (Network n : networkStations) {
					for (Station s : n.getStations()) {
						List<Network> networkStationsChannels = service.fetch("https://service.iris.edu/fdsnws/station/1/query?net=" + network.getCode() + "&sta=" + s.getCode() + "&level=channel");
						for (Network nn : networkStationsChannels) {
							Node networkNode = new Node("FDSN:" + nn.getCode(), nn.getCode(), null, ZonedDateTime.ofInstant(nn.getStartDate().toInstant(), ZoneId.of("UTC")),
									nn.getEndDate() == null ? null : ZonedDateTime.ofInstant(nn.getEndDate().toInstant(), ZoneId.of("UTC")));
							nodes.add(networkNode);
							writer.write(networkNode.sourceIdentifier);
							writer.write(System.lineSeparator());
							for (Station ss : nn.getStations()) {
								Node stationNode = new Node(ss.getCode(), null, ZonedDateTime.ofInstant(ss.getStartDate().toInstant(), ZoneId.of("UTC")),
										ss.getEndDate() == null ? null : ZonedDateTime.ofInstant(ss.getEndDate().toInstant(), ZoneId.of("UTC")));
								networkNode.add(stationNode);
								System.out.println(stationNode.sourceIdentifier);
								writer.write(stationNode.sourceIdentifier);
								writer.write(System.lineSeparator());
								for (Channel cc : ss.getChannels()) {
									Node channelNode = new Node(cc.getCode(), cc.getLocationCode(), ZonedDateTime.ofInstant(cc.getStartDate().toInstant(), ZoneId.of("UTC")),
											cc.getEndDate() == null ? null : ZonedDateTime.ofInstant(cc.getEndDate().toInstant(), ZoneId.of("UTC")));
									stationNode.add(channelNode);
									System.out.println(channelNode.sourceIdentifier);
									writer.write(channelNode.sourceIdentifier);
									writer.write(System.lineSeparator());
									//all.add("FDSN:" + n.getCode() + "_" + s.getCode()+"_"+cc.getLocationCode()+"_"+cc.getCode().charAt(0)+"_"+cc.getCode().charAt(1)+"_"+cc.getCode().charAt(2));
								}
								break;
							}
						}
					}
				}
				//IU_COLA_00_B_H_Z
			}
			for (Node s : nodes) {
				System.out.println(s);
			}
		}
	}

	void print(Node node){
		if(node==null){
			return;
		}
		System.out.println(node.sourceIdentifier);
		for(Node child:node.children){
			print(child);
		}
	}
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

	public void fetchQuery()throws Exception{
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


class Node{
	String sourceIdentifier;
	String code;
	String locationCode;
	ZonedDateTime startDateTime;
	ZonedDateTime endDateTime;
	Node parent;
	List<Node> children;

	public Node(String code, String locationCode, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
		this(null, code, locationCode, startDateTime, endDateTime);
	}
	public Node(String sourceIdentifier, String code, String locationCode, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
		this.sourceIdentifier=sourceIdentifier;
		this.code = code;
		this.locationCode=locationCode;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}



	void add(Node child){
		child.parent=this;
		if(child.locationCode!=null){
			child.sourceIdentifier = this.sourceIdentifier + "_" + child.locationCode+"_"+
					child.code.charAt(0)+"_"+child.code.charAt(1)+"_"+child.code.charAt(2);
		}else {
			child.sourceIdentifier = this.sourceIdentifier + "_" + child.code;
		}
		if(this.children==null){
			this.children=new ArrayList<>();
		}
		this.children.add(child);
	}

	@Override
	public String toString() {
		return "Node{" +
				"sourceIdentifier='" + sourceIdentifier + '\'' +
				'}';
	}
}