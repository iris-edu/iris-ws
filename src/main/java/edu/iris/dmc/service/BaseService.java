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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.ws.util.StringUtil;
import sun.net.www.protocol.http.Handler;

public class BaseService {

	private Logger logger = Logger.getLogger("edu.iris.dmc.service.BaseService");

	public static int DEFAULT_READ_TIMEOUT_IN_MS = 180000;

	protected String appName = "";
	protected String baseUrl;

	protected String version;
	protected String serviceVersion;
	protected String userAgent;
	// protected boolean authenticate = false;

	protected String username;
	protected String password;

	public BaseService(String version, String v, String userAgent) {
		this.version = version;
		this.serviceVersion = v;
		this.userAgent = userAgent + "/" + this.version;

	}

	protected HttpURLConnection getConnection(String url) throws IOException, ServiceNotSupportedException {
		return getConnection(url, null, null);
	}

	protected HttpURLConnection getConnection(String url, String username, String password)
			throws IOException, ServiceNotSupportedException {

		String uAgent = this.userAgent;
		if (this.appName != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		if (url.startsWith("https")) {
			return buildSSLConn(url, uAgent, username, password);
		} else {
			return buildConn(url, uAgent, username, password);
		}
	}

	private HttpURLConnection buildSSLConn(String url, String uAgent, final String username, final String password)
			throws IOException {

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

			if (username != null || password != null) {
				url = url.replace("query", "queryauth");

				Authenticator.setDefault(new Authenticator() {
					private int attempts = 0;

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						if (attempts > 1) {
							return null;
						}
						attempts++;
						return new PasswordAuthentication(username, password.toCharArray());
					}
				});
			}
			URL console = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());

			conn.setUseCaches(false);

			conn.setRequestProperty("User-Agent", uAgent);
			conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

			return conn;
		} catch (KeyManagementException e) {
			throw new IOException(e);
		} catch (MalformedURLException e) {
			throw new IOException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
	}

	private HttpURLConnection buildConn(String url, String uAgent, final String username, final String password)
			throws IOException {

		if (username != null || password != null) {
			url = url.replace("query", "queryauth");

			Authenticator.setDefault(new Authenticator() {
				private int attempts = 0;

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					if (attempts > 1) {
						return null;
					}
					attempts++;
					return new PasswordAuthentication(username, password.toCharArray());
				}
			});
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setUseCaches(false);

		conn.setRequestProperty("User-Agent", uAgent);
		conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
		return conn;
	}
	/*
	 * protected void authenticate(boolean authenticate) { this.authenticate =
	 * authenticate; }
	 */

	/**
	 * Stream result into output stream, can be used to save result to a local file.
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
	public void stream(OutputStream out, Criteria criteria) throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException, UnauthorizedAccessException {

		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)",
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
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
			if ("gzip".equals(connection.getContentEncoding())) {
				inputStream = new GZIPInputStream(inputStream);
			}

			switch (responseCode) {
			case 404:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + theQuery + StringUtil.toString(inputStream));
				return;
			case 204:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + theQuery);
				throw new NoDataFoundException("No data found for: " + theQuery);
			case 400:
				if (logger.isLoggable(SEVERE))
					logger.severe("An error occurred while making a GET request " + theQuery
							+ StringUtil.toString(inputStream));
				throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
			case 500:
				if (logger.isLoggable(WARNING))
					logger.severe("An error occurred while making a GET request " + theQuery
							+ StringUtil.toString(inputStream));
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
					logger.exiting(this.getClass().getName(), "find(OutputStream out, Criteria criteria)");
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
	 * Provide an appName included in the User-Agent. This text string is intended
	 * to uniquely identify an application. Preferred string format:
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
