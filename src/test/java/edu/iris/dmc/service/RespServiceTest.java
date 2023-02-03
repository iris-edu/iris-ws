package edu.iris.dmc.service;

import edu.iris.dmc.criteria.RespCriteria;
import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import org.junit.Test;

import java.util.List;

public class RespServiceTest {

	@Test
	public void fetch() throws Exception {

		RespService service = ServiceUtil.getInstance().getRespService();
		service.fetch("https://service.iris.edu/irisws/resp/1/query?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2005-001T00:00:00&end=2008-001T00:00:00");

	}
}
