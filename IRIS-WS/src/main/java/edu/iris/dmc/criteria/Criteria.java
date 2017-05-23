package edu.iris.dmc.criteria;

import java.io.UnsupportedEncodingException;

public interface Criteria {

	public String toUrlQuery() throws CriteriaException, UnsupportedEncodingException;
}
