package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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

	public Map<String, List<String>> toMapUrlParameters();
	/**
	 * Will reset the criteria to its defaults
	 */
	public void reset();

}
