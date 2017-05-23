package edu.iris.dmc.service.response;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

import edu.iris.dmc.event.model.Event;
import edu.iris.dmc.service.ResponseHandler;

public class EventResponseHandler implements ResponseHandler<Event> {

	@Override
	public List<Event> handle(InputStream inputStream) {
		List<Event> events = new ArrayList<Event>();
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

		XMLEventReader xmlEventReader;
		try {
			xmlEventReader = xmlFactory.createXMLEventReader(inputStream);

			EventFilter filter = new EventFilter() {
				public boolean accept(XMLEvent event) {
					return event.isStartElement();
				}
			};

			JAXBContext jc = JAXBContext.newInstance(edu.iris.quake.model.ObjectFactory.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			XMLEventReader xmlEventFilteredReader = xmlFactory.createFilteredReader(xmlEventReader, filter);

			StartElement se = (StartElement) xmlEventFilteredReader.nextEvent();
			se = (StartElement) xmlEventFilteredReader.nextEvent();

			if (!"event".equals(se.getName().getLocalPart())) {
				// TODO: throw exception
			}

			while (xmlEventFilteredReader.peek() != null) {
				Object o = unmarshaller.unmarshal(xmlEventReader, edu.iris.quake.model.Event.class);
				if (o instanceof javax.xml.bind.JAXBElement) {
					Object jo = ((JAXBElement) o).getValue();
					if (jo instanceof edu.iris.quake.model.Event) {
						edu.iris.quake.model.Event e = (edu.iris.quake.model.Event) jo;
						events.add(new Event((edu.iris.quake.model.Event) e));
					}
				}
			}
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return events;
	}

	@Override
	public String getContentType() {
		return "application/xml";
	}

}
