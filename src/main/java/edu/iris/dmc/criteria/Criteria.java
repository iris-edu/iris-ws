package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface Criteria {

	/**
	 * Constructs url's parameters
	 * 
	 * @return
	 * @throws CriteriaException
	 * @throws UnsupportedEncodingException
	 */
	public List<String> toUrlParams() throws CriteriaException,
			UnsupportedEncodingException;

	/**
	 * Will reset the criteria to its defaults
	 */
	public void reset();

}
