package edu.iris.dmc.service;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * A utility class to acquire services
 * 
 * @author yazan
 * 
 */
public class ServiceUtil {
	private static String STATION_SERVICE_DEFAULT_URL = "http://service.iris.edu/fdsnws/station/1/";

	private static String EVENT_SERVICE_DEFAULT_URL = "http:// service.iris.edu/fdsnws/event/1/";
	private static String DATASELECT_SERVICE_DEFAULT_URL = "http:// service.iris.edu/fdsnws/dataselect/1/";
	private static String SACPZ_SERVICE_DEFAULT_URL = "http:// service.iris.edu/irisws/sacpz/1/";
	private static String RESP_SERVICE_DEFAULT_URL = "http:// service.iris.edu/irisws/resp/1/";
	private static String STATION_VERSION = "1.1";
	private static String RESP_VERSION = "1.5.1";
	private static String EVENT_VERSION = "1.0";
	private static String SACPZ_VERSION = "1.1.1";
	private static String BULKDATASELECT_VERSION = "1.0";
	private static String DEFAULT_USER_AGENT = "IRIS-WS-Library";
	private static String VERSION = "2.0.15";

	private static ServiceUtil instance;
	private StationService stationService;
	private WaveformService waveformService;
	private EventService eventService;
	private SacpzService sacpzService;
	private RespService respService;

	// private static ResourceBundle rb = ResourceBundle
	// .getBundle("iris");

	private String appName;

	static {
		instance = new ServiceUtil();
	}

	private ServiceUtil() {

		stationService = new StationService(STATION_SERVICE_DEFAULT_URL, VERSION, STATION_VERSION, DEFAULT_USER_AGENT);
		respService = new RespService(RESP_SERVICE_DEFAULT_URL, VERSION, RESP_VERSION, DEFAULT_USER_AGENT);
		eventService = new EventService(EVENT_SERVICE_DEFAULT_URL, VERSION, EVENT_VERSION, DEFAULT_USER_AGENT);
		sacpzService = new SacpzService(SACPZ_SERVICE_DEFAULT_URL, VERSION, SACPZ_VERSION, DEFAULT_USER_AGENT);
		waveformService = new WaveformService(DATASELECT_SERVICE_DEFAULT_URL, VERSION, BULKDATASELECT_VERSION, DEFAULT_USER_AGENT);
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return this.appName;
	}

	/**
	 * Convenient method to get a station service object
	 * 
	 * @return StationService
	 */
	public StationService getStationService() {
		/*
		 * if (this.appName != null && this.appName.length() > 0) {
		 * stationService.setAppName(this.appName); String stationUrl =
		 * rb.getString("stationUrl"); stationService.setBaseUrl(stationUrl); }
		 * try { stationService.validateVersion(); return stationService; }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ServiceNotSupportedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } return null;
		 */
		String stationUrl = STATION_SERVICE_DEFAULT_URL;
		return this.getStationService(stationUrl);
	}

	/**
	 * Convenient method to get a station service object targeting the specified
	 * url
	 * 
	 * @return StationService
	 */
	public StationService getStationService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			stationService.setAppName(this.appName);
			stationService.setBaseUrl(url);
		}
		try {
			stationService.validateVersion();
			return stationService;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convenient method to get a resp service object
	 * 
	 * @return StationService
	 */
	public RespService getRespService() {
		if (this.appName != null && this.appName.length() > 0) {
			respService.setAppName(this.appName);
		}
		return respService;
	}

	/**
	 * Convenient method to get a station service object targeting the specified
	 * url
	 * 
	 * @return StationService
	 */
	public RespService getRespService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			respService.setAppName(this.appName);
		}

		if (url != null) {
			respService.setBaseUrl(url);
		}
		return respService;
	}

	/**
	 * Convenient method to get a event service object
	 * 
	 * @return EventService
	 */
	public EventService getEventService() {
		if (this.appName != null && this.appName.length() > 0) {
			eventService.setAppName(this.appName);
		}
		return eventService;
	}

	/**
	 * Convenient method to get a event service object targeting the specified
	 * url
	 * 
	 * @return EventService
	 */
	public EventService getEventService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			eventService.setAppName(this.appName);
		}
		if (url != null) {
			eventService.setBaseUrl(url);
		}
		return eventService;
	}

	/**
	 * Convenient method to get a sacpz service object
	 * 
	 * @return EventService
	 */
	public SacpzService getSacpzService() {
		if (this.appName != null && this.appName.length() > 0) {
			sacpzService.setAppName(this.appName);
		}
		return sacpzService;
	}

	/**
	 * Convenient method to get a sacpz service object targeting the specified
	 * url
	 * 
	 * @return EventService
	 */
	public SacpzService getSacpzService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			sacpzService.setAppName(this.appName);
		}
		if (url != null) {
			sacpzService.setBaseUrl(url);
		}
		return sacpzService;
	}

	/**
	 * Convenient method to get a waveform service object
	 * 
	 * @return WaveFormService
	 */
	public WaveformService getWaveformService() {
		return this.getWaveformService(DATASELECT_SERVICE_DEFAULT_URL);
	}

	/**
	 * Convenient method to get a waveform service object
	 * 
	 * @return WaveFormService
	 */
	public WaveformService getWaveformService(String username, String password) {
		waveformService.setAuth(username, password);
		return this.getWaveformService();
	}

	/**
	 * Convenient method to get a waveform service object targeting the
	 * specified url
	 * 
	 * @return WaveFormService
	 */
	public WaveformService getWaveformService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			waveformService.setAppName(this.appName);
		}
		if (url != null) {
			waveformService.setBaseUrl(url);
		}
		try {
			waveformService.validateVersion();
			return waveformService;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ServiceUtil getInstance() {
		return instance;
	}
}
