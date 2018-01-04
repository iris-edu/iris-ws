package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.EventCriteria;
import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.event.model.Event;
import edu.iris.dmc.event.model.Message;
import edu.iris.dmc.sacpz.model.NumberUnit;
import edu.iris.dmc.sacpz.model.Pole;
import edu.iris.dmc.sacpz.model.PolesZeros;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.sacpz.model.Zero;
import edu.iris.dmc.ws.util.StringUtil;

/**
 * Sacpz service is the main class for querying the Sacpz service
 * 
 */
public class SacpzService extends BaseService {

	private static final Logger logger = Logger.getLogger(StationService.class
			.getName());

	public SacpzService(String baseUrl, String version,
			String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	/**
	 * fetch sacpz objects list from IRIS Sacpz service
	 * 
	 * @param criteria
	 * @return list of events
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws ServiceNotSupportedException
	 */
	public List<Sacpz> fetch(SacpzCriteria criteria)
			throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException, DataFormatException {

		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"fetch(SacpzCriteria criteria)", new Object[] { criteria });
		}

		HttpURLConnection connection = null;
		String paramsString = null;
		try {
			paramsString = criteria.toUrlParams().get(0);

			connection = getConnection(this.baseUrl+"query?"+ paramsString);
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestMethod("GET");
			connection.connect();

			int responseCode = connection.getResponseCode();
			InputStream inputStream = responseCode != HTTP_OK ? connection
					.getErrorStream() : connection.getInputStream();

			switch (responseCode) {
			case 404:
				throw new NoDataFoundException("No data found for: "
						+ criteria.toString());
			case 400:
				throw new CriteriaException("Bad request parameter: "
						+ criteria);
			case 429:
				if (logger.isLoggable(SEVERE))
					logger.severe("Too Many Requests");
				throw new IOException("Too Many Requests");
			case 500:
				if (logger.isLoggable(WARNING))
					logger.warning("An error occurred while making a GET request "
							+ StringUtil.toString(inputStream));
				throw new IOException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 200:
				List<Sacpz> list = new ArrayList<Sacpz>();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));
				String line;

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-mm-dd'T'HH:mm:ss");

				while ((line = br.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}
					Sacpz sacpz = new Sacpz();
					if (line.startsWith("*")) {
						if (line.endsWith("*")) {
							continue;
						} else {
							String[] components = line.split(":");
							if (components != null && components.length == 2) {
								String temp = components[1].trim();
								sacpz.setNetwork(temp);
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setStation(temp);
								}
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setLocation(temp);
								}
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setChannel(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();

									try {
										sacpz.setCreated(sdf.parse(temp));
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();

									try {
										sacpz.setStartTime(sdf.parse(temp));
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();

									try {
										sacpz.setEndTime(sdf.parse(temp));
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setDescription(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setLatitude(Double.parseDouble(temp));
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setLongitude(Double.parseDouble(temp));
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setElevation(Double.parseDouble(temp));
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setDepth(Double.parseDouble(temp));
								}
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setInclination(Double
											.parseDouble(temp));
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setAzimuth(Double.parseDouble(temp));
								}
							}
							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setSampleRate(Double
											.parseDouble(temp));
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setInputUnit(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setOutputUnit(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setInstrumentType(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String[] theStrings = components[1].trim()
											.split(" ");
									if (theStrings.length == 2) {
										Double value = Double
												.parseDouble(theStrings[0]);
										sacpz.setInstrumentGain(new NumberUnit(
												theStrings[1], value));
									} else {
										try {
											Double value = Double
													.parseDouble(theStrings[0]);
											sacpz.setInstrumentGain(new NumberUnit(
													"", value));
										} catch (NumberFormatException e) {
										}
									}
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setComment(temp);
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									
									String[] theStrings = components[1].trim()
											.split(" ");
									if (theStrings.length == 2) {
										Double value = Double
												.parseDouble(theStrings[0]);
										sacpz.setSensitivity(new NumberUnit(
												theStrings[1], value));
									} else {
										try {
											Double value = Double
													.parseDouble(theStrings[0]);
											sacpz.setSensitivity(new NumberUnit(
													"", value));
										} catch (NumberFormatException e) {
										}
									}
								}
							}

							if ((line = br.readLine()) != null) {
								components = line.split(":");
								if (components != null
										&& components.length == 2) {
									String temp = components[1].trim();
									sacpz.setA0(Double.parseDouble(temp));
								}
							}

						}
					}

					line = br.readLine();
					if (line == null) {
						break;
					}
					line = br.readLine();
					if (line == null) {
						break;
					}

					if (!line.startsWith("ZEROS")) {
						br.close();
						throw new DataFormatException(
								"Unable to parse data: Zeros are not found where expected");
					}
					String[] components = line.split("\t");
					try {
						int count = Integer.parseInt(components[1]);
						List<Zero> zeros = new ArrayList<Zero>();
						for (int i = 0; i < count; i++) {
							line = br.readLine();
							line = line.trim();
							String[] lineComponents = line.split("\t");
							Double real = Double.parseDouble(lineComponents[0]);
							Double imaginary = Double
									.parseDouble(lineComponents[1]);
							Zero zero = new Zero(real, imaginary);
							zeros.add(zero);
						}
						sacpz.setZeros(zeros);
					} catch (NumberFormatException e) {
						br.close();
						throw new DataFormatException(
								"Unable to parse data: NumberFormatException");
					}

					line = br.readLine();
					if (line == null || !line.startsWith("POLES")) {
						br.close();
						throw new DataFormatException(
								"Unable to parse data: Poles are not found where expected");
					}

					line = line.trim();
					components = line.split("\t");
					try {
						int count = Integer.parseInt(components[1]);
						List<Pole> poles = new ArrayList<Pole>();
						for (int i = 0; i < count; i++) {
							line = br.readLine();
							line = line.trim();
							String[] lineComponents = line.split("\t");
							Double real = Double.parseDouble(lineComponents[0]);
							Double imaginary = Double
									.parseDouble(lineComponents[1]);
							Pole pole = new Pole(real, imaginary);
							poles.add(pole);
						}
						sacpz.setPoles(poles);
					} catch (NumberFormatException e) {
						br.close();
						throw new DataFormatException(
								"Unable to parse data: NumberFormatException");
					}

					line = br.readLine();
					if(line!=null && line.startsWith("CONSTANT")){
						line = line.trim();

						components = line.split("\t");
						try {
							Double constant = Double.parseDouble(components[1]);
							sacpz.setConstant(constant);
						} catch (NumberFormatException e) {

						}
					}
					list.add(sacpz);
				}

				connection.disconnect();

				if (logger.isLoggable(Level.FINER)) {
					logger.exiting(this.getClass().getName(),
							"fetch(EventCriteria criteria)");
				}

				if (!list.isEmpty()) {
					return list;
				} else {
					return null;
				}

			default:
				throw new IOException(connection.getResponseMessage());
			}

		} catch (UnsupportedEncodingException e1) {
			throw new IOException(e1.getMessage());
		} catch (MalformedURLException e) {
			throw new IOException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Load Sacpz from inputstream, example local file
	 * 
	 * @param inputStream
	 * @return list of Sacpz objects
	 * @throws IOException
	 */
	public List<Sacpz> load(InputStream inputStream) throws IOException {
		// TODO:not implemented yet
		return null;

	}

}
