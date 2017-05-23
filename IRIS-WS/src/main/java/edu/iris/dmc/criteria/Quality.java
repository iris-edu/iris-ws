package edu.iris.dmc.criteria;

/**
 * The D, R, Q, M are defined in the seed format as written below.  the E and B Qualities are supported by DMC request mechanisms.
 *
 */
public enum Quality {
	/**
	 * Indicates everything/all quality
	 */
	E, 
	/**
	 * Data center modified, time-series values have not been changed.
	 */
	M, 
	/**
	 * Best
	 */
	B, 
	/**
	 * Quality Controlled Data, some processes have been applied to the data.
	 */
	Q, 
	/**
	 * Raw Waveform Data with no Quality Control
	 */
	R, 
	/**
	 * The state of quality control of the data is indeterminate
	 */
	D
}
