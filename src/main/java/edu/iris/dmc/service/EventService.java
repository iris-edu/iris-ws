package edu.iris.dmc.service;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.EventCriteria;
import edu.iris.dmc.event.model.Event;
import edu.iris.dmc.event.model.Message;
import edu.iris.dmc.ws.util.StringUtil;
import edu.iris.quake.model.ObjectFactory;

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

public class EventService extends BaseService {
	private static final Logger logger = Logger.getLogger(EventService.class.getName());

	public EventService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
		super(version, compatabilityVersion, userAgent);
		this.baseUrl = baseUrl;
	}

	public Event get(Long id, boolean includeMagnitudes) throws NoDataFoundException, IOException, CriteriaException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "get(Long id, boolean includeMagnitudes)", new Object[]{id, includeMagnitudes});
		}

		String url = this.baseUrl + "query?eventid=" + id.toString();
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Query url: " + url);
		}

		try {
			List<Event> events = this.fetch(url);
			if (events != null && !events.isEmpty()) {
				if (logger.isLoggable(Level.FINER)) {
					logger.exiting(this.getClass().getName(), "get(Long id, boolean includeMagnitudes)");
				}

				return (Event)events.get(0);
			} else {
				return null;
			}
		} catch (CriteriaException var6) {
			var6.printStackTrace();
			throw var6;
		} catch (ServiceNotSupportedException var7) {
			var7.printStackTrace();
			throw new IOException(var7);
		}
	}

	public List<Event> fetch(EventCriteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		return this.fetch(this.baseUrl + "query?" + (String) criteria.toUrlParams().get(0));
	}

	public List<Event> fetch(String url) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
		if (logger.isLoggable(Level.FINER)) {
			logger.entering(this.getClass().getName(), "fetch(String url, boolean doVersionCheck)", new Object[]{url});
		}

		HttpURLConnection connection = null;

		try {
			connection = this.getConnection(url);
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestMethod("GET");
			connection.connect();
			int responseCode = connection.getResponseCode();
			InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream();
			List<Event> events;
			XMLEventReader xmlEventReader;
			Unmarshaller unmarshaller;
			XMLEventReader xmlEventFilteredReader;
			switch(responseCode) {
				case 200:
					events = new ArrayList<>();
					XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
					xmlEventReader = xmlFactory.createXMLEventReader(inputStream);
					EventFilter filter = new EventFilter() {
						public boolean accept(XMLEvent event) {
							return event.isStartElement();
						}
					};
					JAXBContext jc = JAXBContext.newInstance(new Class[]{ObjectFactory.class});
					unmarshaller = jc.createUnmarshaller();
					xmlEventFilteredReader = xmlFactory.createFilteredReader(xmlEventReader, filter);
					StartElement se = (StartElement)xmlEventFilteredReader.nextEvent();
					se = (StartElement)xmlEventFilteredReader.nextEvent();
					if (!"event".equals(se.getName().getLocalPart())) {
					}
					break;
				case 400:
					String inputStreamString = (new Scanner(inputStream, "UTF-8")).useDelimiter("\\A").next();
					throw new CriteriaException("Bad request parameter: " + inputStreamString);
				case 404:
					throw new NoDataFoundException("No data found for: " + url);
				case 500:
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("An error occurred while making a GET request " + StringUtil.toString(inputStream));
					}

					throw new IOException("Bad request parameter: " + StringUtil.toString(inputStream));
				default:
					throw new IOException(connection.getResponseMessage());
			}

			while(xmlEventFilteredReader.peek() != null) {
				Object o = unmarshaller.unmarshal(xmlEventReader, edu.iris.quake.model.Event.class);
				if (o instanceof JAXBElement) {
					Object jo = ((JAXBElement)o).getValue();
					if (jo instanceof edu.iris.quake.model.Event) {
						edu.iris.quake.model.Event e = (edu.iris.quake.model.Event)jo;
						events.add(new Event(e));
					}
				}
			}

			connection.disconnect();
			if (logger.isLoggable(Level.FINER)) {
				logger.exiting(this.getClass().getName(), "fetch(EventCriteria criteria)");
			}
			return events;
		} catch (UnsupportedEncodingException | MalformedURLException | JAXBException | XMLStreamException e) {
			throw new IOException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

		}
	}

	public List<Event> load(InputStream inputStream) throws IOException {
		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[]{Message.class});
			JAXBElement je = (JAXBElement)jc.createUnmarshaller().unmarshal(inputStream);
			Message message = new Message(je.getValue());
			return message.getEvents();
		} catch (JAXBException var5) {
			IOException ioe = new IOException(var5.getMessage());
			ioe.setStackTrace(var5.getStackTrace());
			throw ioe;
		}
	}
}

