package edu.iris.dmc.event.model;

import javax.xml.bind.JAXBElement;


/**
 * This class represents a magnitude as described in the QuakeMl schema
 *
 */

public class Magnitude {

	private edu.iris.quake.model.Magnitude baseMagnitude;
	private String publicId;

	private String originId;

	private String type;
	private Double value;
	private String author;
	private String originPublicId;


	public Magnitude() {
	}

	public Magnitude(edu.iris.quake.model.Magnitude baseMagnitude) {
		this.baseMagnitude = baseMagnitude;
		this.publicId = baseMagnitude.getPublicID();

		for (JAXBElement element : baseMagnitude
				.getCommentOrStationMagnitudeContributionOrMag()) {
			if ("mag".equalsIgnoreCase(element.getName().getLocalPart())) {
				edu.iris.quake.model.RealQuantity rq = (edu.iris.quake.model.RealQuantity) element.getValue();

				for (JAXBElement e : rq
						.getValueOrUncertaintyOrLowerUncertainty()) {
					if ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.value = (Double) e.getValue();
					}
				}
			}

			if ("originid".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.originPublicId = (String) element.getValue();
			}
			
			if ("type".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.type = (String) element.getValue();
			}

			if ("originid".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.originId = (String) element.getValue();
			}

			if ("creationinfo".equalsIgnoreCase(element.getName()
					.getLocalPart())) {
				edu.iris.quake.model.CreationInfo ci = (edu.iris.quake.model.CreationInfo) element.getValue();
				for (JAXBElement e : ci.getAgencyIDOrAgencyURIOrAuthor()) {
					if ("author".equalsIgnoreCase(e.getName().getLocalPart())) {
						this.author = (String) e.getValue();
					}
				}
			}

		}
	}

	public String getPublicId() {
		return publicId;
	}
	
	public String getOriginPublicId() {
		return originPublicId;
	}

	/**
	 * 
	 * @return the type, null if does not exist
	 */
	public String getType() {
		return this.type;
	}

	public Double getValue() {
		return this.value;
	}

	/**
	 * 
	 * @return the author, null if does not exist
	 */
	public String getAuthor() {
		return author;
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
		Magnitude other = (Magnitude) obj;
		if (publicId == null) {
			if (other.publicId != null)
				return false;
		} else if (!publicId.equals(other.publicId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Magnitude [publicId=" + publicId + ", originId=" + originId
				+ ", type=" + type + ", value=" + value + ", author=" + author
				+ "]";
	}



}
