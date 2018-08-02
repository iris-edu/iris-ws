package edu.iris.dmc.extensions.fetch;

import org.junit.Test;

import edu.iris.dmc.extensions.entities.Trace;

public class TraceDataTest {

	@Test
	public void fetch() throws Exception {
		//IU&sta=ANMO&loc=00&cha=LHZ&start=2004-12-26T00:00:00&end=2004-12-27T00:00:00&nodata=404
		TraceData.setBASE_URL("http://service.iris.edu");
		Trace[] traces=TraceData.fetchTraces("IU", "ANMO", "00", "LHZ", "2004-12-26 00:00:00.000", "2004-12-27 00:00:00.000", 'B', true);
	}
}
