package edu.iris.dmc.sacpz.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sacpz {
	private String network;
	private String station;
	private String channel;
	private String location;
	private Date created;
	private Date start;
	private Date end;
	private String description;
	private Double latitude;
	private Double longitude;
	private Double elevation;
	private Double depth;
	private Double dip;
	private Double azimuth;
	private Double sampleRate;
	private String inputUnit;
	private String outputUnit;
	private String insttype;
	private NumberUnit instgain;
	private String comment;
	private NumberUnit sensitivity;
	private Double a0;
	private Double constant;
	private List<Pole> poles;
	private List<Zero> zeros;

}
