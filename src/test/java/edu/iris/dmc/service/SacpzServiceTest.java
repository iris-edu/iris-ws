package edu.iris.dmc.service;

import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import org.junit.Test;

import java.util.List;

public class SacpzServiceTest {

	@Test
	public void fetch() throws Exception {

		SacpzService service = ServiceUtil.getInstance().getSacpzService();
		SacpzCriteria.SacpzCriteriaBuilder builder=SacpzCriteria.builder().netCode("OO").staCode("AXBA1").chanCode("HDH").locCode("  ").
				startTime(DateUtil.parse("2017-01-01T00:00:00", "yyyy-MM-dd'T'HH:mm:ss")).
				endTime(DateUtil.parse("2017-01-01T01:00:00", "yyyy-MM-dd'T'HH:mm:ss"));
		SacpzCriteria sacpzCriteria=builder.build();
		System.out.println(sacpzCriteria.toUrlParams());
		List<Sacpz> list = service.fetch(sacpzCriteria);
	}
}
