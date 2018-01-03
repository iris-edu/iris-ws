package edu.iris.dmc.ws.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class StringUtil {

	public static String toString(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
		StringBuilder builder = new StringBuilder();
		while (scanner.hasNext()) {
			builder.append(scanner.next());
		}
		return builder.toString();
	}

	public static URI createURI(final String scheme, final String host,
			int port, final String path, final String query,
			final String fragment) throws URISyntaxException {

		StringBuilder buffer = new StringBuilder();
		if (host != null) {
			if (scheme != null) {
				buffer.append(scheme);
				buffer.append("://");
			}
			buffer.append(host);
			if (port > 0) {
				buffer.append(':');
				buffer.append(port);
			}
		}
		if (path == null || !path.startsWith("/")) {
			buffer.append('/');
		}
		if (path != null) {
			buffer.append(path);
		}
		if (query != null) {
			buffer.append('?');
			buffer.append(query);
		}
		if (fragment != null) {
			buffer.append('#');
			buffer.append(fragment);
		}
		return new URI(buffer.toString());
	}
}
