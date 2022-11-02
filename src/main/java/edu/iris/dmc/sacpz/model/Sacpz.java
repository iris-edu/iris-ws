//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.sacpz.model;

import java.util.Date;
import java.util.List;

public class Sacpz {
	private String network;
	private String station;
	private String channel;
	private String location;
	private Date created;
	private Date startTime;
	private Date endTime;
	private String description;
	private Double latitude;
	private double longitude;
	private Double elevation;
	private Double depth;
	private Double dip;
	private Double azimuth;
	private Double sampleRate;
	private String inputUnit;
	private String outputUnit;
	private String instrumentType;
	private NumberUnit instrumentGain;
	private String comment;
	private NumberUnit sensitivity;
	private Double a0;
	private double constant;
	private List<Pole> poles;
	private List<Zero> zeros;

	public Sacpz() {
	}

	public String getNetwork() {
		return this.network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getStation() {
		return this.station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return this.elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Double getDepth() {
		return this.depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public Double getInclination() {
		return this.dip;
	}

	public void setInclination(Double dip) {
		this.dip = dip;
	}

	public Double getAzimuth() {
		return this.azimuth;
	}

	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}

	public Double getSampleRate() {
		return this.sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		this.sampleRate = sampleRate;
	}

	public String getInputUnit() {
		return this.inputUnit;
	}

	public void setInputUnit(String inputUnit) {
		this.inputUnit = inputUnit;
	}

	public String getOutputUnit() {
		return this.outputUnit;
	}

	public void setOutputUnit(String outputUnit) {
		this.outputUnit = outputUnit;
	}

	public String getInstrumentType() {
		return this.instrumentType;
	}

	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}

	public NumberUnit getInstrumentGain() {
		return this.instrumentGain;
	}

	public void setInstrumentGain(NumberUnit instrumentGain) {
		this.instrumentGain = instrumentGain;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public NumberUnit getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(NumberUnit sensitivity) {
		this.sensitivity = sensitivity;
	}

	public Double getA0() {
		return this.a0;
	}

	public void setA0(Double a0) {
		this.a0 = a0;
	}

	public double getConstant() {
		return this.constant;
	}

	public void setConstant(double constant) {
		this.constant = constant;
	}

	public List<Pole> getPoles() {
		return this.poles;
	}

	public void setPoles(List<Pole> poles) {
		this.poles = poles;
	}

	public List<Zero> getZeros() {
		return this.zeros;
	}

	public void setZeros(List<Zero> zeros) {
		this.zeros = zeros;
	}
}
