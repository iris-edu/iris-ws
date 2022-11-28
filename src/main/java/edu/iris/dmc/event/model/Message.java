package edu.iris.dmc.event.model;

import edu.iris.quake.model.Quakeml;

import java.util.ArrayList;
import java.util.List;

public class Message extends Quakeml {

	private Quakeml q;

	public Message(){}
	public Message(Object q) {
		if (q instanceof Quakeml) {
			this.q = (Quakeml)q;
		}
	}

	public List<Event> getEvents() {
		List<Object> objects = this.q.getEventParameters()
				.getCommentOrEventOrDescription();

		List<Event> events = new ArrayList<Event>();
		for (Object o : objects) {
			if (o instanceof edu.iris.quake.model.Event) {
				events.add(new Event((edu.iris.quake.model.Event) o));
			}
		}

		return events;
	}
}
