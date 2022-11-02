package edu.iris.dmc.service;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.ws.util.StringUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class BaseService {
	private Logger logger = Logger.getLogger("edu.iris.dmc.service.BaseService");
	public static int DEFAULT_READ_TIMEOUT_IN_MS = 180000;
	protected String appName = "";
	protected String baseUrl;
	protected String version;
	protected String serviceVersion;
	protected String userAgent;
	protected String username;
	protected String password;

	public BaseService(String version, String v, String userAgent) {
		this.version = version;
		this.serviceVersion = v;
		this.userAgent = userAgent + "/" + this.version;
	}

	protected HttpURLConnection getConnection(String url) throws IOException, ServiceNotSupportedException {
		return this.getConnection(url, (String)null, (String)null);
	}

	protected HttpURLConnection getConnection(String url, String username, String password) throws IOException, ServiceNotSupportedException {
		String uAgent = this.userAgent;
		if (this.appName != null && !"".equals(this.appName)) {
			uAgent = uAgent + " (" + this.appName + ")";
		}

		return url.startsWith("https") ? this.buildSSLConn(url, uAgent, username, password) : this.buildConn(url, uAgent, username, password);
	}

	private HttpURLConnection buildSSLConn(String url, String uAgent, final String username, final String password) throws IOException {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init((KeyManager[])null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
			if (username != null || password != null) {
				url = url.replace("query", "queryauth");
				Authenticator.setDefault(new Authenticator() {
					private int attempts = 0;

					protected PasswordAuthentication getPasswordAuthentication() {
						if (this.attempts > 1) {
							return null;
						} else {
							++this.attempts;
							return new PasswordAuthentication(username, password.toCharArray());
						}
					}
				});
			}

			URL console = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection)console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", uAgent);
			conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			return conn;
		} catch (KeyManagementException var8) {
			throw new IOException(var8);
		} catch (MalformedURLException var9) {
			throw new IOException(var9);
		} catch (NoSuchAlgorithmException var10) {
			throw new IOException(var10);
		}
	}

	private HttpURLConnection buildConn(String url, String uAgent, final String username, final String password) throws IOException {
		if (username != null || password != null) {
			url = url.replace("query", "queryauth");
			Authenticator.setDefault(new Authenticator() {
				private int attempts = 0;

				protected PasswordAuthentication getPasswordAuthentication() {
					if (this.attempts > 1) {
						return null;
					} else {
						++this.attempts;
						return new PasswordAuthentication(username, password.toCharArray());
					}
				}
			});
		}

		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setUseCaches(false);
		conn.setRequestProperty("User-Agent", uAgent);
		conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
		return conn;
	}

	public void stream(OutputStream out, Criteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, UnauthorizedAccessException {
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)", new Object[]{criteria});
		}

		List<String> l = criteria.toUrlParams();
		if (l == null) {
			throw new CriteriaException("Invalid queries...");
		} else {
			String theQuery = "query?" + (String)l.get(0);
			HttpURLConnection connection = null;
			Object inputStream = null;

			try {
				connection = this.getConnection(this.baseUrl + theQuery);
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
						BufferedInputStream data = new BufferedInputStream((InputStream)inputStream);
						BufferedOutputStream bout = new BufferedOutputStream(out);

						while(true) {
							int datum = data.read();
							if (datum == -1) {
								bout.flush();
								if (this.logger.isLoggable(Level.INFO)) {
									this.logger.info("Finished writing result to outputstream.");
								}

								if (this.logger.isLoggable(Level.FINER)) {
									this.logger.exiting(this.getClass().getName(), "find(OutputStream out, Criteria criteria)");
								}

								return;
							}

							bout.write(datum);
						}
					case 204:
						if (this.logger.isLoggable(Level.WARNING)) {
							this.logger.warning("No data Found for the GET request " + theQuery);
						}

						throw new NoDataFoundException("No data found for: " + theQuery);
					case 400:
						if (this.logger.isLoggable(Level.SEVERE)) {
							this.logger.severe("An error occurred while making a GET request " + theQuery + StringUtil.toString((InputStream)inputStream));
						}

						throw new CriteriaException("Bad request parameter: " + StringUtil.toString((InputStream)inputStream));
					case 404:
						if (this.logger.isLoggable(Level.WARNING)) {
							this.logger.warning("No data Found for the GET request " + theQuery + StringUtil.toString((InputStream)inputStream));
						}

						return;
					case 500:
						if (this.logger.isLoggable(Level.WARNING)) {
							this.logger.severe("An error occurred while making a GET request " + theQuery + StringUtil.toString((InputStream)inputStream));
						}

						throw new IOException(StringUtil.toString((InputStream)inputStream));
					default:
						throw new IOException(connection.getResponseMessage());
				}
			} finally {
				if (inputStream != null) {
					try {
						((InputStream)inputStream).close();
					} catch (IOException var22) {
						var22.printStackTrace();
					}
				}

				if (connection != null) {
					connection.disconnect();
				}

				if (out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException var21) {
						var21.printStackTrace();
					}
				}

			}
		}
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

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
