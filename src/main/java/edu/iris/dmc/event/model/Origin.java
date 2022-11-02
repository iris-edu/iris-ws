package edu.iris.dmc.event.model;

import edu.iris.quake.model.CreationInfo;
import edu.iris.quake.model.RealQuantity;
import edu.iris.quake.model.TimeQuantity;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents an origin as described in the QuakeMl schema
 * 
 */
public class Origin {

	private edu.iris.quake.model.Origin baseOrigin;
	private String publicId;

	private double latitude;
	private double longitude;
	private double depth;

	private String author;
	private String catalog;
	private String contributor;
	
	private String contributorOriginId;
	private String contributorEventId;
	private Date time;

	public Origin() {
	}

	public Origin(edu.iris.quake.model.Origin baseOrigin) {
		this.baseOrigin = baseOrigin;
		this.publicId = baseOrigin.getPublicID();

		for (JAXBElement element : baseOrigin
				.getCompositeTimeOrCommentOrOriginUncertainty()) {
			if ("longitude".equalsIgnoreCase(element.getName().getLocalPart())) {
				RealQuantity rq = (RealQuantity) element.getValue();

				for (JAXBElement e : rq
						.getValueOrUncertaintyOrLowerUncertainty()) {
					if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.longitude = (Double) e.getValue();
					}
				}
			}

			if ("latitude".equalsIgnoreCase(element.getName().getLocalPart())) {
				RealQuantity rq = (RealQuantity) element.getValue();

				for (JAXBElement e : rq
						.getValueOrUncertaintyOrLowerUncertainty()) {
					if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.latitude = (Double) e.getValue();
					}
				}
			}

			if ("depth".equalsIgnoreCase(element.getName().getLocalPart())) {
				RealQuantity rq = (RealQuantity) element.getValue();

				for (JAXBElement e : rq
						.getValueOrUncertaintyOrLowerUncertainty()) {
					if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.depth = (Double) e.getValue();
					}
				}
			}

			if ("time".equalsIgnoreCase(element.getName().getLocalPart())) {

				TimeQuantity tq = (TimeQuantity) element.getValue();
				for (JAXBElement e : tq
						.getValueOrUncertaintyOrLowerUncertainty()) {
					if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
						XMLGregorianCalendar t = (XMLGregorianCalendar) e
								.getValue();
						GregorianCalendar gc = t.toGregorianCalendar();
						gc.setTimeZone(TimeZone.getTimeZone("GMT"));
						this.time = gc.getTime();
					}
				}
			}

			if ("creationinfo".equalsIgnoreCase(element.getName()
					.getLocalPart())) {

				CreationInfo creationInfo = (CreationInfo) element.getValue();

				for (JAXBElement e : creationInfo
						.getAgencyIDOrAgencyURIOrAuthor()) {
					if ("author".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.author = (String) e.getValue();
					}
				}

			}
		}

		Map<QName, String> otherAttributes = this.baseOrigin
				.getOtherAttributes();
		Iterator<Entry<QName, String>> it = otherAttributes.entrySet()
				.iterator();

		while (it.hasNext()) {
			Entry<QName, String> entry = it.next();
			if ("catalog".equalsIgnoreCase(entry.getKey().getLocalPart())) {
				this.catalog=entry.getValue();
			}

			if ("contributor".equalsIgnoreCase(entry.getKey().getLocalPart())) {
				this.contributor=entry.getValue();
			}
			
			if ("contributororiginid".equalsIgnoreCase(entry.getKey().getLocalPart())) {
				this.contributorOriginId=entry.getValue();
			}
			
			if ("contributoreventid".equalsIgnoreCase(entry.getKey().getLocalPart())) {
				this.contributorEventId=entry.getValue();
			}
		}

		/*
		 * timeFixed", namespace = "http://quakeml.org/xmlns/bed/1.2", type =
		 * JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "compositeTime", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "methodID", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "epicenterFixed", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "creationInfo", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "referenceSystemID", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "evaluationStatus", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "arrival", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "time", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "type", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "comment", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "region", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "originUncertainty", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "quality", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "earthModelID", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "depthType", namespace =
		 * "http://quakeml.org/xmlns/bed/1.2", type = JAXBElement.class),
		 * 
		 * @XmlElementRef(name = "evaluationMode
		 */
	}

	public String getPublicId() {
		return publicId;
	}

	/**
	 * 
	 * @return latitude in degrees
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @return longitude in degrees
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return depth in Kilometers
	 */
	public double getDepth() {
		return depth;
	}

	public String getAuthor() {
		return author;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getContributor() {
		return contributor;
	}

	public Date getTime() {
		return time;
	}
	
	public String getContributorOriginId() {
		return contributorOriginId;
	}

	public String getContributorEventId() {
		return contributorEventId;
	}

	
	public List<Arrival> getArrivals(){
		List<Arrival> list = new ArrayList<Arrival>();
		for (JAXBElement e : this.baseOrigin.getCompositeTimeOrCommentOrOriginUncertainty()) {
			if (e.getValue() instanceof edu.iris.quake.model.Arrival) {
				list.add(new Arrival((edu.iris.quake.model.Arrival) e.getValue()));
			}
		}

		return list;
	}
	
	@Override
	public String toString() {
		return "Origin [publicId=" + publicId + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", depth=" + depth + ", time="
				+ time + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((publicId == null) ? 0 : publicId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Origin other = (Origin) obj;
		if (publicId == null) {
			if (other.publicId != null)
				return false;
		} else if (!publicId.equals(other.publicId))
			return false;
		return true;
	}

}
