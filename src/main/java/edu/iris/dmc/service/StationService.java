package edu.iris.dmc.service;


import edu.iris.dmc.criteria.*;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.station.parser.*;
import edu.iris.dmc.ws.util.StringUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class StationService extends BaseService {
	private static final Logger logger = Logger.getLogger(StationService.class.getName());
	private FDSNStationXML root;

	public StationService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	private IterableStationParser getIterableParser(InputStream inputStream, Map<String, String> queryKeyValue, OutputLevel level) throws Exception {
			OutputFormat outputFormat = this.extractFormat(queryKeyValue);
			if (outputFormat == OutputFormat.TEXT) {
				if (OutputLevel.NETWORK == level) {
					throw new CriteriaException("Invalid level/format combination. Level must be station|channel");
				} else {
					return new StationTextIteratorParser(inputStream, level);
				}
			} else if (outputFormat == OutputFormat.XML) {
				if (OutputLevel.NETWORK == level) {
					throw new CriteriaException("Invalid level/format combination. Level must be station|channel|response");
				} else {
					return new StationXMLIteratorParser(inputStream, level);
				}
			} else {
				throw new CriteriaException("Format: ['" + outputFormat.toString() + "'] is not supported");
			}
	}

	private StationParser getParser(InputStream inputStream, Map<String, String> queryKeyValue, OutputLevel level) throws CriteriaException, IOException {
		OutputFormat outputFormat = this.extractFormat(queryKeyValue);
		if (outputFormat == OutputFormat.TEXT) {
			return new StationTextParser(inputStream, level);
		} else if (outputFormat == OutputFormat.XML) {
			return new StationXMLParser(inputStream, level);
		} else {
			throw new CriteriaException("Format: ['" + outputFormat.toString() + "'] is not supported");
		}
	}

	public NetworkIterator iterateNetworks(Criteria criteria, OutputLevel level) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		((StationCriteria)criteria).setFormat(OutputFormat.TEXT);
		String urlString = this.baseUrl + "query?" + (String) criteria.toUrlParams().get(0) + "&level=network";
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		connection = this.getConnection(urlString);
		connection.setRequestMethod("GET");
		String uAgent = this.userAgent;
		if (this.getAppName() != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		connection.setRequestProperty("User-Agent", uAgent);
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.connect();
		int responseCode = connection.getResponseCode();
		inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream();
		if ("gzip".equals(connection.getContentEncoding())) {
			inputStream = new GZIPInputStream((InputStream)inputStream);
		}

		switch(responseCode) {
			case 200:
				IterableNetworkParser parser = new NetworkTextIteratorParser((InputStream)inputStream, level);
				return new NetworkIterator(connection, parser);
			case 204:
			case 404:
				if (inputStream != null) {
					((InputStream)inputStream).close();
				}

				throw new NoDataFoundException("No data found for: " + urlString);
			case 400:
				throw new CriteriaException("Bad request parameter: " + StringUtil.toString((InputStream)inputStream));
			case 429:
				try {
					((InputStream)inputStream).close();
				} catch (IOException var11) {
				}

				throw new IOException("Too Many Requests");
			case 500:
				if (logger.isLoggable(Level.WARNING)) {
					logger.severe("An error occurred while making a GET request " + urlString + StringUtil.toString((InputStream)inputStream));
				}

				throw new IOException("An error occurred while making a GET request :" + StringUtil.toString((InputStream)inputStream));
			default:
				String message = connection.getResponseMessage();
				connection.disconnect();
				throw new IOException(message);
		}
	}

	public StationIterator iterateStations(Criteria criteria, OutputLevel level) throws Exception {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria, url)", new Object[]{criteria, level});
		}

		StringBuilder paramsString = new StringBuilder((String)criteria.toUrlParams().get(0));
		if (level == OutputLevel.NETWORK) {
			paramsString.append("&level=network").toString();
		} else if (level == OutputLevel.STATION) {
			paramsString.append("&level=station").toString();
		} else if (level == OutputLevel.CHANNEL) {
			paramsString.append("&level=channel").toString();
		} else if (level == OutputLevel.RESPONSE) {
			paramsString.append("&level=response").toString();
		} else {
			paramsString.append("&level=station").toString();
		}

		return this.iterate(this.baseUrl + "query?" + paramsString);
	}

	public StationIterator iterate(InputStream inputStream) throws Exception {
		return new StationIterator(new StationXMLIteratorParser(inputStream, OutputLevel.RESPONSE));
	}

	public StationIterator iterate(String url) throws Exception {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url)", new Object[]{url});
		}

		URL u = new URL(url);
		String query = u.getQuery();
		String[] pairs = query.split("&");
		Map<String, String> queryKeyValue = new LinkedHashMap<>();


		String uAgent;
		int responseCode;
		for (String pair : pairs) {
			uAgent = pair;
			responseCode = uAgent.indexOf(61);
			queryKeyValue.put(URLDecoder.decode(uAgent.substring(0, responseCode), "UTF-8"), URLDecoder.decode(uAgent.substring(responseCode + 1), "UTF-8"));
		}

		OutputLevel level = this.extractLevel(queryKeyValue);
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		connection = this.getConnection(url);
		connection.setRequestMethod("GET");
		uAgent = this.userAgent;
		if (this.getAppName() != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		connection.setRequestProperty("User-Agent", uAgent);
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.connect();
		responseCode = connection.getResponseCode();
		inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream();
		if ("gzip".equals(connection.getContentEncoding())) {
			inputStream = new GZIPInputStream((InputStream)inputStream);
		}

		switch(responseCode) {
			case 200:
				IterableStationParser parser = this.getIterableParser((InputStream)inputStream, queryKeyValue, level);
				return new StationIterator(connection, parser);
			case 204:
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No data Found for the GET request " + url);
				}

				if (inputStream != null) {
					(inputStream).close();
				}

				throw new NoDataFoundException("No data found for: " + url);
			case 400:
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("An error occurred while making a GET request " + url + StringUtil.toString((InputStream)inputStream));
				}

				throw new CriteriaException("Bad request parameter: " + StringUtil.toString((InputStream)inputStream));
			case 404:
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No data Found for the GET request " + url + StringUtil.toString((InputStream)inputStream));
				}

				return null;
			case 429:
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Too Many Requests");
				}

				if (inputStream != null) {
					((InputStream)inputStream).close();
				}

				throw new IOException("Too Many Requests");
			case 500:
				if (logger.isLoggable(Level.WARNING)) {
					logger.severe("An error occurred while making a GET request " + url + StringUtil.toString((InputStream)inputStream));
				}

				throw new IOException(StringUtil.toString((InputStream)inputStream));
			default:
				String message = connection.getResponseMessage();
				if (connection != null) {
					connection.disconnect();
				}

				throw new IOException(message);
		}
	}

	public List<Network> load(InputStream inputStream) throws IOException {
		try {
			JAXBContext jc = JAXBContext.newInstance("edu.iris.dmc.fdsn.station.model");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setEventHandler(new DefaultValidationEventHandler());
			FDSNStationXML root = (FDSNStationXML)unmarshaller.unmarshal(inputStream);
			if (root == null) {
				throw new IOException("Failed to marshal document.");
			} else {
				return root.getNetwork();
			}
		} catch (JAXBException var5) {
			IOException ioe = new IOException(var5.getMessage());
			ioe.setStackTrace(var5.getStackTrace());
			throw ioe;
		}
	}

	public List<Network> fetch(Criteria c) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria)", new Object[]{c});
		}

		StationCriteria criteria = (StationCriteria)c;
		String theUrl = this.baseUrl + "query?" + criteria.toUrlParams().get(0);
		return this.fetch(theUrl);
	}

	public List<Network> fetch(Criteria criteria, OutputLevel level) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria, url)", new Object[]{criteria, level});
		}

		StationCriteria stationCriteria = (StationCriteria)criteria;
		stationCriteria.setLevel(level);
		return this.fetch((Criteria)stationCriteria);
	}

	public List<Network> fetch(String url) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url)", new Object[]{url});
		}
		URL u = new URL(url);
		String query = u.getQuery();
		String[] pairs = query.split("&");
		Map<String, String> queryKeyValue = new LinkedHashMap<>();

		String pair;
		for (String s : pairs) {
			pair = s;
			pair = pair.trim();
			if (pair.length() != 0) {
				int idx = pair.indexOf("=");
				queryKeyValue.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
		}
		OutputLevel level = this.extractLevel(queryKeyValue);
		StationParser parser = null;
		List<Network> result = null;
		pair = null;
		HttpURLConnection connection = this.getConnection(url);
		connection.setRequestMethod("GET");
		String uAgent = this.userAgent;
		if (this.getAppName() != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		connection.setRequestProperty("User-Agent", uAgent);
		connection.setRequestProperty("Accept", "application/xml");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.connect();
		int responseCode = connection.getResponseCode();

		try {
			InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : ("gzip".equals(connection.getContentEncoding()) ? new GZIPInputStream(connection.getInputStream()) : connection.getInputStream());
			Throwable var13 = null;

			try {
				Object var14;
				try {
					switch(responseCode) {
						case 200:
							parser = this.getParser((InputStream)inputStream, queryKeyValue, level);
							result = parser.parse();
							return result;
						case 204:
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("No data Found for the GET request " + url);
							}

							throw new NoDataFoundException("No data found for: " + url);
						case 400:
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("An error occurred while making a GET request " + url + StringUtil.toString((InputStream)inputStream));
							}

							throw new CriteriaException("Bad request parameter: " + StringUtil.toString((InputStream)inputStream));
						case 404:
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("No data Found for the GET request " + url + StringUtil.toString((InputStream)inputStream));
							}

							return Collections.emptyList();
						case 500:
							if (logger.isLoggable(Level.WARNING)) {
								logger.severe("An error occurred while making a GET request " + url + StringUtil.toString((InputStream)inputStream));
							}

							throw new IOException(StringUtil.toString((InputStream)inputStream));
						default:
							throw new IOException(connection.getResponseMessage());
					}
				} catch (Throwable var43) {
					var14 = var43;
					var13 = var43;
					throw var43;
				}
			} finally {
				if (inputStream != null) {
					if (var13 != null) {
						try {
							((InputStream)inputStream).close();
						} catch (Throwable var42) {
							var13.addSuppressed(var42);
						}
					} else {
						((InputStream)inputStream).close();
					}
				}

			}
		} finally {
			if (parser != null) {
				try {
					parser.close();
				} catch (IOException var41) {
					var41.printStackTrace();
				}
			}

			try {
				connection.disconnect();
			} catch (Exception ignored) {
			}

		}
	}

	private OutputLevel extractLevel(Map<String, String> queryKeyValue) throws CriteriaException {
		String level = (String)queryKeyValue.get("level");
		if (level == null) {
			return OutputLevel.STATION;
		} else if (!level.equalsIgnoreCase("net") && !level.equalsIgnoreCase("network")) {
			if (!level.equalsIgnoreCase("sta") && !level.equalsIgnoreCase("station")) {
				if (!level.equalsIgnoreCase("cha") && !level.equalsIgnoreCase("channel")) {
					if (!level.equalsIgnoreCase("resp") && !level.equalsIgnoreCase("response")) {
						throw new CriteriaException("Level: ['" + level + "'] is not valid");
					} else {
						return OutputLevel.RESPONSE;
					}
				} else {
					return OutputLevel.CHANNEL;
				}
			} else {
				return OutputLevel.STATION;
			}
		} else {
			return OutputLevel.NETWORK;
		}
	}

	private OutputFormat extractFormat(Map<String, String> queryKeyValue) throws CriteriaException {
		if (queryKeyValue.get("format") != null) {
			String format = (String)queryKeyValue.get("format");
			if ("text".equals(format)) {
				return OutputFormat.TEXT;
			} else if ("texttree".equalsIgnoreCase(format)) {
				return OutputFormat.TEXTTREE;
			} else if ("xml".equalsIgnoreCase(format)) {
				return OutputFormat.XML;
			} else {
				throw new CriteriaException("Format: ['" + format + "'] is not supported");
			}
		} else {
			return OutputFormat.XML;
		}
	}
}