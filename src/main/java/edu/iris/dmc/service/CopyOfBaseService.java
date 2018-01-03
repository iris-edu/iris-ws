package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import sun.net.www.protocol.http.Handler;
import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.ws.util.StringUtil;

public class CopyOfBaseService {

	private Logger logger = Logger
			.getLogger("edu.iris.dmc.service.BaseService");

	public static int DEFAULT_READ_TIMEOUT_IN_MS = 180000;

	protected String appName = "";
	protected String baseUrl;

	protected String version;
	protected String serviceVersion;
	protected String userAgent;
	// protected boolean authenticate = false;

	protected String username;
	protected String password;

	public CopyOfBaseService(String version, String v, String userAgent) {
		this.version = version;
		this.serviceVersion = v;
		this.userAgent = userAgent + "/" + this.version;

	}

	protected void validateVersion() throws IOException,
			ServiceNotSupportedException {
		validateVersion(this.baseUrl);
	}

	protected void validateVersion(String url) throws IOException,
			ServiceNotSupportedException {
		doVersionCheck(url);
	}

	private void doVersionCheck(String validFdsnUrl) throws IOException,
			ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"doVersionCheck(String validFdsnUrl)",
					new Object[] { validFdsnUrl });
		}

		URL url = new URL(validFdsnUrl);
		String path = url.getPath();
		if (!path.endsWith("version")) {
			if (path.endsWith("query")) {
				path = path.replace("query", "version");
			} else {
				path = path + "version";
			}
		}
		URI versionURL = null;
		try {
			versionURL = StringUtil.createURI(url.getProtocol(), url.getHost(),
					url.getPort(), path, null, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		Handler handler = new sun.net.www.protocol.http.Handler();
		URL theUrl = new URL(versionURL.toURL(), versionURL.toString(), handler);

		if (logger.isLoggable(Level.FINER)) {
			logger.log(Level.FINER, "URL to validate: " + theUrl.toString());
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) theUrl.openConnection();
			String uAgent = this.userAgent;
			if (this.appName != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}
			connection.setRequestProperty("User-Agent", uAgent);

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "text/plain");
			connection.connect();
			int responseCode = connection.getResponseCode();
			InputStream inputStream = responseCode != HTTP_OK ? connection
					.getErrorStream() : connection.getInputStream();

			if (responseCode != 200) {
				throw new IOException(connection.getResponseMessage());
			}

			String versionText = StringUtil.toString(inputStream);

			String[] vComponents = versionText.split("\\.");

			String[] lvComponents = this.serviceVersion.split("\\.");

			if (!vComponents[0].equals(lvComponents[0])) {
				throw new ServiceNotSupportedException(
						"Version is not supported.");
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private HttpURLConnection createConnection(URL url, final String username,
			final String password) throws IOException {
		/*
		 * String host = url.getHost(); int port = url.getPort(); String path =
		 * url.getPath(); if (!this.authenticate && path != null &&
		 * path.contains("query")) { path = path.replace("query", ""); } else {
		 * path = path.replace("queryauth", ""); } String baseUrl = "http://" +
		 * host;
		 * 
		 * if (port > -1 && port != 80) { baseUrl = baseUrl + ":" + port; }
		 * baseUrl = baseUrl + path;
		 */
		String uAgent = this.userAgent;
		if (this.appName != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
		if (username != null || password != null) {
			sun.net.www.protocol.http.AuthCacheValue
					.setAuthCache(new sun.net.www.protocol.http.AuthCacheImpl());
			Authenticator.setDefault(new Authenticator() {
				private boolean failedOnce = false;

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO:FIX This
					if (failedOnce) {
						return null;
					}
					failedOnce = true;
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});
		}

		// CookieHandler.setDefault( new CookieManager( null,
		// CookiePolicy.ACCEPT_ALL ) );
		HttpURLConnection connection = null;
		Handler handler = new sun.net.www.protocol.http.Handler();
		url = new URL(url, url.toString(), handler);

		connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);

		connection.setRequestProperty("User-Agent", uAgent);
		connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
		return connection;
	}

	protected HttpURLConnection getConnection(String url, String username,
			String password) throws IOException, ServiceNotSupportedException {
		if (username != null || password != null) {
			url = url.replace("query", "queryauth");
		}
		URL u = new URL(url);
		return createConnection(u, username, password);
	}

	protected HttpURLConnection getConnection(String url) throws IOException,
			ServiceNotSupportedException {
		URL u = new URL(url);
		return createConnection(u, null, null);
	}

	/*
	 * protected void authenticate(boolean authenticate) { this.authenticate =
	 * authenticate; }
	 */

	/**
	 * Stream result into output stream, can be used to save result to a local
	 * file.
	 * 
	 * @param out
	 * @param criteria
	 * @throws NoDataFoundException
	 *             (404)
	 * @throws CriteriaException
	 *             (400)
	 * @throws IOException
	 * @throws ServiceNotSupportedException
	 * @throws UnauthorizedAccessException
	 */
	public void stream(OutputStream out, Criteria criteria)
			throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException, UnauthorizedAccessException {

		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"find(OutputStream out, Criteria criteria)",
					new Object[] { criteria });
		}

		List<String> l = criteria.toUrlParams();
		if (l == null) {
			throw new CriteriaException("Invalid queries...");
		}
		String theQuery = "query?" + l.get(0);

		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			connection = getConnection(this.baseUrl + theQuery);
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
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream()
					: connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding())) {
				inputStream = new GZIPInputStream(inputStream);
			}

			switch (responseCode) {
			case 404:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request "
							+ theQuery + StringUtil.toString(inputStream));
				return;
			case 204:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request "
							+ theQuery);
				throw new NoDataFoundException("No data found for: " + theQuery);
			case 400:
				if (logger.isLoggable(SEVERE))
					logger.severe("An error occurred while making a GET request "
							+ theQuery + StringUtil.toString(inputStream));
				throw new CriteriaException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 500:
				if (logger.isLoggable(WARNING))
					logger.severe("An error occurred while making a GET request "
							+ theQuery + StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case 200:
				BufferedInputStream data = new BufferedInputStream(inputStream);

				BufferedOutputStream bout = new BufferedOutputStream(out);

				while (true) {
					int datum = data.read();
					if (datum == -1)
						break;
					bout.write(datum);
				}
				bout.flush();

				if (logger.isLoggable(Level.INFO)) {
					logger.info("Finished writing result to outputstream.");
				}

				if (logger.isLoggable(Level.FINER)) {
					// Use the following if the method does not return a value
					logger.exiting(this.getClass().getName(),
							"find(OutputStream out, Criteria criteria)");
				}
				break;
			default:
				throw new IOException(connection.getResponseMessage());
			}

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Provide an appName included in the User-Agent. This text string is
	 * intended to uniquely identify an application. Preferred string format:
	 * "APPNAME/VERSION"
	 * 
	 * @param appName
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 
	 * @return the App name used for User-Agent
	 */
	public String getAppName() {
		return this.appName;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}
}
