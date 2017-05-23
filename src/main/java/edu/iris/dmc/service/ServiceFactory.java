package edu.iris.dmc.service;

import edu.iris.dmc.event.model.Event;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.service.response.EventResponseHandler;
import edu.iris.dmc.service.response.SacpzResponseHandler;
import edu.iris.dmc.service.response.StationXmlResponseHandler;
import edu.iris.dmc.service.response.TimeseriesResponseHandler;
import edu.iris.dmc.timeseries.model.Timeseries;

public class ServiceFactory {

	public static String DEFAULT_HOST = "service.iris.edu";
	public static String DEFAULT_STATION_END_POINT = "/fdsnws/station/1/query";
	public static String DEFAULT_EVENT_END_POINT = "/fdsnws/station/1/query";
	public static String DEFAULT_TIMESERIES_END_POINT = "/fdsnws/dataselect/1/query";

	public static String DEFAULT_SACPZ_END_POINT = "/irisws/sacpz/1/query";
	private static IrisService<Network> stationService = new IrisServiceImp<>(new StationXmlResponseHandler(),
			DEFAULT_HOST, DEFAULT_STATION_END_POINT, "");
	private static IrisService<Event> eventService = new IrisServiceImp<>(new EventResponseHandler(), DEFAULT_HOST,
			DEFAULT_EVENT_END_POINT, "");

	private static IrisService<Timeseries> timeseriesService = new IrisServiceImp<>(new TimeseriesResponseHandler(),
			DEFAULT_HOST, DEFAULT_TIMESERIES_END_POINT, "");

	private static IrisService<Sacpz> sacpzService = new IrisServiceImp<>(new SacpzResponseHandler(), DEFAULT_HOST,
			DEFAULT_SACPZ_END_POINT, "");

	public static IrisService<Network> getStationService() {
		return stationService;
	}

	public static IrisService<Network> getStationService(String host, String endPoint) {
		return new IrisServiceImp<Network>(new StationXmlResponseHandler(), host, endPoint, "");
	}

	public static IrisService<Event> getEventService() {
		return eventService;
	}

	public static IrisService<Event> getEventService(String host, String endPoint) {
		return new IrisServiceImp<Event>(new EventResponseHandler(), host, endPoint, "");
	}

	public static IrisService<Timeseries> getTimeseriesService() {
		return timeseriesService;
	}

	public static IrisService<Timeseries> getTimeseriesService(String host, String endPoint) {
		return new IrisServiceImp<Timeseries>(new TimeseriesResponseHandler(), host, endPoint, "");
	}

	public static IrisService<Sacpz> getSacpzService() {
		return sacpzService;
	}

	public static IrisService<Sacpz> getSacpzService(String host, String endPoint) {
		return new IrisServiceImp<Sacpz>(new SacpzResponseHandler(), host, endPoint, "");
	}

	public static <T> IrisService<T> createService(ResponseHandler<T> handler) {
		return new IrisServiceImp<T>(handler, null, null, "");
	}

	public static <T> IrisService<T> createService(String host, String endPoint, ResponseHandler<T> handler) {
		return new IrisServiceImp<T>(handler, host, endPoint, "");
	}

}
