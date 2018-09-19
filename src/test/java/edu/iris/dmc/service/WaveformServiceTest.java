package edu.iris.dmc.service;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.Before;
import org.junit.Test;

import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.WaveformService;
import edu.iris.dmc.timeseries.model.Timeseries;

public class WaveformServiceTest {
	WaveformService waveFormService;
	private ServiceUtil serviceManager;

	@Before
	public void setUp() throws FileNotFoundException, IOException {
		serviceManager = ServiceUtil.getInstance();
		this.waveFormService = serviceManager.getWaveformService();
	}

	@Test
	public void fetchAuth() throws Exception {
		ConsoleHandler loggingHandler = new ConsoleHandler();
        loggingHandler.setLevel(Level.ALL);
        loggingHandler.setFormatter(new SimpleFormatter());
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(loggingHandler);
        rootLogger.setLevel(Level.ALL);

        Logger httpLogger = Logger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
        httpLogger.setLevel(Level.ALL);
       // net=IU&sta=ANMO&loc=00&cha=BHZ&start=2010-02-27T06:30:00.000&end=2010-02-27T10:30:00.000
		
		String network = "IU";
		String station = "ANMO";
		String channel = "BHZ";
		String location = "00";
		String startDateStr = "2012-02-27 06:00:00.000";
		String endDateStr = "2012-02-27 10:00:00.000";
		char quality = 'B';
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date a = dfm.parse(startDateStr);

		Date b = dfm.parse(endDateStr);
		try {
			waveFormService.setAuth("nobody@iris.edu", "anonymous");

			// IU ANMO 00 BHZ 2010-084T00:00:00 2010-091T00:00:00
			WaveformCriteria criteria = new WaveformCriteria();
			criteria.add(network, station, location, channel, a, b)
					.makeDistinctRequests(true);

			List<Timeseries> timeSeriesCollection = waveFormService
					.fetch(criteria);
			System.out.println(">>>>>>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	public void fetchAuthUserPass() throws Exception {
		String network = "IU";
		String station = "ANMO";
		String channel = "BHZ";
		String location = "00";
		String startDateStr = "2012-02-20 00:00:00.000";
		String endDateStr = "2012-02-20 1:00:00.000";
		char quality = 'B';
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date a = dfm.parse(startDateStr);

		Date b = dfm.parse(endDateStr);
		try {
			waveFormService.setAuth("nobody@iris.edu", "anonymous");

			// IU ANMO 00 BHZ 2010-084T00:00:00 2010-091T00:00:00
			WaveformCriteria criteria = new WaveformCriteria();
			criteria.add(network, station, location, channel, a, b)
					.makeDistinctRequests(true);

			List<Timeseries> timeSeriesCollection = waveFormService.fetch(
					criteria, "nobody@iris.edu", "anonymous");
			System.out.println(">>>>>>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

