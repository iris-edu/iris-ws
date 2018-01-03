package edu.iris.dmc.extensions.fetch;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.extensions.entities.Metadata;
import edu.iris.dmc.extensions.entities.Trace;
import edu.iris.dmc.extensions.utils.QualityLookup;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.SacpzService;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;
import edu.iris.dmc.service.WaveformService;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;

public class TraceData {
	public static boolean VERBOSE = false;
	private static Logger logger = Logger
			.getLogger("edu.iris.dmc.ws.extensions.fetch.TraceData");
	private static String APP_NAME = "";

	public static String BASE_URL;

	public static String SACPZ_URL;
	public static String STATION_URL;
	public static String WAVEFORM_URL;
	private static boolean CONSOLE_HANDLER_ADDED = false;

	public static void setAppName(String appName) {
		APP_NAME = appName;
	}

	public static void setVerbosity(boolean verbose) {
		// needed this for matlab!

		if (!CONSOLE_HANDLER_ADDED && verbose) {
			CONSOLE_HANDLER_ADDED = true;
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.FINER);
			Handler consHandler = new ConsoleHandler();
			consHandler.setLevel(Level.FINEST);
			final TimeZone tz = TimeZone.getDefault();
			consHandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					dateFormat.setTimeZone(tz);
					Calendar cal = Calendar.getInstance(tz);
					cal.setTimeZone(tz);
					return dateFormat.format(cal.getTime()) + " "
							+ record.getLevel() + ": "
							+ record.getSourceClassName() + " "
							+ record.getSourceMethodName() + ": "
							+ record.getMessage() + "\n";
				}
			});
			// Feb 3, 2014 7:11:25 PM edu.iris.dmc.extensions.fetch.TraceData
			// fetchTraces
			// INFO: StationService url set to:
			// http://service.iris.edu/fdsnws/station/1/

			// Logger.getLogger("").addHandler(consHandler);

			logger.addHandler(consHandler);
		}
		TraceData.VERBOSE = verbose;

	}

	/**
	 * 
	 * @param network
	 * @param station
	 * @param location
	 * @param channel
	 * @param startDateStr
	 * @param endDateStr
	 * @param qualityChar
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(String network, String station,
			String location, String channel, String startDateStr,
			String endDateStr, char qualityChar, boolean includePolesZeros)
			throws Exception {
		return fetchTraces(network, station, location, channel, startDateStr,
				endDateStr, qualityChar, includePolesZeros, null, null);
	}

	/**
	 * 
	 * @param network
	 * @param station
	 * @param location
	 * @param channel
	 * @param startDateStr
	 * @param endDateStr
	 * @param qualityChar
	 * @param includePolesZeros
	 * @param username
	 * @param password
	 * @return Trace[]
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(String network, String station,
			String location, String channel, String startDateStr,
			String endDateStr, char qualityChar, boolean includePolesZeros,
			String username, String password) throws Exception {

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		// This supports the Matlab 'DateString' format which is rather generic,
		// BTW.
		// Can be easily constructed in Matlab via datestr([2003 10 24 12 45
		// 07]), \
		// which returns "2003-10-24 12:45:07"
		final DateFormat sdfm = new SimpleDateFormat(
				"yyyy-MM-dd' 'HH:mm:ss.SSS");

		Date startDate = sdfm.parse(startDateStr);
		Date endDate = sdfm.parse(endDateStr);
		return fetchTraces(network, station, location, channel, startDate,
				endDate, qualityChar, includePolesZeros, username, password);
	}

	/**
	 * 
	 * @param network
	 * @param station
	 * @param location
	 * @param channel
	 * @param startDate
	 * @param endDate
	 * @param qualityChar
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(String network, String station,
			String location, String channel, Date startDate, Date endDate,
			char qualityChar, boolean includePolesZeros) throws IOException,
			NoDataFoundException, CriteriaException, Exception {
		return fetchTraces(network, station, location, channel, startDate,
				endDate, qualityChar, includePolesZeros, null, null);
	}

	/**
	 * 
	 * @param network
	 * @param station
	 * @param location
	 * @param channel
	 * @param startDate
	 * @param endDate
	 * @param qualityChar
	 * @param includePolesZeros
	 * @param username
	 * @param password
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(String network, String station,
			String location, String channel, Date startDate, Date endDate,
			char qualityChar, boolean includePolesZeros, String username,
			String password) throws IOException, NoDataFoundException,
			CriteriaException, Exception {

		StationCriteria sc = new StationCriteria();
		sc.addNetwork(network).addStation(station).addLocation(location)
				.addChannel(channel);
		sc.setStartTime(startDate).setEndTime(endDate);

		StationCriteria[] asc = new StationCriteria[1];
		asc[0] = sc;
		return fetchTraces(asc, qualityChar, includePolesZeros, username,
				password);
	}

	// Methods based on StationCriteria

	/**
	 * 
	 * @param aSc
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(StationCriteria[] aSc,
			boolean includePolesZeros) throws IOException,
			NoDataFoundException, CriteriaException, Exception {
		return fetchTraces(aSc, null, includePolesZeros, null, null);
	}

	/**
	 * 
	 * @param aSc
	 * @param username
	 * @param password
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(StationCriteria[] aSc, String username,
			String password, boolean includePolesZeros) throws IOException,
			NoDataFoundException, CriteriaException, Exception {
		return fetchTraces(aSc, null, includePolesZeros, username, password);
	}

	/**
	 * 
	 * @param aSc
	 * @param qualityChar
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(StationCriteria[] aSc,
			Character qualityChar, boolean includePolesZeros)
			throws IOException, NoDataFoundException, CriteriaException,
			Exception {
		return fetchTraces(aSc, qualityChar, includePolesZeros, null, null);
	}

	/**
	 * 
	 * @param aSc
	 * @param qualityChar
	 * @param username
	 * @param password
	 * @param includePolesZeros
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(StationCriteria[] aSc,
			Character qualityChar, String username, String password,
			boolean includePolesZeros) throws IOException,
			NoDataFoundException, CriteriaException, Exception {
		return fetchTraces(aSc, qualityChar, includePolesZeros, username,
				password);
	}

	/**
	 * 
	 * @param aSc
	 * @param qualityChar
	 * @param includePolesZeros
	 * @param username
	 * @param password
	 * @return Trace[]
	 * @throws IOException
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws Exception
	 */
	public static Trace[] fetchTraces(StationCriteria[] aSc,
			Character qualityChar, boolean includePolesZeros, String username,
			String password) throws IOException, NoDataFoundException,
			CriteriaException, Exception {

		final DateFormat sdfm = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS");

		List<Trace> lTraces = new ArrayList<Trace>();

		// Initialize service architecture.
		ServiceUtil serviceUtil = null;
		serviceUtil = ServiceUtil.getInstance();

		StationService ss = serviceUtil.getStationService();

		if (TraceData.BASE_URL != null || TraceData.STATION_URL != null) {
			if (TraceData.BASE_URL != null) {
				ss.setBaseUrl(TraceData.BASE_URL + "/fdsnws/station/1/");
			} else if (TraceData.STATION_URL != null) {
				ss.setBaseUrl(TraceData.STATION_URL);
			}
		}
		ss.setAppName(APP_NAME);
		if (VERBOSE) {
			logger.info("StationService url set to: " + ss.getBaseUrl());
		}

		// For each station criteria object, fetch the matching results from the
		// Station Service
		// and use those to request waveform data.
		List<Network> ln = null;
		for (int i = 0; i < aSc.length; i++) {
			StationCriteria sc = aSc[i];

			if (sc.getStartTime() == null || sc.getEndTime() == null) {
				throw new CriteriaException(
						"startTime and endTime are required.");
			}

			try {
				ln = ss.fetch(sc, OutputLevel.CHANNEL);

			} catch (NoDataFoundException e) {
				if (VERBOSE) {
					logger.info(e.getMessage());
				}
				continue;

			} catch (Exception e) {
				throw new IOException(e);
			}

			List<Metadata> lmd = Metadata.parseMetadata(ln);
			usrMessage("Found  " + lmd.size() + " applicable metadata");

			WaveformService ws = serviceUtil.getWaveformService();
			if (TraceData.BASE_URL != null || TraceData.WAVEFORM_URL != null) {
				if (TraceData.BASE_URL != null) {
					ws.setBaseUrl(TraceData.BASE_URL + "/fdsnws/dataselect/1/");
				}

				if (TraceData.WAVEFORM_URL != null) {
					ws.setBaseUrl(TraceData.WAVEFORM_URL);
				}
			}

			if (VERBOSE) {
				logger.info("WaveService url set to: " + ws.getBaseUrl());
			}

			ws.setAppName(APP_NAME);
			
			WaveformCriteria wc = new WaveformCriteria();

			if (qualityChar != null)
				wc.setQuality(QualityLookup.getQualityFromChar(qualityChar));

			// Align waveform request start and end dates with metadata
			// responses.
			// I.e., don't 'span' epochs in a single waveform data request.
			for (Metadata md : lmd) {
				Date segStartDate = null, segEndDate = null;

				if (md.getStartDate().before(sc.getStartTime())) {
					segStartDate = sc.getStartTime();
				} else {
					segStartDate = md.getStartDate();
				}

				if (md.getEndDate() == null
						|| md.getEndDate().after(sc.getEndTime())) {
					segEndDate = sc.getEndTime();
				} else {
					// Subtract .75 sample of time here to avoid having the last
					// sample
					// duplicated in two adjoining segments.
					double dpad = (1. / md.getSampleRate()) * 0.75;
					long lpad = Math.round(dpad * 1000.);
					long l = 0;

					if (md.getEndDate() == null) {
						l = new Date().getTime();
					} else {
						l = md.getEndDate().getTime();
					}

					segEndDate = new Date(l - lpad);
				}

				wc.add(md.getNetwork(), md.getStation(), md.getLocation(),
						md.getChannel(), segStartDate, segEndDate);
				usrMessage("Requesting: " + md.getNetwork() + " "
						+ md.getStation() + " " + md.getLocation() + " "
						+ md.getChannel() + " " + sdfm.format(segStartDate)
						+ " " + sdfm.format(segEndDate));
			}

			usrMessage("Fetching waveform data");
			List<Timeseries> lts = null;
			try {
				wc.makeDistinctRequests(true);
				if (username != null && password != null) {
					lts = ws.fetch(wc, username, password);
				} else {
					lts = ws.fetch(wc);
				}
			} catch (NoDataFoundException e) {
				usrMessage("No data found for \n" + wc.toString());
				continue;
			}
			SacpzService spzs = null;
			if (includePolesZeros) {
				spzs = serviceUtil.getSacpzService();

				if (TraceData.SACPZ_URL != null) {
					spzs.setBaseUrl(TraceData.SACPZ_URL + "/irisws/sacpz/1/");
				}
				spzs.setAppName(TraceData.APP_NAME);

				if (VERBOSE) {
					logger.info("SacpzService url set to: " + spzs.getBaseUrl());
				}
			}

			for (Timeseries ts : lts) {
				for (Segment s : ts.getSegments()) {
					Metadata md = findMetaData(lmd, ts, s);
					if (md == null) {
						throw new Exception(
								"Internal: Could not correlate metadata with waveform data");
					}
					usrMessage("Matching waveform to metadata: \n\t" + md);

					Trace trace = new Trace(md, s, ts.getDataQuality());

					if (includePolesZeros) {
						SacpzCriteria spzc = new SacpzCriteria();
						spzc.addNetwork(ts.getNetworkCode())
								.addChannel(ts.getChannelCode())
								.addLocation(ts.getLocation())
								.addStation(ts.getStationCode());

						ArrayList<Segment> segL = (ArrayList<Segment>) ts
								.getSegments();
						Segment seg = segL.get(0);
						spzc.setStartTime(seg.getStartTime());
						seg = segL.get(segL.size() - 1);
						spzc.setEndTime(seg.getEndTime());

						List<Sacpz> l = spzs.fetch(spzc);
						if (l != null && l.size() > 0) {
							if (l.size() > 1) {
								throw new Exception(
										"Problem interpreting SacPZ metedata for this time segment; multiple entries found!: "
												+ spzc.toUrlParams());
							} else {
								trace.setSacpz(l.get(0));
							}
						}
					}
					lTraces.add(trace);
				}
			}

		}
		if (lTraces.isEmpty()) {
			throw new NoDataFoundException("");
		}
		return (Trace[]) lTraces.toArray(new Trace[lTraces.size()]);
	}

	/**
	 * Return the 1st matching meta data object (if any) for a particular time
	 * series segment. This is based on NSLC and time window.
	 */
	private static Metadata findMetaData(List<Metadata> lmd, Timeseries ts,
			Segment s) {
		String network = ts.getNetworkCode().trim();
		String station = ts.getStationCode().trim();
		String location = ts.getLocation().replaceAll("--", "  ").trim();
		String channel = ts.getChannelCode().trim();

		Date startDate = s.getStartTime();
		Date endDate = s.getEndTime();
		for (Metadata md : lmd) {
			md.setLocation(md.getLocation().replaceAll("--", "  ").trim());

			if (md.getNetwork().equals(network)
					&& md.getStation().equals(station)
					&& md.getLocation().equals(location)
					&& md.getChannel().equals(channel)) {
				if ((md.getStartDate().compareTo(startDate) <= 0)
						&& (md.getEndDate() == null || (md.getEndDate()
								.compareTo(endDate) >= 0))) {
					return md;
				}
			}
		}
		return null;
	}

	// Utility functions
	private static void usrMessage(String m) {
		if (!VERBOSE)
			return;
		TraceData.logger.finer(m);
	}

	public static void setBASE_URL(String bASE_URL) {
		BASE_URL = bASE_URL;
	}

	public static void setSACPZ_URL(String sACPZ_URL) {
		SACPZ_URL = sACPZ_URL;
	}

	public static void setSTATION_URL(String sTATION_URL) {
		STATION_URL = sTATION_URL;
	}

	public static void setWAVEFORM_URL(String wAVEFORM_URL) {
		WAVEFORM_URL = wAVEFORM_URL;
	}

}
