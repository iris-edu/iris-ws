package edu.iris.dmc.criteria;

/**
 * Specific to station service, result can be filtered into subsets using levels
 * NETWORK, STATION, CHANNEL and RESPONSE
 * 
 */
public enum OutputFormat {
	XML("xml"), TEXT("text"), TEXTTREE("texttree");

	private final String text;

	private OutputFormat(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
