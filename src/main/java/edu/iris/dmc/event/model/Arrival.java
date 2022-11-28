package edu.iris.dmc.event.model;

import edu.iris.quake.model.Phase;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class Arrival {
	private edu.iris.quake.model.Arrival baseArrival;
	private List<String> pickIds = new ArrayList<>();
	private String phase;
	private Double azimuth;
	private Double distance;
	private Double timeResidual;

	public Arrival() {
	}

	public Arrival(edu.iris.quake.model.Arrival arrival) {
		this.baseArrival = arrival;

		for (JAXBElement<?> jaxbElement : this.baseArrival.getCommentOrPickIDOrPhase()) {
			if ("phase".equalsIgnoreCase(jaxbElement.getName().getLocalPart())) {
				Phase bp = (Phase) jaxbElement.getValue();
				this.phase = bp.getValue();
			}

			if ("azimuth".equalsIgnoreCase(jaxbElement.getName().getLocalPart())) {
				this.azimuth = (Double) jaxbElement.getValue();
			}

			if ("distance".equalsIgnoreCase(jaxbElement.getName().getLocalPart())) {
				this.distance = (Double) jaxbElement.getValue();
			}

			if ("timeResidual".equalsIgnoreCase(jaxbElement.getName().getLocalPart())) {
				this.timeResidual = (Double) jaxbElement.getValue();
			}

			if ("pickID".equalsIgnoreCase(jaxbElement.getName().getLocalPart())) {
				this.pickIds.add((String) jaxbElement.getValue());
			}
		}

	}

	public String getPublicId() {
		return this.baseArrival == null ? null : this.baseArrival.getPublicID();
	}

	public String getPhase() {
		return this.phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Double getAzimuth() {
		return this.azimuth;
	}

	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}

	public Double getDistance() {
		return this.distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getTimeResidual() {
		return this.timeResidual;
	}

	public void setTimeResidual(Double timeResidual) {
		this.timeResidual = timeResidual;
	}

	public List<Pick> getPicks() {
		this.baseArrival.getCommentOrPickIDOrPhase();
		List<Pick> list = new ArrayList<>();

		for (JAXBElement<?> jaxbElement : this.baseArrival.getCommentOrPickIDOrPhase()) {
			if (jaxbElement.getValue() instanceof edu.iris.quake.model.Pick) {
				list.add(new Pick((edu.iris.quake.model.Pick) jaxbElement.getValue()));
			}
		}

		return list;
	}
}
