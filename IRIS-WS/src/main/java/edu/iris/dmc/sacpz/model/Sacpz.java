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
	
	
	
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	public Double getDepth() {
		return depth;
	}
	public void setDepth(Double depth) {
		this.depth = depth;
	}
	
	
	public Double getInclination() {
		return dip;
	}
	public void setInclination(Double dip) {
		this.dip = dip;
	}
	public Double getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(Double azimuth) {
		this.azimuth = azimuth;
	}
	public Double getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(Double sampleRate) {
		this.sampleRate = sampleRate;
	}
	public String getInputUnit() {
		return inputUnit;
	}
	public void setInputUnit(String inputUnit) {
		this.inputUnit = inputUnit;
	}
	public String getOutputUnit() {
		return outputUnit;
	}
	public void setOutputUnit(String outputUnit) {
		this.outputUnit = outputUnit;
	}
	public String getInstrumentType() {
		return instrumentType;
	}
	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}
	public NumberUnit getInstrumentGain() {
		return instrumentGain;
	}
	public void setInstrumentGain(NumberUnit instrumentGain) {
		this.instrumentGain = instrumentGain;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public NumberUnit getSensitivity() {
		return sensitivity;
	}
	public void setSensitivity(NumberUnit sensitivity) {
		this.sensitivity = sensitivity;
	}
	public Double getA0() {
		return a0;
	}
	public void setA0(Double a0) {
		this.a0 = a0;
	}
	public double getConstant() {
		return constant;
	}
	public void setConstant(double constant) {
		this.constant = constant;
	}
	public List<Pole> getPoles() {
		return poles;
	}
	public void setPoles(List<Pole> poles) {
		this.poles = poles;
	}
	public List<Zero> getZeros() {
		return zeros;
	}
	public void setZeros(List<Zero> zeros) {
		this.zeros = zeros;
	}
	
	
	
}
