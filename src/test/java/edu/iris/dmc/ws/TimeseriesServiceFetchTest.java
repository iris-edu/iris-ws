package edu.iris.dmc.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.iris.dmc.criteria.WaveformSearchCriteria;
import edu.iris.dmc.seedcodec.Type;
import edu.iris.dmc.service.IrisService;
import edu.iris.dmc.service.ServiceFactory;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.service.NoDataFoundException;

public class TimeseriesServiceFetchTest {

	private static String HOST = "service.iris.edu";
	private static int PORT = 8080;
	private static String API = "/fdsnwsbeta/dataselect/1/query";
	private static final double DELTA = 1e-15;
	private IrisService<Timeseries> service;

	@BeforeClass
	public static void setup() {


	}

	@Before
	public void setUp() throws FileNotFoundException, IOException {
		this.service = ServiceFactory.getTimeseriesService();
	}

	@Test(expected = NoDataFoundException.class)
	public void noDataFoundException() throws Exception {
		WaveformSearchCriteria criteria = WaveformSearchCriteria.of("IU", "dummy", null, null, "1995-07-14T01:00:00",
				"1995-07-14T03:00:00");
		this.service.fetch(criteria);
	}

	@Test
	public void queryauthRestricted() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/queryauth?net=XL&sta=HD35&loc=--&cha=HHE&start=2012-06-28T06:30:00.000&end=2012-06-29T10:30:00.000",
				"ameltzer@lehigh.edu", "R9CNbpay1Vzq");
	}

	//@Test(expected = NoDataFoundException.class)
	public void queryauthNobody() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/queryauth?net=XL&sta=HD35&loc=--&cha=HHE&start=2012-06-28T06:30:00.000&end=2012-06-29T10:30:00.000",
				"nobody@iris.edu", "anonymous");
	}

	@Test
	public void queryauthNobodyIncorrect() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/queryauth?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2009-02-23&endtime=2009-02-25",
				"nobody@iris.edu", "anonymous");
		assertEquals(1, result.size());
		}
	@Test
	public void queryauth() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/queryauth?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2009-02-23&endtime=2009-02-25",
				"nobody@iris.edu", "anonymous");

		assertEquals(1, result.size());

		Timeseries ts = result.get(0);

		assertEquals("IU", ts.getNetworkCode());
		assertEquals("ANMO", ts.getStationCode());
		assertEquals("BHZ", ts.getChannelCode());
		assertEquals("00", ts.getLocation());

		assertEquals(Type.from("STEIM2"), ts.getType());
		assertEquals('M', ts.getDataQuality());
		assertNotNull(ts.getSegments());

		assertEquals(1, ts.getSegments().size());

		Segment segment1 = ts.getSegments().get(0);
		assertEquals(segment1.getTotalNumberOfSamples(), segment1.getTotalNumberOfSamples());
		assertEquals(segment1.getType(), ts.getType());
		assertEquals(3424100, segment1.getTotalNumberOfSamples());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		assertEquals(sdf.parse("2009-02-23T00:00:00.000019"), segment1.getStartTime());
		assertEquals(sdf.parse("2009-02-24T15:10:27.000369"), segment1.getEndTime());
		assertEquals(sdf.parse("2009-02-24T15:10:27.000419"), segment1.getExpectedNextSampleTime());
		assertEquals(20.0, segment1.getSamplerate(), DELTA);

		Segment segment2 = ts.getSegments().get(1);
		assertEquals(segment2.getTotalNumberOfSamples(), segment2.getTotalNumberOfSamples());
		assertEquals(segment2.getType(), ts.getType());
		assertEquals(14040, segment2.getTotalNumberOfSamples());
		assertEquals(sdf.parse("2009-02-24T15:36:59.000419"), segment2.getStartTime());
		assertEquals(sdf.parse("2009-02-24T15:48:41.000369"), segment2.getEndTime());
		assertEquals(sdf.parse("2009-02-24T15:48:41.000419"), segment2.getExpectedNextSampleTime());
		assertEquals(20.0, segment2.getSamplerate(), DELTA);

		Segment segment3 = ts.getSegments().get(2);
		assertEquals(segment3.getTotalNumberOfSamples(), segment3.getTotalNumberOfSamples());
		assertEquals(segment3.getType(), ts.getType());
		assertEquals(589512, segment3.getTotalNumberOfSamples());
		assertEquals(sdf.parse("2009-02-24T15:48:44.000419"), segment3.getStartTime());
		assertEquals(sdf.parse("2009-02-24T23:59:59.000969"), segment3.getEndTime());
		assertEquals(sdf.parse("2009-02-25T00:00:00.000019"), segment3.getExpectedNextSampleTime());
		assertEquals(20.0, segment3.getSamplerate(), DELTA);

	}

	@Test
	public void fetchUrlWithMultipleSegments() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/query?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2009-02-23&endtime=2009-02-25");

		assertEquals(1, result.size());

		Timeseries ts = result.get(0);

		assertEquals("IU", ts.getNetworkCode());
		assertEquals("ANMO", ts.getStationCode());
		assertEquals("BHZ", ts.getChannelCode());
		assertEquals("00", ts.getLocation());

		assertEquals(Type.from("STEIM2"), ts.getType());
		assertEquals('M', ts.getDataQuality());
		assertNotNull(ts.getSegments());

		assertEquals(1, ts.getSegments().size());

		Segment segment1 = ts.getSegments().get(0);
		assertEquals(segment1.getTotalNumberOfSamples(), segment1.getTotalNumberOfSamples());
		assertEquals(segment1.getType(), ts.getType());
		assertEquals(3424100, segment1.getTotalNumberOfSamples());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		assertEquals(sdf.parse("2009-02-23T00:00:00.000019"), segment1.getStartTime());
		assertEquals(sdf.parse("2009-02-24T15:10:27.000369"), segment1.getEndTime());
		assertEquals(sdf.parse("2009-02-24T15:10:27.000419"), segment1.getExpectedNextSampleTime());
		assertEquals(20.0, segment1.getSamplerate(), DELTA);

		Segment segment2 = ts.getSegments().get(1);
		assertEquals(segment2.getTotalNumberOfSamples(), segment2.getTotalNumberOfSamples());
		assertEquals(segment2.getType(), ts.getType());
		assertEquals(14040, segment2.getTotalNumberOfSamples());
		assertEquals(sdf.parse("2009-02-24T15:36:59.000419"), segment2.getStartTime());
		assertEquals(sdf.parse("2009-02-24T15:48:41.000369"), segment2.getEndTime());
		assertEquals(sdf.parse("2009-02-24T15:48:41.000419"), segment2.getExpectedNextSampleTime());
		assertEquals(20.0, segment2.getSamplerate(), DELTA);

		Segment segment3 = ts.getSegments().get(2);
		assertEquals(segment3.getTotalNumberOfSamples(), segment3.getTotalNumberOfSamples());
		assertEquals(segment3.getType(), ts.getType());
		assertEquals(589512, segment3.getTotalNumberOfSamples());
		assertEquals(sdf.parse("2009-02-24T15:48:44.000419"), segment3.getStartTime());
		assertEquals(sdf.parse("2009-02-24T23:59:59.000969"), segment3.getEndTime());
		assertEquals(sdf.parse("2009-02-25T00:00:00.000019"), segment3.getExpectedNextSampleTime());
		assertEquals(20.0, segment3.getSamplerate(), DELTA);

	}

	@Test
	public void fetchUrl() throws Exception {
		List<Timeseries> result = this.service.fetch(
				"http://service.iris.edu/fdsnws/dataselect/1/query?net=IU&sta=ANMO&loc=00&cha=BHZ&start=2010-02-27T06:30:00.000&end=2010-02-27T10:30:00.000");

		assertEquals(1, result.size());

		Timeseries ts = result.get(0);

		assertEquals("IU", ts.getNetworkCode());
		assertEquals("ANMO", ts.getStationCode());
		assertEquals("BHZ", ts.getChannelCode());
		assertEquals("00", ts.getLocation());
		ts.getChannel();
		System.out.println(ts.getDataQuality());

		Collection<Segment> segments = ts.getSegments();

		for (Segment s : segments) {
			assertEquals(s.getTotalNumberOfSamples(), s.getTotalNumberOfSamples());
			System.out.println(s);
			System.out.println(s.getType());
			System.out.println(s.getStartTime());
			System.out.println(s.getEndTime());
			System.out.println(s.getExpectedNextSampleTime());
			System.out.println(s.getSamplerate());
			System.out.println(s.getData().size() + "   " + s.getTotalNumberOfSamples());
		}
	}

	@Test
	public void fetchCriteria() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date startDate = sdf.parse("1998-10-26T18:00:00.000000");
		Date endDate = sdf.parse("1998-10-26T20:00:00.000000");

		WaveformSearchCriteria criteria = WaveformSearchCriteria.of("IU", "ANMO", "--", "BHZ", startDate, endDate);
		List<Timeseries> result = this.service.fetch(criteria);
		assertEquals(1, result.size());

		Timeseries ts = result.get(0);

		assertEquals("IU", ts.getNetworkCode());
		assertEquals("ANMO", ts.getStationCode());
		assertEquals("BHZ", ts.getChannelCode());
		assertEquals("  ", ts.getLocation());

		assertEquals('M', ts.getDataQuality());

		List<Segment> segments = ts.getSegments();
		assertNotNull(segments);
		assertEquals(1, segments.size());

		Segment s = segments.get(0);
		assertEquals("STEIM2", s.getType().name());

		System.out.println(sdf.format(s.getExpectedNextSampleTime()));
		assertEquals(sdf.parse("1998-10-26T10:00:00.000040"), s.getStartTime());
		assertEquals(sdf.parse("1998-10-26T11:59:59.000990"), s.getEndTime());
		assertEquals(sdf.parse("1998-10-26T12:00:00.000040"), s.getExpectedNextSampleTime());

		assertEquals(20.0, s.getSamplerate(), DELTA);
		assertEquals(144000, s.getTotalNumberOfSamples());
	}

}
