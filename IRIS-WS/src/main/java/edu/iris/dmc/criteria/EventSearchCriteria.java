package edu.iris.dmc.criteria;

import java.util.Date;

public class EventSearchCriteria extends SimpleGeoCriteria {

	private EventSearchCriteria() {
		super();
	}

	public EventSearchCriteria addEventId(Integer id) {
		this.add("eventid", id);
		return this;
	}

	public EventSearchCriteria addContributor(String contributor) {
		this.add("contributor", contributor);
		return this;
	}

	public EventSearchCriteria addCatalog(String catalog) {
		this.add("catalog", catalog);
		return this;
	}

	public EventSearchCriteria setType(String type) {
		this.add("type", type);
		return this;
	}

	public EventSearchCriteria includeArrivals(boolean bool) {
		return this;
	}

	public EventSearchCriteria includeAllOrigins(boolean bool) {
		return this;
	}

	public EventSearchCriteria includeAllMagnitudes(boolean bool) {
		return this;
	}

	public EventSearchCriteria setMinimumDepth(Double minumumDepth) {
		return this;
	}

	public EventSearchCriteria setMaximumDepth(Double maximumDepth) {
		return this;
	}

	public EventSearchCriteria setEndBefore(Date date) {
		this.add("endbefore", date);
		return this;
	}

	public EventSearchCriteria setEndAfter(Date date) {
		this.add("endafter", date);
		return this;
	}

	public EventSearchCriteria setStartBefore(Date date) {
		this.add("startbefore", date);
		return this;
	}

	public EventSearchCriteria setStartAfter(Date date) {
		this.add("startafter", date);
		return this;
	}

}
