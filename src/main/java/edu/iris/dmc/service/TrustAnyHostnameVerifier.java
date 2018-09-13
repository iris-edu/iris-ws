package edu.iris.dmc.service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Util4J
 * @author Util4J
 *
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {
	
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
