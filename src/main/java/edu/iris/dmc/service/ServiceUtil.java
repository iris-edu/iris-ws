package edu.iris.dmc.service;

public class ServiceUtil {
	private static String STATION_SERVICE_DEFAULT_URL = "http://service.iris.edu/fdsnws/station/1/";
	private static String EVENT_SERVICE_DEFAULT_URL = "http://service.iris.edu/fdsnws/event/1/";
	private static String DATASELECT_SERVICE_DEFAULT_URL = "http://service.iris.edu/fdsnws/dataselect/1/";
	private static String SACPZ_SERVICE_DEFAULT_URL = "http://service.iris.edu/irisws/sacpz/1/";
	private static String RESP_SERVICE_DEFAULT_URL = "http://service.iris.edu/irisws/resp/1/";
	private static String STATION_VERSION = "1.1";
	private static String RESP_VERSION = "1.5.1";
	private static String EVENT_VERSION = "1.0";
	private static String SACPZ_VERSION = "1.1.1";
	private static String BULKDATASELECT_VERSION = "1.0";
	private static String DEFAULT_USER_AGENT = "IRIS-WS-Library";
	private static String VERSION = "2.0.17";
	private static ServiceUtil instance = new ServiceUtil();
	private StationService stationService;
	private WaveformService waveformService;
	private EventService eventService;
	private SacpzService sacpzService;
	private RespService respService;
	private String appName;

	private ServiceUtil() {
		this.stationService = new StationService(STATION_SERVICE_DEFAULT_URL, VERSION, STATION_VERSION, DEFAULT_USER_AGENT);
		this.respService = new RespService(RESP_SERVICE_DEFAULT_URL, VERSION, RESP_VERSION, DEFAULT_USER_AGENT);
		this.eventService = new EventService(EVENT_SERVICE_DEFAULT_URL, VERSION, EVENT_VERSION, DEFAULT_USER_AGENT);
		this.sacpzService = new SacpzService(SACPZ_SERVICE_DEFAULT_URL, VERSION, SACPZ_VERSION, DEFAULT_USER_AGENT);
		this.waveformService = new WaveformService(DATASELECT_SERVICE_DEFAULT_URL, VERSION, BULKDATASELECT_VERSION, DEFAULT_USER_AGENT);
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return this.appName;
	}

	public StationService getStationService() {
		String stationUrl = STATION_SERVICE_DEFAULT_URL;
		return this.getStationService(stationUrl);
	}

	public StationService getStationService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			this.stationService.setAppName(this.appName);
			this.stationService.setBaseUrl(url);
		}

		return this.stationService;
	}

	public RespService getRespService() {
		if (this.appName != null && this.appName.length() > 0) {
			this.respService.setAppName(this.appName);
		}

		return this.respService;
	}

	public RespService getRespService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			this.respService.setAppName(this.appName);
		}

		if (url != null) {
			this.respService.setBaseUrl(url);
		}

		return this.respService;
	}

	public EventService getEventService() {
		if (this.appName != null && this.appName.length() > 0) {
			this.eventService.setAppName(this.appName);
		}

		return this.eventService;
	}

	public EventService getEventService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			this.eventService.setAppName(this.appName);
		}

		if (url != null) {
			this.eventService.setBaseUrl(url);
		}

		return this.eventService;
	}

	public SacpzService getSacpzService() {
		if (this.appName != null && this.appName.length() > 0) {
			this.sacpzService.setAppName(this.appName);
		}

		return this.sacpzService;
	}

	public SacpzService getSacpzService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			this.sacpzService.setAppName(this.appName);
		}

		if (url != null) {
			this.sacpzService.setBaseUrl(url);
		}

		return this.sacpzService;
	}

	public WaveformService getWaveformService() {
		return this.getWaveformService(DATASELECT_SERVICE_DEFAULT_URL);
	}

	public WaveformService getWaveformService(String username, String password) {
		this.waveformService.setAuth(username, password);
		return this.getWaveformService();
	}

	public WaveformService getWaveformService(String url) {
		if (this.appName != null && this.appName.length() > 0) {
			this.waveformService.setAppName(this.appName);
		}

		if (url != null) {
			this.waveformService.setBaseUrl(url);
		}

		return this.waveformService;
	}

	public static ServiceUtil getInstance() {
		return instance;
	}
}