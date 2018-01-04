package edu.iris.dmc.service;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.EventCriteria;
import edu.iris.dmc.event.model.Event;
import edu.iris.dmc.event.model.Message;
import edu.iris.dmc.ws.util.StringUtil;

/**
 * Event service is the main class for querying the Event service
 * 
 */
public class EventService extends BaseService {

	private static final Logger logger = Logger.getLogger(EventService.class
			.getName());

	public EventService(String baseUrl, String version,
			String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	/**
	 * A convenient method to retrieve a single event when the id is provided.
	 * The id is data center specific.
	 * 
	 * @param id
	 * @param includeMagnitudes
	 * @return the Event
	 * @throws IOException
	 * @throws CriteriaException
	 */

	public Event get(Long id, boolean includeMagnitudes)
			throws NoDataFoundException, IOException, CriteriaException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"get(Long id, boolean includeMagnitudes)", new Object[] {
							id, includeMagnitudes });
		}

		String url = this.baseUrl + "query?eventid=" + id.toString();

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Query url: " + url);
		}

		List<Event> events;
		try {
			events = this.fetch(url);

			if (events == null || events.isEmpty()) {
				return null;
			}

			if (logger.isLoggable(Level.FINER)) {
				// Use the following if the method does not return a value
				logger.exiting(this.getClass().getName(),
						"get(Long id, boolean includeMagnitudes)");
			}
			return events.get(0);
		} catch (CriteriaException e) {
			e.printStackTrace();
			throw e;
		} catch (ServiceNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	
	/**
	 * fetch events from IRIS event service
	 * 
	 * @param criteria
	 * @return list of events
	 * @throws NoDataFoundException
	 * @throws CriteriaException
	 * @throws ServiceNotSupportedException
	 */
	public List<Event> fetch(EventCriteria criteria)
			throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException {
		StringBuilder paramsString = new StringBuilder(criteria.toUrlParams()
				.get(0));
		return this.fetch(this.baseUrl + "query?" + paramsString.toString());

	}

	public List<Event> fetch(String url)
			throws NoDataFoundException, CriteriaException, IOException,
			ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(),
					"fetch(String url, boolean doVersionCheck)",
					new Object[] { url });
		}

		HttpURLConnection connection = null;

		try {
			connection = getConnection(url);

			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestMethod("GET");
			connection.connect();

			int responseCode = connection.getResponseCode();
			InputStream inputStream = responseCode != HTTP_OK ? connection
					.getErrorStream() : connection.getInputStream();

			switch (responseCode) {
			case 404:
				throw new NoDataFoundException("No data found for: " + url);
			case 400:
				String inputStreamString = new Scanner(inputStream, "UTF-8")
						.useDelimiter("\\A").next();
				throw new CriteriaException("Bad request parameter: "
						+ inputStreamString);
			case 500:
				if (logger.isLoggable(WARNING))
					logger.warning("An error occurred while making a GET request "
							+ StringUtil.toString(inputStream));
				throw new IOException("Bad request parameter: "
						+ StringUtil.toString(inputStream));
			case 200:
				List<Event> events = new ArrayList<Event>();
				XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

				XMLEventReader xmlEventReader = xmlFactory
						.createXMLEventReader(inputStream);

				EventFilter filter = new EventFilter() {
					public boolean accept(XMLEvent event) {
						return event.isStartElement();
					}
				};

				JAXBContext jc = JAXBContext
						.newInstance(edu.iris.quake.model.ObjectFactory.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				XMLEventReader xmlEventFilteredReader = xmlFactory
						.createFilteredReader(xmlEventReader, filter);

				StartElement se = (StartElement) xmlEventFilteredReader
						.nextEvent();
				se = (StartElement) xmlEventFilteredReader.nextEvent();

				if (!"event".equals(se.getName().getLocalPart())) {
					// TODO: throw exception
				}

				while (xmlEventFilteredReader.peek() != null) {
					Object o = unmarshaller.unmarshal(xmlEventReader,
							edu.iris.quake.model.Event.class);
					if (o instanceof javax.xml.bind.JAXBElement) {
						Object jo = ((JAXBElement) o).getValue();
						if (jo instanceof edu.iris.quake.model.Event) {
							edu.iris.quake.model.Event e = (edu.iris.quake.model.Event) jo;
							events.add(new Event((edu.iris.quake.model.Event) e));
						}
					}
				}
				connection.disconnect();

				if (logger.isLoggable(Level.FINER)) {
					logger.exiting(this.getClass().getName(),
							"fetch(EventCriteria criteria)");
				}
				return events;
			default:
				throw new IOException(connection.getResponseMessage());
			}

		} catch (UnsupportedEncodingException e1) {
			throw new IOException(e1.getMessage());
		} catch (MalformedURLException e) {
			throw new IOException(e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}


	/**
	 * Load events from inputstream, example local file
	 * 
	 * @param inputStream
	 * @return list of events
	 * @throws IOException
	 */
	public List<Event> load(InputStream inputStream) throws IOException {

		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Message.class);

			JAXBElement je = (JAXBElement) jc.createUnmarshaller().unmarshal(
					inputStream);
			Message message = new Message(je.getValue());
			return message.getEvents();
		} catch (JAXBException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}

	}

}
