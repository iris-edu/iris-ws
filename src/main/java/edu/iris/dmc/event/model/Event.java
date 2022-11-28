package edu.iris.dmc.event.model;

import edu.iris.quake.model.EventDescription;
import edu.iris.quake.model.EventDescriptionType;
import edu.iris.quake.model.EventType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents an Event, users can access events related origins and
 * magnitudes from here.
 * 
 */
public class Event {

	private String publicId;
	private String type;
	private String description;

	private Integer flinnEngdahlRegionCode;
	private String flinnEngdahlRegionName;
	private String regionName;

	private Origin preferdOrigin = null;
	private Magnitude preferdMagnitude = null;

	private String preferedOriginIdKey;
	private String preferedMagnitudeIdKey;

	private edu.iris.quake.model.Event baseEvent;

	public Event() {
		super();
	}

	public Event(edu.iris.quake.model.Event e) {
		this.baseEvent = e;
		this.publicId = e.getPublicID();

		for (JAXBElement element : e.getDescriptionOrCommentOrFocalMechanism()) {
			if ("preferredoriginid".equalsIgnoreCase(element.getName()
					.getLocalPart())) {
				this.preferedOriginIdKey = (String) element.getValue();
			}

			if ("preferredMagnitudeID".equalsIgnoreCase(element.getName()
					.getLocalPart())) {
				this.preferedMagnitudeIdKey = (String) element.getValue();
			}

			if ("type".equalsIgnoreCase(element.getName().getLocalPart())) {
				EventType eType = (EventType) element.getValue();
				if (eType != null) {
					this.type = eType.value();
				}
			}
			if ("description"
					.equalsIgnoreCase(element.getName().getLocalPart())) {
				this.description = "";
				EventDescription eventDescription = (EventDescription) element
						.getValue();

				if (eventDescription != null) {
					Map<QName, String> otherAttributes = eventDescription
							.getOtherAttributes();
					Iterator<Entry<QName, String>> it = otherAttributes
							.entrySet().iterator();

					while (it.hasNext()) {
						Entry<QName, String> entry = it.next();
						if ("FEcode".equalsIgnoreCase(entry.getKey()
								.getLocalPart())) {
							String code = entry.getValue();
							if (code != null) {
								this.flinnEngdahlRegionCode = Integer
										.parseInt(code);
							}
						}
					}

					for (Object o : eventDescription.getTextOrType()) {
						if (o instanceof EventDescriptionType) {
							EventDescriptionType edt = (EventDescriptionType) o;
							String value = edt.value();
							if (edt.equals(EventDescriptionType.FLINN_ENGDAHL_REGION)) {
								this.flinnEngdahlRegionName = value;
							} else if (edt
									.equals(EventDescriptionType.REGION_NAME)) {
								this.regionName = value;
							} else {// Do nothing now
							}
						}

					}
				}
			}
		}
	}

	public String getPublicId() {
		return this.publicId;
	}

	public String getType() {
		return type;
	}

	public Integer getFlinnEngdahlRegionCode() {
		return flinnEngdahlRegionCode;
	}

	public String getFlinnEngdahlRegionName() {
		return flinnEngdahlRegionName;
	}

	public String getRegionName() {
		return regionName;
	}

	/**
	 * Include preferred estimates only. If catalog is selected, the result
	 * returned will include the preferred origin as specified by the catalog.
	 * 
	 * @return the preferred origin, null if it does not exist
	 */
	public Origin getPreferredOrigin() {
		if (this.preferdOrigin != null) {
			return this.preferdOrigin;
		}
		for (JAXBElement element : baseEvent
				.getDescriptionOrCommentOrFocalMechanism()) {
			if (element.getValue() instanceof edu.iris.quake.model.Origin) {
				edu.iris.quake.model.Origin bo = (edu.iris.quake.model.Origin) element
						.getValue();
				if (preferedOriginIdKey.equalsIgnoreCase(bo.getPublicID())) {
					this.preferdOrigin = new Origin(
							(edu.iris.quake.model.Origin) element.getValue());
				}
			}
		}
		return this.preferdOrigin;
	}

	/**
	 * Include preferred estimates only. If catalog is selected, the result
	 * returned will include the preferred origin as specified by the catalog.
	 * 
	 * @return the preferred origin, null if it does not exist
	 */
	public Magnitude getPreferredMagnitude() {
		if (this.preferdMagnitude != null) {
			return this.preferdMagnitude;
		}
		if (this.preferedMagnitudeIdKey == null) {
			return null;
		}
		for (JAXBElement element : baseEvent
				.getDescriptionOrCommentOrFocalMechanism()) {
			if (element.getValue() instanceof edu.iris.quake.model.Magnitude) {
				edu.iris.quake.model.Magnitude bo = (edu.iris.quake.model.Magnitude) element
						.getValue();
				if (preferedMagnitudeIdKey.equalsIgnoreCase(bo.getPublicID())) {
					this.preferdMagnitude = new Magnitude(
							(edu.iris.quake.model.Magnitude) element.getValue());
				}
			}
		}
		return this.preferdMagnitude;
	}

	public List<Origin> getOrigins() {
		List<Origin> list = new ArrayList<Origin>();
		for (JAXBElement e : this.baseEvent
				.getDescriptionOrCommentOrFocalMechanism()) {
			if (e.getValue() instanceof edu.iris.quake.model.Origin) {
				list.add(new Origin((edu.iris.quake.model.Origin) e.getValue()));
			}
		}

		return list;
	}

	public List<Magnitude> getMagnitudes() {
		List<Magnitude> list = new ArrayList<Magnitude>();
		for (JAXBElement e : this.baseEvent
				.getDescriptionOrCommentOrFocalMechanism()) {
			if (e.getValue() instanceof edu.iris.quake.model.Magnitude) {
				list.add(new Magnitude((edu.iris.quake.model.Magnitude) e
						.getValue()));
			}
		}

		return list;
	}

	public List<Pick> getPicks() {
		List<Pick> list = new ArrayList<Pick>();
		for (JAXBElement e : this.baseEvent
				.getDescriptionOrCommentOrFocalMechanism()) {
			if (e.getValue() instanceof edu.iris.quake.model.Pick) {
				list.add(new Pick((edu.iris.quake.model.Pick) e.getValue()));
			}
		}

		return list;
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
		Event other = (Event) obj;
		if (publicId == null) {
			if (other.publicId != null)
				return false;
		} else if (!publicId.equals(other.publicId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [publicId=" + publicId + ", description=" + description
				+ ", preferedOriginIdKey=" + preferedOriginIdKey + "]";
	}

}
