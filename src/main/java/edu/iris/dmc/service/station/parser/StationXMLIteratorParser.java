package edu.iris.dmc.service.station.parser;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

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

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.RestrictedStatusType;
import edu.iris.dmc.fdsn.station.model.Station;
import org.xml.sax.helpers.XMLFilterImpl;

public class StationXMLIteratorParser extends XMLFilterImpl implements
		IterableStationParser {
	private XMLEventReader xmlEventFilteredReader;
	private XMLEventReader xmlEventReader;
	private Unmarshaller unmarshaller;
	private Network network;
	private Station station;
	private FDSNStationXML root;
	private boolean closed = false;

	public StationXMLIteratorParser(InputStream inputStream, OutputLevel level) {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
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

			JAXBContext jc = JAXBContext
					.newInstance(edu.iris.dmc.fdsn.station.model.ObjectFactory.class);
			unmarshaller = jc.createUnmarshaller();

			xmlEventFilteredReader = xmlFactory.createFilteredReader(
					xmlEventReader, filter);
		} catch (XMLStreamException e) {
			System.err
					.println("StationXMLIterableParser: Unable to iterate lines, printing stack trace");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.err
					.println("StationXMLIterableParser: Unable to iterate xml, printing stack trace");
			e.printStackTrace();
		}
	}

	public Station next() {
		try {
			XMLEvent e = null;
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
								if (se.getName().getLocalPart()
										.equals("Source")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setSource(e.asCharacters()
												.getData());
									}
								} else if (se.getName().getLocalPart()
										.equals("Sender")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setSender(e.asCharacters()
												.getData());
									}
								} else if (se.getName().getLocalPart()
										.equals("Module")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setModule(e.asCharacters()
												.getData());
									}
								} else if (se.getName().getLocalPart()
										.equals("ModuleURI")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										root.setModuleURI(e.asCharacters()
												.getData());
									}

								} else if (se.getName().getLocalPart()
										.equals("Created")) {
									e = xmlEventFilteredReader.nextEvent();
									e = xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = xmlEventFilteredReader.nextEvent();
										XMLGregorianCalendar created;
										try {
											created = DatatypeFactory
													.newInstance()
													.newXMLGregorianCalendar(
															e.asCharacters()
																	.getData());
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
							} else {
								xmlEventFilteredReader.nextEvent();
							}
						}// END WHILE FOR ROOT
					} else if (se.getName().getLocalPart().equals("Network")) {
						network = new Network();
						network.setRootDocument(root);
						Iterator<Attribute> attributes = se.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							String attributeName = attribute.getName()
									.getLocalPart();
							if (attributeName.equals("code")) {
								network.setCode(attribute.getValue());
							} else if (attributeName.equals("startDate")) {
								String startString = attribute.getValue();
								if (startString != null) {
									SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd'T'HH:mm:ss");
									try {
										Date date = sdf.parse(startString);
										GregorianCalendar c = new GregorianCalendar();
										c.setTime(date);
										XMLGregorianCalendar cal2 = DatatypeFactory
												.newInstance()
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
									SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd'T'HH:mm:ss");
									try {
										Date date = sdf.parse(endString);
										GregorianCalendar c = new GregorianCalendar();
										c.setTime(date);
										XMLGregorianCalendar cal2 = DatatypeFactory
												.newInstance()
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
								network.setRestrictedStatus(RestrictedStatusType
										.fromValue(attribute.getValue()));
							} else {
								// Do nothing for now
							}
						}
						xmlEventFilteredReader.next();
					} else if (se.getName().getLocalPart()
							.equals("Description")) {
						xmlEventFilteredReader.nextEvent();
						network.setDescription(xmlEventFilteredReader
								.getElementText());

					} else if (se.getName().getLocalPart()
							.equals("TotalNumberStations")) {
						xmlEventFilteredReader.nextEvent();
						String tnos = xmlEventFilteredReader.getElementText();
						if (tnos != null) {
							network.setTotalNumberStations(new BigInteger(tnos));
						}

					} else if (se.getName().getLocalPart()
							.equals("SelectedNumberStations")) {
						xmlEventFilteredReader.nextEvent();
						String tnos = xmlEventFilteredReader.getElementText();
						if (tnos != null) {
							network.setSelectedNumberStations(new BigInteger(
									tnos));
						}

					} else if (se.getName().getLocalPart().equals("Station")) {
						Object o = unmarshaller.unmarshal(xmlEventReader,
								edu.iris.dmc.fdsn.station.model.Station.class);

						if (o instanceof javax.xml.bind.JAXBElement) {
							Object jo = ((JAXBElement) o).getValue();
							if (jo instanceof edu.iris.dmc.fdsn.station.model.Station) {
								if (station == null) {
									station = (edu.iris.dmc.fdsn.station.model.Station) jo;
									network.addStation(station);
								} else {
									Station oldStation = station;
									station = (edu.iris.dmc.fdsn.station.model.Station) jo;
									network.addStation(station);
									return oldStation;
								}
							}
						}
					} else {
						xmlEventFilteredReader.next();
					}
				} else {
					xmlEventFilteredReader.next();
				}
			}
		} catch (XMLStreamException e) {
			System.err
					.println("StationXMLIterableParser: Unable to iterate lines, printing stack trace");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			System.err
					.println("StationXMLIterableParser: Unable to iterate lines, printing stack trace");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if (station != null) {
			Station oldStation = station;
			station = null;
			network.addStation(oldStation);
			return oldStation;
		}
		return station;
	}

	//@Override
	public boolean hasNext() {
		if (closed) {
			return false;
		}
		boolean hasNext = this.xmlEventReader.hasNext();
		if (!hasNext) {
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hasNext;

	}

	//@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	//@Override
	public void close() throws IOException {
		if (this.xmlEventReader != null) {
			try {
				this.xmlEventReader.close();
				closed = true;
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

	}

}
