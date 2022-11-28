package edu.iris.dmc.service;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.WaveformCriteria;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.iris.dmc.timeseries.model.Timeseries;
import edu.iris.dmc.timeseries.model.Util;
import edu.iris.dmc.ws.util.StringUtil;
import edu.sc.seis.seisFile.mseed.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaveformService extends BaseService {
	private final Logger logger = Logger.getLogger("edu.iris.dmc.ws.service.DataSelectService");

	public WaveformService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	public void setAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}

	private List<Timeseries> fetch(String baseUrl, String query, String username, String password) throws IOException, NoDataFoundException, CriteriaException, ServiceNotSupportedException, UnauthorizedAccessException {
		HttpURLConnection connection = null;
		List<Timeseries> collection = new ArrayList<>();

		try {
			connection = this.getConnection(baseUrl + "query", username, password);
			String uAgent = this.userAgent;
			if (this.getAppName() != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}

			connection.setRequestProperty("User-Agent", uAgent);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			connection.connect();
			OutputStream outputStream = connection.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(query);
			writer.close();
			outputStream.close();
			int responseCode = connection.getResponseCode();
			try(InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream()){
				label198:
				switch (responseCode) {
					case 200:
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
										DataHeader header = (DataHeader) dr.getControlHeader();
										String network = header.getNetworkCode();
										String station = header.getStationIdentifier();
										String location = header.getLocationIdentifier();
										String channel = header.getChannelIdentifier();
										Timestamp startTime = Util.toTime(header.getStartBtime(), header.getActivityFlags(), header.getTimeCorrection(), microseconds);
										Timeseries timeseries = new Timeseries(network, station, location, channel);
										int index = collection.indexOf(timeseries);
										if (index > -1) {
											timeseries = (Timeseries) collection.get(index);
										} else {
											if (this.logger.isLoggable(Level.FINER)) {
												this.logger.finer("Adding new Timeseries: " + timeseries);
											}

											collection.add(timeseries);
										}

										timeseries.add(startTime, dr);
									}
								}
							} catch (EOFException var37) {
								dis.close();
								break label198;
							}
						}
					case 204:
						throw new NoDataFoundException("(204) No data found for: " + query);
					case 400:
						throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
					case 401:
						throw new UnauthorizedAccessException("Unauthorized access: " + query);
					case 404:
						throw new NoDataFoundException("(404) No data found for: " + query);
					case 429:
						if (this.logger.isLoggable(Level.SEVERE)) {
							this.logger.severe("Too Many Requests");
						}

						throw new IOException("Too Many Requests");
					case 500:
						if (this.logger.isLoggable(Level.WARNING)) {
							this.logger.warning("An error occurred while making a GET request " + StringUtil.toString(inputStream));
						}

						throw new IOException(StringUtil.toString(inputStream));
					default:
						throw new IOException(connection.getResponseMessage());
				}
			}
		} catch (EOFException ignored) {
		} catch (SeedFormatException | CodecException e) {
			throw new IOException(e);
		}

		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.exiting(this.getClass().getName(), "find(OutputStream out, Criteria criteria)");
		}

		return collection;
	}

	public List<Timeseries> fetch(WaveformCriteria criteria, String username, String password) throws NoDataFoundException, IOException, CriteriaException, ServiceNotSupportedException {
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)", new Object[]{criteria});
		}

		List<Timeseries> collection = new ArrayList<>();

		try {
			List<String> paramsString = criteria.toUrlParams();
			if (paramsString == null) {
				throw new CriteriaException("No query parameters found!");
			} else {

				for (String entry : paramsString) {
					try {
						List<Timeseries> result = this.fetch(this.baseUrl, entry, username, password);
						collection.addAll(result);
					} catch (Exception e) {
						//this is for Matlab, not sure why
						if (e instanceof NoDataFoundException) {
							if (this.logger.isLoggable(Level.WARNING)) {
								this.logger.finer(e.getMessage());
							}
						} else {
							if (!"Document not found on server".equals(e.getMessage())) {
								throw new IOException(e);
							}
						}
					}
				}

				if (collection.size() > 0) {
					return collection;
				} else {
					throw new NoDataFoundException("No data found for: " + criteria.toString());
				}
			}
		} catch (UnsupportedEncodingException var10) {
			if (this.logger.isLoggable(Level.WARNING)) {
				this.logger.finer(var10.getMessage());
			}

			throw new CriteriaException(var10.getMessage());
		}
	}

	public List<Timeseries> fetch(WaveformCriteria criteria) throws NoDataFoundException, IOException, CriteriaException, ServiceNotSupportedException {
		return this.fetch(criteria, this.username, this.password);
	}

	public void stream(OutputStream out, Criteria criteria, String username, String password) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, UnauthorizedAccessException {
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)", new Object[]{criteria});
		}

		try {
			List<String> paramsString = criteria.toUrlParams();
			if (paramsString == null) {
			}

			Iterator var6 = paramsString.iterator();

			while(var6.hasNext()) {
				String query = (String)var6.next();
				this.stream(out, query, username, password);
				out.flush();
			}
		} catch (UnsupportedEncodingException var15) {
			if (this.logger.isLoggable(Level.WARNING)) {
				this.logger.finer(var15.getMessage());
			}

			throw new CriteriaException(var15.getMessage());
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException var14) {
					var14.printStackTrace();
				}
			}

		}

	}

	public void stream(OutputStream out, Criteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, UnauthorizedAccessException {
		this.stream(out, criteria, this.username, this.password);
	}

	private void stream(OutputStream out, String query, String username, String password) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, UnauthorizedAccessException {
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.entering(this.getClass().getName(), "find(OutputStream out, Criteria criteria)", new Object[]{query});
		}
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.finer("Query url: " + query);
		}
		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			connection = this.getConnection(this.baseUrl + "query", username, password);
			String uAgent = this.userAgent;
			if (this.getAppName() != null && !"".equals(this.appName)) {
				uAgent = uAgent + " (" + this.appName + ")";
			}

			connection.setRequestProperty("User-Agent", uAgent);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
			OutputStream outputStream = connection.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(query);
			writer.close();
			outputStream.close();
			int responseCode = connection.getResponseCode();
			inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream();
			switch(responseCode) {
				case 200:
					BufferedInputStream data = new BufferedInputStream(inputStream);
					BufferedOutputStream bout = new BufferedOutputStream(out);

					while(true) {
						int datum = data.read();
						if (datum == -1) {
							bout.flush();
							if (this.logger.isLoggable(Level.FINER)) {
								this.logger.finer("Finished writing result to outputstream.");
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
						this.logger.warning("No data Found for: " + query);
					}

					throw new NoDataFoundException("No data found for: " + query);
				case 400:
					throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
				case 401:
					throw new UnauthorizedAccessException("Unauthorized access: " + query);
				case 404:
					throw new NoDataFoundException("No data found for: " + query);
				case 500:
					if (this.logger.isLoggable(Level.WARNING)) {
						this.logger.warning("An error occurred while making a GET request " + StringUtil.toString(inputStream));
					}

					throw new IOException(StringUtil.toString(inputStream));
				default:
					String message = "";
					if (inputStream != null) {
						message = StringUtil.toString(inputStream);
					}

					if (this.logger.isLoggable(Level.WARNING)) {
						this.logger.warning("An error occurred while making request " + message + ". code:" + responseCode);
					}

					throw new IOException(message + " Code: " + responseCode);
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException var19) {
					var19.printStackTrace();
				}
			}

			if (connection != null) {
				connection.disconnect();
			}

		}
	}

	public List<Timeseries> load(InputStream inputStream) throws IOException {
		List<Timeseries> collection = new ArrayList();
		BufferedInputStream data = new BufferedInputStream(inputStream);
		DataInputStream dis = new DataInputStream(data);

		try {
			ArrayList list = new ArrayList();

			while(true) {
				while(true) {
					try {
						SeedRecord sr = SeedRecord.read(dis);
						if (sr instanceof DataRecord) {
							list.add((DataRecord)sr);
						}
					} catch (EOFException var18) {
						var18.printStackTrace();
						if (!list.isEmpty()) {
							Iterator var6 = list.iterator();

							while(var6.hasNext()) {
								DataRecord dr = (DataRecord)var6.next();
								byte microseconds = 0;
								Blockette[] bs = dr.getBlockettes(1001);
								if (bs.length > 0) {
									Blockette1001 b1001 = (Blockette1001)bs[0];
									microseconds = b1001.getMicrosecond();
								}

								if (dr.getBlockettes(1000).length != 0) {
									DataHeader header = (DataHeader)dr.getControlHeader();
									String network = header.getNetworkCode();
									String station = header.getStationIdentifier();
									String location = header.getLocationIdentifier();
									String channel = header.getChannelIdentifier();
									Timestamp startTime = Util.toTime(header.getStartBtime(), header.getActivityFlags(), header.getTimeCorrection(), microseconds);
									Timeseries timeseries = new Timeseries(network, station, location, channel);
									int index = collection.indexOf(timeseries);
									if (index > -1) {
										timeseries = (Timeseries)collection.get(index);
									} else {
										if (this.logger.isLoggable(Level.FINER)) {
											this.logger.finer("Adding new Timeseries: " + timeseries);
										}

										collection.add(timeseries);
									}

									timeseries.add(startTime, dr);
								}
							}
						}

						return collection;
					} catch (IOException var19) {
						var19.printStackTrace();
					}
				}
			}
		} catch (SeedFormatException | CodecException e) {
			throw new IOException(e);
		}
	}
}
