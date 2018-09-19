package edu.iris.dmc.extensions.fetch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.iris.dmc.extensions.entities.Trace;

public class TraceDataTest {

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
		Trace[] traces = TraceData.fetchTraces("IU", "ANMO", "00", "BHZ", "2010-01-01 00:00:00.000",
				"2010-01-01 01:00:00.000", 'M', true, "nobody@iris.edu", "anonymous");
		
		for(Trace trace:traces) {
			System.out.println(trace);
			assertEquals(72000,trace.getSampleCount());
		}
	}
}
