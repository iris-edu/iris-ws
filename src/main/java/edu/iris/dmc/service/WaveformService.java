package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.net.www.protocol.http.Handler;
import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.timeseries.model.Util;
import edu.iris.dmc.ws.util.StringUtil;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.Blockette1001;
import edu.sc.seis.seisFile.mseed.Btime;
import edu.sc.seis.seisFile.mseed.ControlHeader;
import edu.sc.seis.seisFile.mseed.DataBlockette;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

/**
 * WaveFormService service is the main class for querying the Bulkdataselect
 * service
 * 
 */
public class WaveformService extends BaseService {

	private Logger logger = Logger
			.getLogger("edu.iris.dmc.ws.service.DataSelectService");

	public WaveformService(String baseUrl, String version,
			String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	public void setAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}

	private List<Timeseries> fetch(String baseUrl, String query,
			String username, String password) throws IOException,
			NoDataFoundException, CriteriaException,
			ServiceNotSupportedException, UnauthorizedAccessException {
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		List<Timeseries> collection = new ArrayList<Timeseries>();
		try {
			connection = this.getConnection(baseUrl + "query", username,
					password);
			
			String uAgent = this.userAgent;
			if (this.getAppName() != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}
			connection.setRequestProperty("User-Agent", uAgent);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			// connection.setConnectTimeout(10000);
			connection.connect();

			/*
			 * URL url = null; if (this.authenticate) { url = new URL(baseUrl +
			 * "queryauth"); } else { url = new URL(baseUrl + "query"); }
			 * connection = getConnection(url);
			 * connection.setRequestProperty("Content-Type",
			 * "application/x-www-form-urlencoded");
			 * connection.setRequestMethod("POST");
			 * connection.setDoOutput(true); connection.setDoInput(true);
			 * connection.setUseCaches(false);
			 * connection.setAllowUserInteraction(false); connection.connect();
			 */

			OutputStream outputStream = connection.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(query);
			writer.close();
			outputStream.close();

			int responseCode = connection.getResponseCode();			 
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream()
					: connection.getInputStream();
			switch (responseCode) {
			case 404:
				throw new NoDataFoundException("(404) No data found for: "
						+ query);
			case 401:
				throw new UnauthorizedAccessException("Unauthorized access: "
						+ query);
			case 204:
				throw new NoDataFoundException("(204) No data found for: "
						+ query);
			case 400:
				throw new CriteriaException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 429:
				if (logger.isLoggable(SEVERE))
					logger.severe("Too Many Requests");
				throw new IOException("Too Many Requests");
			case 500:
				if (logger.isLoggable(WARNING))
					logger.warning("An error occurred while making a GET request "
							+ StringUtil.toString(inputStream));
				throw new IOException(StringUtil.toString(inputStream));
			case 200:
				// BufferedInputStream data = new
				// BufferedInputStream(inputStream);
				DataInputStream dis = new DataInputStream(inputStream);

				while (true) {
					try {
						SeedRecord sr = SeedRecord.read(dis);
						byte microseconds = 0;
						if (sr instanceof DataRecord) {
							DataRecord dr = (DataRecord) sr;
							Blockette[] bs = dr.getBlockettes(1001);
							if (bs.length > 0) {
								Blockette1001 b1001 = (Blockette1001) bs[0];
								microseconds = b1001.getMicrosecond();
							}

							if (dr.getBlockettes(1000).length != 0) {
								// ControlHeader
								DataHeader header = (DataHeader) dr
										.getControlHeader();
								String network = header.getNetworkCode();
								String station = header.getStationIdentifier();
								String location = header
										.getLocationIdentifier();
								String channel = header.getChannelIdentifier();

								Timestamp startTime = Util.toTime(
										header.getStartBtime(),
										header.getActivityFlags(),
										header.getTimeCorrection(),
										microseconds);

								Timeseries timeseries = new Timeseries(network,
										station, location, channel);
								int index = collection.indexOf(timeseries);
								if (index > -1) {
									timeseries = collection.get(index);
								} else {
									if (logger.isLoggable(Level.FINER)) {
										logger.finer("Adding new Timeseries: "
												+ timeseries);
									}
									collection.add(timeseries);
								}
								timeseries.add(startTime, dr);
							} else {
								// :TODO throw exception
							}
						} else {

						}
					} catch (EOFException eoe) {
						break;
					}
				}
				dis.close();
				break;

			default:
				throw new IOException(connection.getResponseMessage());
			}

		} catch (EOFException eoe) {
			// DO NOTHING
		} catch (SeedFormatException e) {
			throw new IOException(e);
		} catch (UnsupportedCompressionType e) {
			throw new IOException(e);
		} catch (CodecException e) {
			throw new IOException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			// Use the following if the method does not return a value
			logger.exiting(this.getClass().getName(),
					"find(OutputStream out, Criteria criteria)");
		}
		return collection;
	}

	public List<Timeseries> fetch(WaveformCriteria criteria, String username,
			String password) throws NoDataFoundException, IOException,
			CriteriaException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"find(OutputStream out, Criteria criteria)",
					new Object[] { criteria });
		}

		List<Timeseries> collection = new ArrayList<Timeseries>();
		try {
			List<String> paramsString = criteria.toUrlParams();
			if (paramsString == null) {
				throw new CriteriaException("No query parameters found!");
			}

			for (String entry : paramsString) {
				try {
					List<Timeseries> result = fetch(this.baseUrl, entry,
							username, password);
					if (result != null) {
						collection.addAll(result);
					}
				} catch (Exception e) {
					if (e instanceof NoDataFoundException) {
						// do nothing for now
						if (logger.isLoggable(Level.WARNING)) {
							logger.finer(e.getMessage());
						}
					} else if ("Document not found on server".equals(e
							.getMessage())) {
						// do nothing for now
						if (logger.isLoggable(Level.WARNING)) {
							logger.finer(e.getMessage());
						}
					} else {
						throw new IOException(e);
					}
				}
			}

			if (collection.size() > 0) {
				return collection;
			} else {
				throw new NoDataFoundException("No data found for: "
						+ criteria.toString());
			}

		} catch (UnsupportedEncodingException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.finer(e.getMessage());
			}
			throw new CriteriaException(e.getMessage());
		}
	}

	/**
	 * Find timeseries based on the criteria provided. This will download data
	 * from IRIS bulkdataselect service (http://www.iris.edu/ws/bulkdataselect).
	 * 
	 * @param criteria
	 * @return a list of timeseries containing segments
	 * @throws NoDataFoundException
	 * @throws IOException
	 * @throws CriteriaException
	 * @throws ServiceNotSupportedException
	 */
	public List<Timeseries> fetch(WaveformCriteria criteria)
			throws NoDataFoundException, IOException, CriteriaException,
			ServiceNotSupportedException {
		return this.fetch(criteria, this.username, this.password);
	}

	public void stream(OutputStream out, Criteria criteria, String username,
			String password) throws NoDataFoundException, CriteriaException,
			IOException, ServiceNotSupportedException,
			UnauthorizedAccessException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"find(OutputStream out, Criteria criteria)",
					new Object[] { criteria });
		}

		try {
			List<String> paramsString = criteria.toUrlParams();

			if (paramsString == null) {
				// TODO:
			}

			for (String query : paramsString) {
				this.stream(out, query, username, password);
				out.flush();
			}

		} catch (UnsupportedEncodingException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.finer(e.getMessage());
			}
			throw new CriteriaException(e.getMessage());
		} finally {
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

	public void stream(OutputStream out, Criteria criteria)
			throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException, UnauthorizedAccessException {
		this.stream(out, criteria, this.username, this.password);
	}

	/*
	 * protected HttpURLConnection getConnection(URL u) throws IOException,
	 * ServiceNotSupportedException { //validateVersion(u); String uAgent =
	 * this.userAgent; if (this.appName != null && !"".equals(this.appName)) {
	 * uAgent = uAgent + " (" + this.appName + ")"; } Handler handler = new
	 * sun.net.www.protocol.http.Handler(); URL url = new URL(new URL(baseUrl +
	 * "version"), baseUrl + "version", handler);
	 * 
	 * if (authenticate) { authenticate(); }
	 * 
	 * if (this.authenticate) { url = new URL(baseUrl + "queryauth"); } else {
	 * url = new URL(baseUrl + "query"); }
	 * 
	 * HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	 * 
	 * connection.setRequestProperty("User-Agent", uAgent);
	 * connection.setRequestProperty("Content-Type",
	 * "application/x-www-form-urlencoded");
	 * connection.setRequestMethod("POST"); connection.setDoOutput(true);
	 * connection.setDoInput(true); connection.setUseCaches(false);
	 * connection.setAllowUserInteraction(false);
	 * connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
	 * //connection.setConnectTimeout(10000); connection.connect();
	 * 
	 * return connection; }
	 */
	private void stream(OutputStream out, String query, String username,
			String password) throws NoDataFoundException, CriteriaException,
			IOException, ServiceNotSupportedException,
			UnauthorizedAccessException {

		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"find(OutputStream out, Criteria criteria)",
					new Object[] { query });
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Query url: " + query);
		}

		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			connection = this.getConnection(this.baseUrl + "query", username,
					password);
			
			String uAgent = this.userAgent;
			if (this.getAppName() != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}
			connection.setRequestProperty("User-Agent", uAgent);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			// connection.setConnectTimeout(10000);
			// connection.connect();
			OutputStream outputStream = connection.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(query);
			writer.close();
			outputStream.close();

			int responseCode = connection.getResponseCode();
			inputStream = responseCode != HTTP_OK ? connection.getErrorStream()
					: connection.getInputStream();
			switch (responseCode) {
			case 204:
				if (logger.isLoggable(WARNING))
					logger.warning("No data Found for: " + query);
				throw new NoDataFoundException("No data found for: " + query);
			case 404:
				throw new NoDataFoundException("No data found for: " + query);
			case 401:
				throw new UnauthorizedAccessException("Unauthorized access: "
						+ query);
			case 400:
				throw new CriteriaException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 500:
				if (logger.isLoggable(WARNING))
					logger.warning("An error occurred while making a GET request "
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

				if (logger.isLoggable(Level.FINER)) {
					// Use the following if the method does not return a value
					logger.finer("Finished writing result to outputstream.");
				}

				if (logger.isLoggable(Level.FINER)) {
					// Use the following if the method does not return a value
					logger.exiting(this.getClass().getName(),
							"find(OutputStream out, Criteria criteria)");
				}
				break;
			default:
				String message = "";
				if (inputStream != null) {
					message = StringUtil.toString(inputStream);
				}
				if (logger.isLoggable(WARNING))
					logger.warning("An error occurred while making request "
							+ message + ". code:" + responseCode);
				throw new IOException(message + " Code: " + responseCode);
			}

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}

		}

	}

	/**
	 * Load timeseries from inputStream like local file
	 * 
	 * @param inputStream
	 * @return a list of timeseries containing segments
	 * @throws IOException
	 */
	public List<Timeseries> load(InputStream inputStream) throws IOException {

		List<Timeseries> collection = new ArrayList<Timeseries>();
		BufferedInputStream data = new BufferedInputStream(inputStream);
		DataInput dis = new DataInputStream(data);

		try {

			List<DataRecord> list = new ArrayList<DataRecord>();
			while (true) {
				try {
					SeedRecord sr = SeedRecord.read(dis);
					if (sr instanceof DataRecord) {
						list.add((DataRecord) sr);
					}
				} catch (EOFException eoe) {
					eoe.printStackTrace();
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (!list.isEmpty()) {
				for (DataRecord dr : list) {
					byte microseconds = 0;
					Blockette[] bs = dr.getBlockettes(1001);
					if (bs.length > 0) {
						Blockette1001 b1001 = (Blockette1001) bs[0];
						microseconds = b1001.getMicrosecond();
					}
					if (dr.getBlockettes(1000).length != 0) {
						DataHeader header = (DataHeader) dr.getControlHeader();

						String network = header.getNetworkCode();
						String station = header.getStationIdentifier();
						String location = header.getLocationIdentifier();
						String channel = header.getChannelIdentifier();

						Timestamp startTime = Util.toTime(
								header.getStartBtime(),
								header.getActivityFlags(),
								header.getTimeCorrection(), microseconds);

						Timeseries timeseries = new Timeseries(network,
								station, location, channel);
						int index = collection.indexOf(timeseries);
						if (index > -1) {
							timeseries = collection.get(index);
						} else {
							if (logger.isLoggable(Level.FINER)) {
								logger.finer("Adding new Timeseries: "
										+ timeseries);
							}
							collection.add(timeseries);
						}

						timeseries.add(startTime, dr);
					} else {
						// :TODO throw exception
					}
				}
			}

		} catch (SeedFormatException e) {
			throw new IOException(e);
		} catch (UnsupportedCompressionType e) {
			throw new IOException(e);
		} catch (CodecException e) {
			throw new IOException(e);
		}

		return collection;
	}
}
