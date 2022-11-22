package edu.iris.dmc.sacpz.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class NumberUnit {
	private String unit;
	private Double value;


}
