package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Level.SEVERE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.criteria.StationCriteria;
import edu.iris.dmc.ws.util.StringUtil;

/**
 * Resp service is the main class for querying the Resp service
 * 
 */
public class RespService extends BaseService {

	private static final Logger logger = Logger.getLogger(RespService.class
			.getName());

	public RespService(String baseUrl, String version,
			String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	public String fetch(String url) throws NoDataFoundException,
			CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url)",
					new Object[] { url });
		}
		HttpURLConnection connection = null;

		int i = url.indexOf("?");
		if (i < 0) {
			throw new CriteriaException("Incomplete Url...");
		}
		String searchURL = url.substring(url.indexOf("?") + 1);
		try {
			connection = getConnection(this.baseUrl + "query?" + searchURL);
			connection.setRequestMethod("GET");

			String uAgent = this.userAgent;
			if (this.getAppName() != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}
			connection.setRequestProperty("User-Agent", uAgent);

			connection.setRequestProperty("Accept", "application/xml");
			connection.connect();

			int responseCode = connection.getResponseCode();
			InputStream inputStream = responseCode != HTTP_OK ? connection
					.getErrorStream() : connection.getInputStream();

			switch (responseCode) {
			case 404:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request "
							+ this.baseUrl + " " + searchURL
							+ StringUtil.toString(inputStream));
				throw new NoDataFoundException("No data found for: "
						+ searchURL);
			case 402:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request "
							+ this.baseUrl + " " + searchURL
							+ StringUtil.toString(inputStream));
				throw new NoDataFoundException("No data found for: "
						+ searchURL);
			case 400:
				if (logger.isLoggable(SEVERE))
					logger.severe("An error occurred while making a GET request "
							+ this.baseUrl
							+ " "
							+ searchURL
							+ StringUtil.toString(inputStream));
				throw new CriteriaException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 429:
				if (logger.isLoggable(SEVERE))
					logger.severe("Too Many Requests");
				throw new IOException("Too Many Requests");
			case 500:
				if (logger.isLoggable(WARNING))
					logger.severe("An error occurred while making a GET request "
							+ this.baseUrl
							+ " "
							+ searchURL
							+ StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case 200:
				String message = StringUtil.toString(inputStream);

				if (logger.isLoggable(Level.FINER)) {
					logger.exiting(this.getClass().getName(),
							"fetch(StationCriteria criteria, OutputLevel level)");
				}

				return message;
			default:
				throw new IOException(connection.getResponseMessage());
			}

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * Find Resp based on the criteria provided. This will download data
	 * 
	 * @param criteria
	 * @param level
	 * @return List of all stations meeting the criteria provided.
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws ServiceNotSupportedException
	 */
	public String fetch(Criteria criteria) throws NoDataFoundException,
			CriteriaException, IOException, ServiceNotSupportedException {

		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"fetch(StationCriteria criteria, OutputLevel level)",
					new Object[] { criteria });
		}
		StringBuilder paramsString = new StringBuilder(criteria.toUrlParams()
				.get(0));
		return this.fetch(this.baseUrl + "query?" + paramsString.toString());
	}

}
