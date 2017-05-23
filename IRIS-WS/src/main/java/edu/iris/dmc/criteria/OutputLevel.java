package edu.iris.dmc.criteria;

/**
 * Specific to station service, result can be filtered into subsets using levels NETWORK, STATION, CHANNEL and RESPONSE
 *
 */
public enum OutputLevel {
	NETWORK("network"), STATION("station"), CHANNEL("channel"), RESPONSE("response");
	
	private final String text;
	
	private OutputLevel(final String text) {
        this.text = text;
    }
	
	@Override
    public String toString() {
        return text;
    }
}
