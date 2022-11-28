package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.*;
import edu.iris.dmc.ws.util.DateUtil;

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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

public class StationXMLParser extends AbstractStationParser {
	private XMLEventReader xmlEventFilteredReader;
	private XMLEventReader xmlEventReader;

	public StationXMLParser(InputStream inputStream, OutputLevel level) throws IOException {
		super(inputStream, level);
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

		try {
			this.xmlEventReader = xmlFactory.createXMLEventReader(this.inputStream);
			EventFilter filter = new EventFilter() {
				public boolean accept(XMLEvent event) {
					if (event.isStartElement()) {
						return true;
					} else if (event.isCharacters()) {
						String s = event.asCharacters().getData();
						if (s == null) {
							return false;
						} else {
							return !s.trim().equals("\n");
						}
					} else {
						return false;
					}
				}
			};
			this.xmlEventFilteredReader = xmlFactory.createFilteredReader(this.xmlEventReader, filter);
		} catch (XMLStreamException var5) {
			throw new IOException(var5);
		}
	}

	public List<Network> parse() throws IOException {
		assert this.xmlEventFilteredReader != null;

		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[]{ObjectFactory.class});
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			List<Network> networks = new ArrayList();
			XMLEvent e = null;
			Network network = null;
			FDSNStationXML root = null;

			while(true) {
				while(true) {
					while((e = this.xmlEventFilteredReader.peek()) != null) {
						if (e.isStartElement()) {
							StartElement se = (StartElement)e;
							if (se.getName().getLocalPart().equals("FDSNStationXML")) {
								root = new FDSNStationXML();
								se = (StartElement)this.xmlEventFilteredReader.nextEvent();

								while((e = this.xmlEventFilteredReader.peek()) != null) {
									if (e.isStartElement()) {
										se = (StartElement)e;
										if (se.getName().getLocalPart().equals("Source")) {
											e = this.xmlEventFilteredReader.nextEvent();
											e = this.xmlEventFilteredReader.peek();
											if (e != null && e.isCharacters()) {
												e = this.xmlEventFilteredReader.nextEvent();
												root.setSource(e.asCharacters().getData());
											}
										} else if (se.getName().getLocalPart().equals("Sender")) {
											e = this.xmlEventFilteredReader.nextEvent();
											e = this.xmlEventFilteredReader.peek();
											if (e != null && e.isCharacters()) {
												e = this.xmlEventFilteredReader.nextEvent();
												root.setSender(e.asCharacters().getData());
											}
										} else if (se.getName().getLocalPart().equals("Module")) {
											e = this.xmlEventFilteredReader.nextEvent();
											e = this.xmlEventFilteredReader.peek();
											if (e != null && e.isCharacters()) {
												e = this.xmlEventFilteredReader.nextEvent();
												root.setModule(e.asCharacters().getData());
											}
										} else if (se.getName().getLocalPart().equals("ModuleURI")) {
											e = this.xmlEventFilteredReader.nextEvent();
											e = this.xmlEventFilteredReader.peek();
											if (e != null && e.isCharacters()) {
												e = this.xmlEventFilteredReader.nextEvent();
												root.setModuleURI(e.asCharacters().getData());
											}
										} else {
											if (!se.getName().getLocalPart().equals("Created")) {
												break;
											}

											e = this.xmlEventFilteredReader.nextEvent();
											e = this.xmlEventFilteredReader.peek();
											if (e != null && e.isCharacters()) {
												e = this.xmlEventFilteredReader.nextEvent();

												try {
													XMLGregorianCalendar created = DatatypeFactory.newInstance().newXMLGregorianCalendar(e.asCharacters().getData());
													root.setCreated(created);
												} catch (DatatypeConfigurationException var15) {
													var15.printStackTrace();
												}
											}
										}
									} else {
										this.xmlEventFilteredReader.nextEvent();
									}
								}
							} else if (!se.getName().getLocalPart().equals("Network")) {
								if (se.getName().getLocalPart().equals("Description")) {
									this.xmlEventFilteredReader.nextEvent();
									network.setDescription(this.xmlEventFilteredReader.getElementText());
								} else {
									String tnos;
									if (se.getName().getLocalPart().equals("TotalNumberStations")) {
										this.xmlEventFilteredReader.nextEvent();
										tnos = this.xmlEventFilteredReader.getElementText();
										if (tnos != null) {
											network.setTotalNumberStations(new BigInteger(tnos));
										}
									} else if (se.getName().getLocalPart().equals("SelectedNumberStations")) {
										this.xmlEventFilteredReader.nextEvent();
										tnos = this.xmlEventFilteredReader.getElementText();
										if (tnos != null) {
											network.setSelectedNumberStations(new BigInteger(tnos));
										}
									} else if (se.getName().getLocalPart().equals("Station")) {
										Object o = unmarshaller.unmarshal(this.xmlEventReader, Station.class);
										if (o instanceof JAXBElement) {
											Object jo = ((JAXBElement)o).getValue();
											if (jo instanceof Station) {
												Station station = (Station)jo;
												network.addStation(station);
											}
										}
									} else {
										this.xmlEventFilteredReader.next();
									}
								}
							} else {
								network = new Network();
								network.setRootDocument(root);
								networks.add(network);
								Iterator attributes = se.getAttributes();

								while(attributes.hasNext()) {
									Attribute attribute = (Attribute)attributes.next();
									String attributeName = attribute.getName().getLocalPart();
									if (attributeName.equals("code")) {
										network.setCode(attribute.getValue());
									} else {
										String endString;
										Date date;
										GregorianCalendar c;
										if (attributeName.equals("startDate")) {
											endString = attribute.getValue();
											if (endString != null) {
												try {
													date = DateUtil.parseAny(endString);
													c = new GregorianCalendar();
													c.setTime(date);
													XMLGregorianCalendar cal2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
													network.setStartDate(date);
												} catch (ParseException var17) {
													var17.printStackTrace();
												} catch (DatatypeConfigurationException var18) {
													var18.printStackTrace();
												}
											}
										} else if (attributeName.equals("endDate")) {
											endString = attribute.getValue();
											if (endString != null) {
												try {
													date = DateUtil.parseAny(endString);
													c = new GregorianCalendar();
													c.setTime(date);
													network.setEndDate(date);
												} catch (ParseException var16) {
													var16.printStackTrace();
												}
											}
										} else if (attributeName.equals("restrictedStatus")) {
											network.setRestrictedStatus(RestrictedStatusType.fromValue(attribute.getValue()));
										}
									}
								}

								this.xmlEventFilteredReader.next();
							}
						} else {
							this.xmlEventFilteredReader.next();
						}
					}

					return networks;
				}
			}
		} catch (JAXBException var19) {
			throw new IOException(var19);
		} catch (XMLStreamException var20) {
			var20.printStackTrace();
			throw new IOException(var20);
		}
	}
}

