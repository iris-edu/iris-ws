package edu.iris.dmc.event.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import edu.iris.quake.model.Phase;

public class Arrival {

	private edu.iris.quake.model.Arrival baseArrival;

	private List<String> pickIds = new ArrayList<String>();
	private String phase;

	private Double azimuth;
	private Double distance;
	private Double timeResidual;

	public Arrival() {
	}

	public Arrival(edu.iris.quake.model.Arrival arrival) {
		this.baseArrival = arrival;

		for (JAXBElement element : baseArrival.getCommentOrPickIDOrPhase()) {
			if ("phase".equalsIgnoreCase(element.getName().getLocalPart())) {
				Phase bp = (Phase) element.getValue();
				this.phase = bp.getValue();

				/*
				 * for (JAXBElement e : rq
				 * .getValueOrUncertaintyOrLowerUncertainty()) { if
				 * ("value".equalsIgnoreCase(e.getName().getLocalPart())) {
				 * this.phase = (Double) e.getValue(); } }
				 */
			}
			if ("azimuth".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.azimuth = (Double) element.getValue();
			}
			if ("distance".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.distance = (Double) element.getValue();
			}
			if ("timeResidual".equalsIgnoreCase(element.getName()
					.getLocalPart())) {
				this.timeResidual = (Double) element.getValue();

			}

			if ("pickID".equalsIgnoreCase(element.getName().getLocalPart())) {
				this.pickIds.add((String) element.getValue());
			}

		}
		// baseArrival.commentOrPickIDOrPhase
	}

	public String getPublicId() {
		if (this.baseArrival == null) {
			return null;
		}
		return this.baseArrival.getPublicID();
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getTimeResidual() {
		return timeResidual;
	}

	public void setTimeResidual(Double timeResidual) {
		this.timeResidual = timeResidual;
	}

	public List<Pick> getPicks() {
		this.baseArrival.getCommentOrPickIDOrPhase();

		List<Pick> list = new ArrayList<Pick>();
		for (JAXBElement e : this.baseArrival.getCommentOrPickIDOrPhase()) {
			if (e.getValue() instanceof edu.iris.quake.model.Pick) {
				list.add(new Pick((edu.iris.quake.model.Pick) e.getValue()));
			}
		}

		return list;
	}
}
