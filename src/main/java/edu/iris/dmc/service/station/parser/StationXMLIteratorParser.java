//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.service.station.parser;

import edu.iris.dmc.criteria.OutputLevel;
import edu.iris.dmc.fdsn.station.model.*;
import edu.iris.dmc.ws.util.DateUtil;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class StationXMLIteratorParser extends XMLFilterImpl implements IterableStationParser {
	private XMLEventReader xmlEventFilteredReader;
	private XMLEventReader xmlEventReader;
	private Unmarshaller unmarshaller;
	private Network network;
	private Station station;
	private FDSNStationXML root;
	private boolean closed = false;

	public StationXMLIteratorParser(InputStream inputStream, OutputLevel level) throws Exception {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

		try {
			this.xmlEventReader = xmlFactory.createXMLEventReader(inputStream);
			EventFilter filter = new EventFilter() {
				public boolean accept(XMLEvent event) {
					if (event.isStartElement()) {
						return true;
					} else if (!event.isCharacters()) {
						return false;
					} else {
						String s = event.asCharacters().getData();
						return s == null || s.trim().equals("\n");
					}
				}
			};
			JAXBContext jc = JAXBContext.newInstance(new Class[]{ObjectFactory.class});
			this.unmarshaller = jc.createUnmarshaller();
			this.xmlEventFilteredReader = xmlFactory.createFilteredReader(this.xmlEventReader, filter);
		} catch (Exception var6) {
			throw new Exception("StationXMLIterableParser: Unable to iterate xml, printing stack trace", var6);
		}
	}

	public Station next() {
		try {
			XMLEvent e = null;

			while((e = this.xmlEventFilteredReader.peek()) != null) {
				if (!e.isStartElement()) {
					this.xmlEventFilteredReader.next();
				} else {
					StartElement se = (StartElement)e;
					if (se.getName().getLocalPart().equals("FDSNStationXML")) {
						this.root = new FDSNStationXML();
						this.xmlEventFilteredReader.nextEvent();

						while((e = this.xmlEventFilteredReader.peek()) != null) {
							if (e.isStartElement()) {
								se = (StartElement)e;
								if (se.getName().getLocalPart().equals("Source")) {
									e = this.xmlEventFilteredReader.nextEvent();
									e = this.xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = this.xmlEventFilteredReader.nextEvent();
										this.root.setSource(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("Sender")) {
									e = this.xmlEventFilteredReader.nextEvent();
									e = this.xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = this.xmlEventFilteredReader.nextEvent();
										this.root.setSender(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("Module")) {
									e = this.xmlEventFilteredReader.nextEvent();
									e = this.xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = this.xmlEventFilteredReader.nextEvent();
										this.root.setModule(e.asCharacters().getData());
									}
								} else if (se.getName().getLocalPart().equals("ModuleURI")) {
									e = this.xmlEventFilteredReader.nextEvent();
									e = this.xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = this.xmlEventFilteredReader.nextEvent();
										this.root.setModuleURI(e.asCharacters().getData());
									}
								} else {
									if (!se.getName().getLocalPart().equals("Created")) {
										break;
									}

									e = this.xmlEventFilteredReader.nextEvent();
									e = this.xmlEventFilteredReader.peek();
									if (e != null && e.isCharacters()) {
										e = this.xmlEventFilteredReader.nextEvent();
										XMLGregorianCalendar created = DatatypeFactory.newInstance().newXMLGregorianCalendar(e.asCharacters().getData());
										this.root.setCreated(created);
									}
								}
							} else {
								this.xmlEventFilteredReader.nextEvent();
							}
						}
					} else if (!se.getName().getLocalPart().equals("Network")) {
						if (se.getName().getLocalPart().equals("Description")) {
							this.xmlEventFilteredReader.nextEvent();
							this.network.setDescription(this.xmlEventFilteredReader.getElementText());
						} else {
							String tnos;
							if (se.getName().getLocalPart().equals("TotalNumberStations")) {
								this.xmlEventFilteredReader.nextEvent();
								tnos = this.xmlEventFilteredReader.getElementText();
								if (tnos != null) {
									this.network.setTotalNumberStations(new BigInteger(tnos));
								}
							} else if (se.getName().getLocalPart().equals("SelectedNumberStations")) {
								this.xmlEventFilteredReader.nextEvent();
								tnos = this.xmlEventFilteredReader.getElementText();
								if (tnos != null) {
									this.network.setSelectedNumberStations(new BigInteger(tnos));
								}
							} else if (se.getName().getLocalPart().equals("Station")) {
								Object o = this.unmarshaller.unmarshal(this.xmlEventReader, Station.class);
								if (o instanceof JAXBElement) {
									Object jo = ((JAXBElement)o).getValue();
									if (jo instanceof Station) {
										if (this.station != null) {
											Station oldStation = this.station;
											this.station = (Station)jo;
											this.network.addStation(this.station);
											return oldStation;
										}

										this.station = (Station)jo;
										this.network.addStation(this.station);
									}
								}
							} else {
								this.xmlEventFilteredReader.next();
							}
						}
					} else {
						this.network = new Network();
						this.network.setRootDocument(this.root);
						Iterator attributes = se.getAttributes();

						while(attributes.hasNext()) {
							Attribute attribute = (Attribute)attributes.next();
							String attributeName = attribute.getName().getLocalPart();
							if (attributeName.equals("code")) {
								this.network.setCode(attribute.getValue());
							} else {
								String endString;
								Date date;
								if (attributeName.equals("startDate")) {
									endString = attribute.getValue();
									if (endString != null) {
										date = DateUtil.parseAny(endString);
										this.network.setStartDate(date);
									}
								} else if (attributeName.equals("endDate")) {
									endString = attribute.getValue();
									if (endString != null) {
										date = DateUtil.parseAny(endString);
										GregorianCalendar c = new GregorianCalendar();
										c.setTime(date);
										this.network.setEndDate(date);
									}
								} else if (attributeName.equals("restrictedStatus")) {
									this.network.setRestrictedStatus(RestrictedStatusType.fromValue(attribute.getValue()));
								}
							}
						}

						this.xmlEventFilteredReader.next();
					}
				}
			}
		} catch (Exception var9) {
			throw new RuntimeException("StationXMLIterableParser: Unable to iterate lines, printing stack trace", var9);
		}

		if (this.station != null) {
			Station oldStation = this.station;
			this.station = null;
			this.network.addStation(oldStation);
			return oldStation;
		} else {
			return this.station;
		}
	}

	public boolean hasNext() {
		if (this.closed) {
			return false;
		} else {
			boolean hasNext = this.xmlEventReader.hasNext();
			if (!hasNext) {
				try {
					this.close();
				} catch (IOException var3) {
					var3.printStackTrace();
				}
			}

			return hasNext;
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() throws IOException {
		if (this.xmlEventReader != null) {
			try {
				this.xmlEventReader.close();
				this.closed = true;
			} catch (XMLStreamException var2) {
				throw new IOException(var2);
			}
		}

	}
}
