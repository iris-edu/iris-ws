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
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.ws.util.StringUtil;

public class IrisServiceImp<T> implements IrisService<T> {

	private Logger logger = Logger.getLogger("edu.iris.dmc.service.BaseService");

	public static int DEFAULT_READ_TIMEOUT_IN_MS = 180000;

	protected String appName = "";
	protected String host;
	protected int port;
	protected String endPoint;

	protected String userAgent;
	// protected boolean authenticate = false;

	protected String username;
	protected String password;

	private ResponseHandler<T> responseHandler;

	public IrisServiceImp(ResponseHandler<T> responseHandler, String host, String endPoint, String userAgent) {
		this.responseHandler = responseHandler;
		this.host = host;
		this.endPoint = endPoint;
		this.userAgent = userAgent;
	}

	public ResponseHandler<T> getResponseHandler() {
		return responseHandler;
	}

	public void setResponseHandler(ResponseHandler<T> responseHandler) {
		this.responseHandler = responseHandler;
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private HttpURLConnection prepareConnection(final URL url, final String username, final String password)
			throws IOException {
		String uAgent = this.userAgent;
		if (this.appName != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("User-Agent", uAgent);
			urlConnection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

			CookieHandler.setDefault(new CookieManager());
			if (username != null || password != null) {
				//bug JDK-6626700
				sun.net.www.protocol.http.AuthCacheValue.setAuthCache(new sun.net.www.protocol.http.AuthCacheImpl());
				urlConnection.setUseCaches(false);
				urlConnection.setDefaultUseCaches(false);
				Authenticator.setDefault(new Authenticator() {
					private int attempts = 0;

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						if (attempts > 1) {
							return null;
						}
						attempts++;

						PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username,
								password.toCharArray());
						return passwordAuthentication;
					}
				});
			}

			if (urlConnection instanceof HttpsURLConnection) {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
				HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConnection;
				HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
			}

			urlConnection.setRequestMethod("GET");
			return urlConnection;
		} catch (Exception e) {
			throw new IOException("Unable to create HTTP connection", e);
		}
	}

	@Override
	public List<T> fetch(Criteria criteria) throws IOException, CriteriaException, NoDataFoundException {
		return this.fetch("http://" + this.host + "" + this.endPoint + "?" + criteria.toUrlQuery());
	}

	public List<T> fetch(String url) throws IOException, CriteriaException, NoDataFoundException {
		return this.fetch(url, null, null);
	}

	public List<T> fetch(String url, String username, String password)
			throws IOException, NoDataFoundException, CriteriaException {
		return this.fetch(url, username, password, this.responseHandler);
	}

	public List<T> fetch(String urlString, String username, String password, ResponseHandler<T> handler)
			throws IOException, CriteriaException, NoDataFoundException {
		logger.info("Processing: " + urlString);
		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			connection = prepareConnection(url, username, password);
			connection.connect();

			int responseCode = connection.getResponseCode();
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();

			switch (responseCode) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				String location = connection.getHeaderField("Location");
				//do something here
				return null;
			case HttpURLConnection.HTTP_NO_CONTENT:
				String message = StringUtil.toString(inputStream);
				if (logger.isLoggable(WARNING)) {
					logger.warning("CODE 204 " + message + ": " + url);
				}
				throw new NoDataFoundException("No data found for: " + url);
			case HttpURLConnection.HTTP_BAD_REQUEST:
				message = StringUtil.toString(inputStream);
				if (logger.isLoggable(SEVERE)) {
					logger.severe("An error occurred while making a GET request " + url + message);
				}
				throw new CriteriaException("Bad request parameter: " + message);
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				if (logger.isLoggable(WARNING))
					logger.severe(
							"An error occurred while making a GET request " + url + StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case HttpURLConnection.HTTP_OK:
				inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
				if ("gzip".equals(connection.getContentEncoding())) {
					inputStream = new GZIPInputStream(inputStream);
				}
				return handler.handle(inputStream);
			default:
				throw new IOException(connection.getResponseMessage());
			}
		} finally {
			Authenticator.setDefault(null);
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
		}
	}

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
	public void stream(String urlString, OutputStream out) throws NoDataFoundException, CriteriaException, IOException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)",
					new Object[] { urlString });
		}

		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			connection = prepareConnection(url, null, null);
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
			switch (responseCode) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				String location = connection.getHeaderField("Location");
				//do something here
				//System.out.println(location);
				return;
			case HttpURLConnection.HTTP_NOT_FOUND:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + urlString + StringUtil.toString(inputStream));
				return;
			case HttpURLConnection.HTTP_NO_CONTENT:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for the GET request " + urlString);
				throw new NoDataFoundException("No data found for: " + urlString);
			case HttpURLConnection.HTTP_BAD_REQUEST:
				if (logger.isLoggable(SEVERE))
					logger.severe("An error occurred while making a GET request " + urlString
							+ StringUtil.toString(inputStream));
				throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				if (logger.isLoggable(WARNING))
					logger.severe("An error occurred while making a GET request " + urlString
							+ StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case HttpURLConnection.HTTP_OK:
				inputStream = responseCode != HTTP_OK ? connection.getErrorStream() : connection.getInputStream();
				if ("gzip".equals(connection.getContentEncoding())) {
					inputStream = new GZIPInputStream(inputStream);
				}
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

	@Override
	public List<T> load(InputStream inputStream, ResponseHandler<T> handler) throws IOException {
		return handler.handle(inputStream);
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

}
