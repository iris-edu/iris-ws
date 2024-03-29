package edu.iris.dmc.extensions.fetch;

import edu.iris.dmc.criteria.*;
import edu.iris.dmc.extensions.entities.Metadata;
import edu.iris.dmc.extensions.entities.Trace;
import edu.iris.dmc.extensions.utils.QualityLookup;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.service.*;
import edu.iris.dmc.timeseries.model.Segment;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.ws.util.DateUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;

public class TraceData {
	public static boolean VERBOSE = false;
	private static Logger logger = Logger.getLogger("edu.iris.dmc.ws.extensions.fetch.TraceData");
	private static String APP_NAME = "";
	public static String BASE_URL;
	public static String SACPZ_URL;
	public static String STATION_URL;
	public static String WAVEFORM_URL;
	private static boolean CONSOLE_HANDLER_ADDED = false;

	public TraceData() {
	}

	public static void setAppName(String appName) {
		APP_NAME = appName;
	}

	public static void setVerbosity(boolean verbose) {
		if (!CONSOLE_HANDLER_ADDED && verbose) {
			CONSOLE_HANDLER_ADDED = true;
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.FINER);
			Handler consHandler = new ConsoleHandler();
			consHandler.setLevel(Level.FINEST);
			final TimeZone tz = TimeZone.getDefault();
			consHandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					dateFormat.setTimeZone(tz);
					Calendar cal = Calendar.getInstance(tz);
					cal.setTimeZone(tz);
					return dateFormat.format(cal.getTime()) + " " + record.getLevel() + ": " + record.getSourceClassName() + " " + record.getSourceMethodName() + ": " + record.getMessage() + "\n";
				}
			});
			logger.addHandler(consHandler);
		}

		VERBOSE = verbose;
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel, String startDateStr, String endDateStr) throws Exception {
		return fetchTraces(network, station, location, channel, startDateStr, endDateStr, false);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel, String startDateStr, String endDateStr, boolean includePolesZeros) throws Exception {
		return fetchTraces(network, station, location, channel, startDateStr, endDateStr, includePolesZeros,
				null, null);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel, String startDateStr, String endDateStr, char qualityChar, boolean includePolesZeros) throws Exception {
		return fetchTraces(network, station, location, channel, startDateStr, endDateStr, qualityChar, includePolesZeros,
				null, null);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel, String startDateStr, String endDateStr, char qualityChar, boolean includePolesZeros, String username, String password) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS");
		Date startDate = DateUtil.parseAny(startDateStr);
		Date endDate = DateUtil.parseAny(endDateStr);
		return fetchTraces(network, station, location, channel, startDate, endDate, qualityChar, includePolesZeros, username, password);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel,
									  String startDateStr, String endDateStr, boolean includePolesZeros,
									  String username, String password) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS");
		Date startDate = DateUtil.parseAny(startDateStr);
		Date endDate = DateUtil.parseAny(endDateStr);
		return fetchTraces(network, station, location, channel, startDate, endDate, includePolesZeros, username, password);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel,
									  Date startDate, Date endDate, char qualityChar,
									  boolean includePolesZeros) throws Exception {
		return fetchTraces(network, station, location, channel, startDate, endDate, qualityChar, includePolesZeros, null, null);
	}

	public static Trace[] fetchTraces(String network, String station, String location, String channel,
									  Date startDate, Date endDate,
									  boolean includePolesZeros, String username, String password) throws Exception {
		StationCriteria sc = StationCriteria.builder().netCode(network).staCode(station).locCode(location).chanCode(channel).
				startTime(startDate).endTime(endDate).build();
		StationCriteria[] asc = new StationCriteria[]{sc};
		return fetchTraces(asc, username, password, includePolesZeros);
	}
	public static Trace[] fetchTraces(String network, String station, String location, String channel,
									  Date startDate, Date endDate, char qualityChar,
									  boolean includePolesZeros, String username, String password) throws Exception {
		StationCriteria sc = StationCriteria.builder().netCode(network).staCode(station).locCode(location).chanCode(channel).
				startTime(startDate).endTime(endDate).build();
		StationCriteria[] asc = new StationCriteria[]{sc};
		return fetchTraces(asc, qualityChar, includePolesZeros, username, password);
	}

	public static Trace[] fetchTraces(StationCriteria[] aSc, boolean includePolesZeros) throws Exception {
		return fetchTraces(aSc, null, null, includePolesZeros);
	}

	public static Trace[] fetchTraces(StationCriteria[] aSc, String username, String password, boolean includePolesZeros) throws Exception {
		return fetchTraces(aSc, username, password, includePolesZeros);
	}

	public static Trace[] fetchTraces(StationCriteria[] aSc, char qualityChar, boolean includePolesZeros) throws Exception {
		return fetchTraces(aSc, qualityChar, includePolesZeros, null, null);
	}

	public static Trace[] fetchTraces(StationCriteria[] aSc, char qualityChar, String username, String password, boolean includePolesZeros) throws IOException, NoDataFoundException, CriteriaException, Exception {
		return fetchTraces(aSc, qualityChar, includePolesZeros, username, password);
	}

	public static Trace[] fetchTraces(StationCriteria[] aSc, char qualityChar, boolean includePolesZeros, String username, String password) throws IOException, NoDataFoundException, CriteriaException, Exception {
		DateFormat sdfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		List<Trace> lTraces = new ArrayList<>();
		ServiceUtil serviceUtil;
		serviceUtil = ServiceUtil.getInstance();
		StationService ss = serviceUtil.getStationService();
		if (BASE_URL != null || STATION_URL != null) {
			if (BASE_URL != null) {
				ss.setBaseUrl(BASE_URL + "/fdsnws/station/1/");
			} else {
				ss.setBaseUrl(STATION_URL);
			}
		}

		ss.setAppName(APP_NAME);
		if (VERBOSE) {
			logger.info("StationService url set to: " + ss.getBaseUrl());
		}

		List<Network> ln;

		for (StationCriteria sc : aSc) {
			try {
				ln = ss.fetch(sc, OutputLevel.CHANNEL);
			} catch (NoDataFoundException var28) {
				if (VERBOSE) {
					logger.info(var28.getMessage());
				}
				continue;
			} catch (Exception e) {
				throw new IOException(e);
			}

			List<Metadata> lmd = Metadata.parseMetadata(ln);
			usrMessage("Found  " + lmd.size() + " applicable metadata");
			WaveformService ws = serviceUtil.getWaveformService();
			if (BASE_URL != null || WAVEFORM_URL != null) {
				if (BASE_URL != null) {
					ws.setBaseUrl(BASE_URL + "/fdsnws/dataselect/1/");
				}
				if (WAVEFORM_URL != null) {
					ws.setBaseUrl(WAVEFORM_URL);
				}
			}
			if (VERBOSE) {
				logger.info("WaveService url set to: " + ws.getBaseUrl());
			}
			ws.setAppName(APP_NAME);
			WaveformCriteria wc = new WaveformCriteria();
			wc.setQuality(QualityLookup.getQualityFromChar(qualityChar));
			for (Metadata md : lmd) {
				Date segStartDate;
				Date segEndDate;
				if (md.getStartDate().before(sc.getStartTime())) {
					segStartDate = sc.getStartTime();
				} else {
					segStartDate = md.getStartDate();
				}

				if (md.getEndDate() != null && !md.getEndDate().after(sc.getEndTime())) {
					double dpad = 1.0D / md.getSampleRate() * 0.75D;
					long lpad = Math.round(dpad * 1000.0D);
					long l;
					if (md.getEndDate() == null) {
						l = (new Date()).getTime();
					} else {
						l = md.getEndDate().getTime();
					}
					segEndDate = new Date(l - lpad);
				} else {
					segEndDate = sc.getEndTime();
				}
				wc.add(md.getNetwork(), md.getStation(), md.getLocation(), md.getChannel(), segStartDate, segEndDate);
				usrMessage("Requesting: " + md.getNetwork() + " " + md.getStation() + " " + md.getLocation() + " " + md.getChannel() + " " + sdfm.format(segStartDate) + " " + (segEndDate == null ? "*" : sdfm.format(segEndDate)));
			}
			usrMessage("Fetching waveform data");

			List<Timeseries> lts;
			try {
				wc.makeDistinctRequests(true);
				if (username != null && password != null) {
					lts = ws.fetch(wc, username, password);
				} else {
					lts = ws.fetch(wc);
				}
			} catch (NoDataFoundException e) {
				usrMessage("No data found for \n" + wc);
				continue;
			}

			SacpzService spzs = null;
			if (includePolesZeros) {
				spzs = serviceUtil.getSacpzService();
				if (SACPZ_URL != null) {
					spzs.setBaseUrl(SACPZ_URL + "/irisws/sacpz/1/");
				}

				spzs.setAppName(APP_NAME);
				if (VERBOSE) {
					logger.info("SacpzService url set to: " + spzs.getBaseUrl());
				}
			}

			for (Timeseries ts : lts) {
				for(Segment segment:ts.getSegments()){
					Metadata md = findMetaData(lmd, ts, segment);
					if (md == null) {
						throw new Exception("Internal: Could not correlate metadata with waveform data");
					}

					usrMessage("Matching waveform to metadata: \n\t" + md);
					Trace trace = new Trace(md, segment, ts.getDataQuality());
					if (includePolesZeros) {
						SacpzCriteria.SacpzCriteriaBuilder builder = SacpzCriteria.builder().netCode(ts.getNetworkCode()).chanCode(ts.getChannelCode()).
								locCode(ts.getLocation()).staCode(ts.getStationCode());
						ArrayList<Segment> segL = (ArrayList<Segment>) ts.getSegments();
						Segment seg = segL.get(0);
						builder.startTime(seg.getStartTime());
						seg = segL.get(segL.size() - 1);
						builder.endTime(seg.getEndTime());
						SacpzCriteria sacpzCriteria = builder.build();
						List<Sacpz> l = spzs.fetch(sacpzCriteria);
						if (l != null && l.size() > 0) {
							if (l.size() > 1) {
								throw new Exception("Problem interpreting SacPZ metadata for this time segment; multiple entries found!: " +
										sacpzCriteria.toUrlParams());
							}
							trace.setSacpz(l.get(0));
						}
					}
					lTraces.add(trace);
				}
			}
		}

		if (lTraces.isEmpty()) {
			throw new NoDataFoundException("");
		} else {
			return lTraces.toArray(new Trace[0]);
		}
	}

	private static Metadata findMetaData(List<Metadata> lmd, Timeseries ts, Segment s) {
		String network = ts.getNetworkCode().trim();
		String station = ts.getStationCode().trim();
		String location = ts.getLocation().replaceAll("--", "  ").trim();
		String channel = ts.getChannelCode().trim();
		Date startDate = s.getStartTime();
		Date endDate = s.getEndTime();
		for (Metadata md : lmd) {
			md.setLocation(md.getLocation().replaceAll("--", "  ").trim());

			if (md.getNetwork().equals(network) && md.getStation().equals(station) && md.getLocation().equals(location)
					&& md.getChannel().equals(channel)) {
				if ((md.getStartDate().compareTo(startDate) <= 0)
						&& (md.getEndDate() == null || (md.getEndDate().compareTo(endDate) >= 0))) {
					return md;
				}
			}
		}
		return null;
	}

	private static void usrMessage(String m) {
		if (VERBOSE) {
			logger.finer(m);
		}
	}

	public static void setBASE_URL(String baseUrl) {
		BASE_URL = baseUrl;
	}

	public static void setSACPZ_URL(String sacpzUrl) {
		SACPZ_URL = sacpzUrl;
	}

	public static void setSTATION_URL(String stationUrl) {
		STATION_URL = stationUrl;
	}

	public static void setWAVEFORM_URL(String waveformUrl) {
		WAVEFORM_URL = waveformUrl;
	}
}
