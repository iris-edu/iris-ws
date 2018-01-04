package edu.iris.dmc.service;

import java.util.List;

import org.junit.Test;

import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import edu.iris.dmc.ws.util.RespUtil;

public class SacpzServiceTest {

	@Test
	public void fetch() throws Exception {

		SacpzService service = ServiceUtil.getInstance().getSacpzService();
		SacpzCriteria criteria = new SacpzCriteria();
		criteria.addNetwork("OO").addStation("AXBA1").addChannel("HDH").addLocation("  ");
		criteria.setStartTime(DateUtil.parse("2017-01-01T00:00:00", "yyyy-MM-dd'T'HH:mm:ss"));
		criteria.setEndTime(DateUtil.parse("2017-01-01T01:00:00", "yyyy-MM-dd'T'HH:mm:ss"));
		List<Sacpz> list = service.fetch(criteria);
	}
}
