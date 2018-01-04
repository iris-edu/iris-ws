package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputFormat;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.service.station.parser.IterableNetworkParser;
import edu.iris.dmc.service.station.parser.IterableStationParser;
import edu.iris.dmc.service.station.parser.NetworkTextIteratorParser;
import edu.iris.dmc.service.station.parser.StationParser;
import edu.iris.dmc.service.station.parser.StationTextIteratorParser;
import edu.iris.dmc.service.station.parser.StationTextParser;
import edu.iris.dmc.service.station.parser.StationXMLIteratorParser;
import edu.iris.dmc.service.station.parser.StationXMLParser;
import edu.iris.dmc.ws.util.StringUtil;

/**
 * Station service is the main class for querying the Station service
 * 
 */
public class StationService extends BaseService {

	private static final Logger logger = Logger.getLogger(StationService.class.getName());

	private FDSNStationXML root;

	public StationService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	private IterableStationParser getIterableParser(InputStream inputStream, Map<String, String> queryKeyValue,
			OutputLevel level) throws CriteriaException {
		try {
			OutputFormat outputFormat = extractFormat(queryKeyValue);
			if (outputFormat == OutputFormat.TEXT) {
				if (OutputLevel.NETWORK == level) {
					throw new CriteriaException("Invalid level/format combination. Level must be station|channel");
				}
				return new StationTextIteratorParser(inputStream, level);
			} else if (outputFormat == OutputFormat.XML) {
				if (OutputLevel.NETWORK == level) {
					throw new CriteriaException(
							"Invalid level/format combination. Level must be station|channel|response");
				}
				return new StationXMLIteratorParser(inputStream, level);
			} else {
				throw new CriteriaException("Format: ['" + outputFormat.toString() + "'] is not supported");
			}
		} catch (CriteriaException e) {
			throw e;
		}
	}

	private StationParser getParser(InputStream inputStream, Map<String, String> queryKeyValue, OutputLevel level)
			throws CriteriaException, IOException {
		StationParser parser = null;
		try {
			OutputFormat outputFormat = extractFormat(queryKeyValue);
			if (outputFormat == OutputFormat.TEXT) {
				parser = new StationTextParser(inputStream, level);
			} else if (outputFormat == OutputFormat.XML) {
				parser = new StationXMLParser(inputStream, level);
			} else {
				throw new CriteriaException("Format: ['" + outputFormat.toString() + "'] is not supported");
			}
		} catch (CriteriaException e) {
			throw e;
		}
		return parser;
	}

	/**
	 * Iterate over list of stations based on the inputstream
	 * 
	 * @param is
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws JAXBException
	 */
	/*
	 * public StationIterator iterate(InputStream is) throws
	 * FileNotFoundException, XMLStreamException, JAXBException { return new
	 * StationIterator(new StationXMLParser(is, null)); }
	 */

	public NetworkIterator iterateNetworks(Criteria criteria, OutputLevel level)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		

		((StationCriteria)criteria).setFormat(OutputFormat.TEXT);
		StringBuilder paramsString = new StringBuilder(criteria.toUrlParams().get(0));
		String urlString = this.baseUrl + "query?" + paramsString.toString() + "&level=network";

		HttpURLConnection connection = null;
		InputStream inputStream = null;

		connection = getConnection(urlString);
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
		inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
		if ("gzip".equals(connection.getContentEncoding())) {
			inputStream = new GZIPInputStream(inputStream);
		}
		switch (responseCode) {
		case 404:
			if (logger.isLoggable(WARNING))
				logger.warning("No data Found for the GET request " + urlString + StringUtil.toString(inputStream));
			return null;
		case 204:
			if (logger.isLoggable(WARNING))
				logger.warning("No data Found for the GET request " + urlString);
			if (inputStream != null) {
				inputStream.close();
			}
			throw new NoDataFoundException("No data found for: " + urlString);
		case 400:
			if (logger.isLoggable(SEVERE))
				logger.severe(
						"An error occurred while making a GET request " + urlString + StringUtil.toString(inputStream));
			throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
		case 429:
			if (logger.isLoggable(SEVERE))
				logger.severe("Too Many Requests");
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			throw new IOException("Too Many Requests");
		case 500:
			if (logger.isLoggable(WARNING))
				logger.severe(
						"An error occurred while making a GET request " + urlString + StringUtil.toString(inputStream));
			throw new IOException(StringUtil.toString(inputStream));
		case 200:
			IterableNetworkParser parser = new NetworkTextIteratorParser(inputStream, level);
			return new NetworkIterator(connection, parser);
		default:
			String message = connection.getResponseMessage();
			if (connection != null) {
				connection.disconnect();
			}
			throw new IOException(message);
		}
	}

	/**
	 * Iterate over list of stations based on the criteria provided
	 * 
	 * @param criteria
	 * @param level
	 * @return
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws IOException
	 * @throws ServiceNotSupportedException
	 */
	public StationIterator iterateStations(Criteria criteria, OutputLevel level)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria, url)", new Object[] { criteria, level });
		}

		StringBuilder paramsString = new StringBuilder(criteria.toUrlParams().get(0));

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
		return this.iterate(this.baseUrl + "query?" + paramsString.toString());
	}

	public StationIterator iterate(InputStream inputStream)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		return new StationIterator(new StationXMLIteratorParser(inputStream, OutputLevel.RESPONSE));
	}

	public StationIterator iterate(String url)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url)", new Object[] { url });
		}
		URL u = new URL(url);
		String query = u.getQuery();
		String[] pairs = query.split("&");
		Map<String, String> queryKeyValue = new LinkedHashMap<String, String>();
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			queryKeyValue.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}

		OutputLevel level = extractLevel(queryKeyValue);
		HttpURLConnection connection = null;
		InputStream inputStream = null;

		connection = getConnection(url);
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
		inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
		if ("gzip".equals(connection.getContentEncoding())) {
			inputStream = new GZIPInputStream(inputStream);
		}
		switch (responseCode) {
		case 404:
			if (logger.isLoggable(WARNING))
				logger.warning("No data Found for the GET request " + url + StringUtil.toString(inputStream));
			return null;
		case 204:
			if (logger.isLoggable(WARNING))
				logger.warning("No data Found for the GET request " + url);
			if (inputStream != null) {
				inputStream.close();
			}
			throw new NoDataFoundException("No data found for: " + url);
		case 400:
			if (logger.isLoggable(SEVERE))
				logger.severe("An error occurred while making a GET request " + url + StringUtil.toString(inputStream));
			throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
		case 429:
			if (logger.isLoggable(SEVERE))
				logger.severe("Too Many Requests");
			if (inputStream != null) {
				inputStream.close();
			}
			throw new IOException("Too Many Requests");
		case 500:
			if (logger.isLoggable(WARNING))
				logger.severe("An error occurred while making a GET request " + url + StringUtil.toString(inputStream));
			throw new IOException(StringUtil.toString(inputStream));
		case 200:
			IterableStationParser parser = this.getIterableParser(inputStream, queryKeyValue, level);
			return new StationIterator(connection, parser);
		default:
			String message = connection.getResponseMessage();
			if (connection != null) {
				connection.disconnect();
			}
			throw new IOException(message);
		}

	}



	/**
	 * Load Networks from inputstream, example local file
	 * 
	 * @param inputStream
	 * @return the networks list from the file
	 * @throws IOException
	 */
	public List<Network> load(InputStream inputStream) throws IOException {

		try {
			JAXBContext jc = JAXBContext.newInstance("edu.iris.dmc.fdsn.station.model");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

			FDSNStationXML root = (FDSNStationXML) unmarshaller.unmarshal(inputStream);
			if (root == null) {
				throw new IOException("Failed to marshal document.");
			}
			return root.getNetwork();

		} catch (JAXBException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}

	}

	/**
	 * Find stations based on the criteria provided
	 * 
	 * @param c
	 * @return
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws IOException
	 * @throws ServiceNotSupportedException
	 */
	public List<Network> fetch(Criteria c)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria)", new Object[] { c });
		}

		StationCriteria criteria = (StationCriteria) c;
		StringBuilder paramsString = new StringBuilder(criteria.toUrlParams().get(0));
		String theUrl = this.baseUrl + "query?" + paramsString.toString();
		return this.fetch(theUrl);
	}

	/**
	 * Find stations based on the criteria provided at the specified output
	 * level
	 * 
	 * @param criteria
	 * @param level
	 * @return
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws IOException
	 * @throws ServiceNotSupportedException
	 */
	public List<Network> fetch(Criteria criteria, OutputLevel level)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(criteria, url)", new Object[] { criteria, level });
		}

		StationCriteria stationCriteria = (StationCriteria) criteria;
		stationCriteria.setLevel(level);
		return this.fetch(stationCriteria);

	}


	public List<Network> fetch(String url)
			throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url)", new Object[] { url });
		}

		URL u = new URL(url);
		String query = u.getQuery();
		String[] pairs = query.split("&");
		Map<String, String> queryKeyValue = new LinkedHashMap<String, String>();
		for (String pair : pairs) {
			pair = pair.trim();
			if (pair.length() == 0) {
				continue;
			}
			int idx = pair.indexOf("=");
			queryKeyValue.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}

		OutputLevel level = extractLevel(queryKeyValue);
		StationParser parser = null;

		List<Network> result = null;
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			connection = getConnection(url);
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
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding())) {
				inputStream = new GZIPInputStream(inputStream);
			}
			switch (responseCode) {
			case 404:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + url + StringUtil.toString(inputStream));
				return null;
			case 204:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + url);
				throw new NoDataFoundException("No data found for: " + url);
			case 400:
				if (logger.isLoggable(SEVERE))
					logger.severe(
							"An error occurred while making a GET request " + url + StringUtil.toString(inputStream));
				throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
			case 500:
				if (logger.isLoggable(WARNING))
					logger.severe(
							"An error occurred while making a GET request " + url + StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case 200:
				parser = this.getParser(inputStream, queryKeyValue, level);
				result = parser.parse();
				break;
			default:
				throw new IOException(connection.getResponseMessage());
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (parser != null) {
				try {
					parser.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/*
	 * public InputStream getInputStream(String url, boolean doVersionCheck)
	 * throws NoDataFoundException, CriteriaException, IOException,
	 * ServiceNotSupportedException { if (logger.isLoggable(Level.FINER)) {
	 * logger.entering(this.getClass().getName(), "fetch(String url)", new
	 * Object[] { url }); }
	 * 
	 * if (doVersionCheck) { this.validateVersion(url); }
	 * 
	 * HttpURLConnection connection = null;
	 * 
	 * connection = getConnection(url); connection.setRequestMethod("GET");
	 * 
	 * String uAgent = this.userAgent; if (this.getAppName() != null &&
	 * !"".equals(this.appName)) { uAgent = uAgent + " (" + this.appName + ")";
	 * } connection.setRequestProperty("User-Agent", uAgent);
	 * 
	 * connection.setRequestProperty("Accept", "application/xml");
	 * connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
	 * connection.connect();
	 * 
	 * int responseCode = connection.getResponseCode(); InputStream inputStream
	 * = responseCode != HTTP_OK ? connection .getErrorStream() :
	 * connection.getInputStream(); if
	 * ("gzip".equals(connection.getContentEncoding())) { inputStream = new
	 * GZIPInputStream(inputStream); } switch (responseCode) { case 404: if
	 * (logger.isLoggable(WARNING))
	 * logger.warning("No data Found for the GET request " + url +
	 * StringUtil.toString(inputStream)); return null; case 204: if
	 * (logger.isLoggable(WARNING))
	 * logger.warning("No data Found for the GET request " + url); throw new
	 * NoDataFoundException("No data found for: " + url); case 400: if
	 * (logger.isLoggable(SEVERE))
	 * logger.severe("An error occurred while making a GET request " + url +
	 * StringUtil.toString(inputStream)); throw new
	 * CriteriaException("Bad request parameter: " +
	 * StringUtil.toString(inputStream)); case 500: if
	 * (logger.isLoggable(WARNING))
	 * logger.severe("An error occurred while making a GET request " + url +
	 * StringUtil.toString(inputStream)); throw new
	 * IOException(StringUtil.toString(inputStream)); case 200: return
	 * inputStream; default: throw new
	 * IOException(connection.getResponseMessage()); } }
	 */

	private OutputLevel extractLevel(Map<String, String> queryKeyValue) throws CriteriaException {
		String level = queryKeyValue.get("level");
		if (level == null) {
			return OutputLevel.STATION;
		}
		if (level.equalsIgnoreCase("net") || level.equalsIgnoreCase("network")) {
			return OutputLevel.NETWORK;
		}
		if (level.equalsIgnoreCase("sta") || level.equalsIgnoreCase("station")) {
			return OutputLevel.STATION;
		}
		if (level.equalsIgnoreCase("cha") || level.equalsIgnoreCase("channel")) {
			return OutputLevel.CHANNEL;
		}
		if (level.equalsIgnoreCase("resp") || level.equalsIgnoreCase("response")) {
			return OutputLevel.RESPONSE;
		}

		throw new CriteriaException("Level: ['" + level + "'] is not valid");
	}

	private OutputFormat extractFormat(Map<String, String> queryKeyValue) throws CriteriaException {
		if (queryKeyValue.get("format") != null) {
			String format = queryKeyValue.get("format");
			if ("text".equals(format)) {
				return OutputFormat.TEXT;
			} else if ("texttree".equalsIgnoreCase(format)) {
				return OutputFormat.TEXTTREE;
			} else if ("xml".equalsIgnoreCase(format)) {
				return OutputFormat.XML;
			} else {
				throw new CriteriaException("Format: ['" + format + "'] is not supported");
			}
		}
		// default
		return OutputFormat.XML;
	}
}
