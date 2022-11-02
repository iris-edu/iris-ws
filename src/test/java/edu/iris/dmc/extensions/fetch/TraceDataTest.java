package edu.iris.dmc.extensions.fetch;

import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.extensions.entities.Trace;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TraceDataTest {

	// quality=M
	// IU ANMO 00 BHZ 2010-01-01 T00:00:00.000 2010-01-01 T01:00:00.000

	@Test
	public void fetchIU() throws Exception {
		Trace[] traces = TraceData.fetchTraces("IU", "ANMO", "00", "BHZ", "2010-01-01 00:00:00.000",
				"2010-01-01 01:00:00.000", 'M', false, null, null);
		
		for(Trace trace:traces) {
			System.out.println(trace);
			assertEquals(72000,trace.getSampleCount());
		}
	}
	
	@Test
	public void fetchAuthIU() throws Exception {
		Trace[] traces = TraceData.fetchTraces("IU", "ANMO", "00", "BHZ", "2010-01-01 00:00:00.000",
				"2010-01-01 01:00:00.000", 'M', true, "nobody@iris.edu", "anonymous");
		
		for(Trace trace:traces) {
			System.out.println(trace);
			assertEquals(72000,trace.getSampleCount());
		}
	}


	// quality=M
	// IU ANMO 00 BHZ 2010-01-01 T00:00:00.000 2010-01-01 T01:00:00.000

	@Test
	public void fetch() throws Exception {
		Trace[] traces = TraceData.fetchTraces("IU", "ANMO", "00", "BHZ", "2010-01-01 00:00:00.000",
				"2010-01-01 01:00:00.000", 'M', false, null, null);


		for(Trace trace:traces) {
			System.out.println(trace);
			assertEquals(72000,trace.getSampleCount());
		}
	}

	@Test
	public void fetchAuth() throws Exception {
		Trace[] traces = TraceData.fetchTraces("GS", "ASA1", "33",
				"LDF", "2019-01-01 00:00:00.000",
				"2022-01-01 01:00:00.000", 'M', true, "nobody@iris.edu", "anonymous");
		for(Trace trace:traces) {
			System.out.println(trace);
			assertEquals(4434,trace.getSampleCount());
		}
	}

	@Test
	public void n()throws Exception{
		Trace[] traces = TraceData.fetchTraces("IU", "ANMO", "00", "BHZ",
				"2022-07-26 00:00:00",
				"2022-07-28 01:00:00",
				'M', true, "nobody@iris.edu", "anonymous");

		for(Trace trace:traces) {
			System.out.println(trace);
			//assertEquals(72000,trace.getSampleCount());
		}
		//tr = irisFetch.Traces('CI','PASC','00','BHZ','2020-01-01 00:00:00','2020-01-01 01:00:00','verbose','STATIONURL:http://wslive6:8080/fdsnws/station/1/')
	}

	@Test
	public void sta()throws Exception{
		ServiceUtil su=ServiceUtil.getInstance();
		StationService ss=su.getStationService("http://wsbeta1:8080/fdsnwsbeta/station/1/");
		System.out.println("base url: "+ss.getBaseUrl());
		StationCriteria sc=new StationCriteria();
		sc.addNetwork("IU");
		ss.fetch(sc);
	}
}
