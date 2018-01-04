package edu.iris.dmc.extensions.fetch;

import org.junit.Test;

import edu.iris.dmc.extensions.entities.Trace;

public class TraceDataTest {

	@Test
	public void fetch() throws Exception {
		Trace[] traces=TraceData.fetchTraces("IU", "ANMO", "00", "BHZ", "2010-01-01 00:00:00.000", "2010-01-01 02:00:00.000", 'B', true);
	}
}
