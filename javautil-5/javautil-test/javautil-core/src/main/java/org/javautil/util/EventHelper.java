package org.javautil.util;

import java.util.HashSet;

public class EventHelper {
	private final HashSet<Integer> events = new HashSet<Integer>();

	public EventHelper() {

	}

	public void addEvent(Integer eventNumber) {
		events.add(eventNumber);
	}

	public boolean exists(Integer event) {
		return events.contains(event);
	}

	public void removeEvent(Integer value) {
		events.remove(value);

	}
}
