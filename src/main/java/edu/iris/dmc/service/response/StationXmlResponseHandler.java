package edu.iris.dmc.service.response;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.RestrictedStatusType;
import edu.iris.dmc.service.ResponseHandler;

public class StationXmlResponseHandler implements ResponseHandler<Network> {

	@Override
	public List<Network> handle(InputStream inputStream) throws IOException {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlEventFilteredReader = null;
		XMLEventReader xmlEventReader = null;
		try {
			xmlEventReader = xmlFactory.createXMLEventReader(inputStream);
			EventFilter filter = new EventFilter() {
				public boolean accept(XMLEvent event) {
					if (event.isStartElement()) {
						return true;
					}
					if (event.isCharacters()) {
						String s = event.asCharacters().getData();
						// System.out.println("[" + s + "]");
						if (s == null) {
							return false;
						}
						if (s.trim().equals("\n")) {
							return false;
						}
						return true;
					}
					return false;
				}
			};
			xmlEventFilteredReader = xmlFactory.createFilteredReader(xmlEventReader, filter);

			JAXBContext jc = JAXBContext.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			// StartElement se = (StartElement)
			// xmlEventFilteredReader.nextEvent();

			List<edu.iris.dmc.fdsn.station.model.Network> networks = new ArrayList<edu.iris.dmc.fdsn.station.model.Network>();
			XMLEvent e = null;
			Network network = null;
			FDSNStationXML root = null;
			while ((e = xmlEventFilteredReader.peek()) != null) {
				if (e.isStartElement()) {
					StartElement se = (StartElement) e;
					if (se.getName().getLocalPart().equals("FDSNStationXML")) {
						root = new FDSNStationXML();
						// we only peeked, get the actual one
						se = (StartElement) xmlEventFilteredReader.nextEvent();
						// Maybe parse attributes...
						while ((e = xmlEventFilteredReader.peek()) != null) {
							if (e.isStartElement()) {
								se = (StartElement) e;
								if (se.getName().getLocalPart().equals("Source")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setSource(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("Sender")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setSender(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("Module")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setModule(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("ModuleURI")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setModuleURI(e.asCharacters().getData());
									}

								} else if (se.getName().getLocalPart().equals("Created")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										XMLGregorianCalendar created;
										try {
											created = DatatypeFactory.newInstance()
													.newXMLGregorianCalendar(e.asCharacters().getData());
											root.setCreated(created);
										} catch (DatatypeConfigurationException e1) {
											// TODO Auto-generated catch
											// block
											e1.printStackTrace();
										}

									}
								} else {
									break;
								}
								// xmlEventFilteredReader.nextEvent();
							} else {
								xmlEventFilteredReader.nextEvent();
							}
						} // END WHILE FOR ROOT
					} else if (se.getName().getLocalPart().equals("Network")) {

						network = new Network();
						network.setRootDocument(root);
						networks.add(network);
						Iterator<Attribute> attributes = se.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							String attributeName = attribute.getName().getLocalPart();
							if (attributeName.equals("code")) {
								network.setCode(attribute.getValue());
							} else if (attributeName.equals("startDate")) {
								String startString = attribute.getValue();
								if (startString != null) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
									try {
										Date date = sdf.parse(startString);
										GregorianCalendar c = new GregorianCalendar();
										c.setTime(date);
										XMLGregorianCalendar cal2 = DatatypeFactory.newInstance()
												.newXMLGregorianCalendar(c);
										network.setStartDate(cal2);
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (DatatypeConfigurationException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
								}
							} else if (attributeName.equals("endDate")) {
								String endString = attribute.getValue();
								if (endString != null) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
									try {
										Date date = sdf.parse(endString);
										GregorianCalendar c = new GregorianCalendar();
										c.setTime(date);
										XMLGregorianCalendar cal2 = DatatypeFactory.newInstance()
												.newXMLGregorianCalendar(c);
										network.setEndDate(cal2);
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (DatatypeConfigurationException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
								}
							} else if (attributeName.equals("restrictedStatus")) {
								network.setRestrictedStatus(RestrictedStatusType.fromValue(attribute.getValue()));
							} else {
								// Do nothing for now
							}
						}
						xmlEventFilteredReader.next();
					} else if (se.getName().getLocalPart().equals("Description")) {
						xmlEventFilteredReader.nextEvent();
						network.setDescription(xmlEventFilteredReader.getElementText());

					} else if (se.getName().getLocalPart().equals("TotalNumberStations")) {
						xmlEventFilteredReader.nextEvent();
						String tnos = xmlEventFilteredReader.getElementText();
						if (tnos != null) {
							network.setTotalNumberStations(new BigInteger(tnos));
						}

					} else if (se.getName().getLocalPart().equals("SelectedNumberStations")) {
						xmlEventFilteredReader.nextEvent();
						String tnos = xmlEventFilteredReader.getElementText();
						if (tnos != null) {
							network.setSelectedNumberStations(new BigInteger(tnos));
						}

					} else if (se.getName().getLocalPart().equals("Station")) {
						Object o = unmarshaller.unmarshal(xmlEventReader,
								edu.iris.dmc.fdsn.station.model.Station.class);

						if (o instanceof javax.xml.bind.JAXBElement) {
							Object jo = ((JAXBElement) o).getValue();
							if (jo instanceof edu.iris.dmc.fdsn.station.model.Station) {
								edu.iris.dmc.fdsn.station.model.Station station = (edu.iris.dmc.fdsn.station.model.Station) jo;
								network.addStation(station);
							}
						}
					} else {
						xmlEventFilteredReader.next();
					}
				} else {
					xmlEventFilteredReader.next();
				}
				// xmlEventFilteredReader.next();

			}
			return networks;
		} catch (JAXBException e) {
			throw new IOException(e);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	@Override
	public String getContentType() {
		return "application/xml";
	}

}
