package edu.iris.dmc.ws.util;

import edu.iris.dmc.fdsn.station.model.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

public class TextStreamUtil {
	private static final ObjectFactory objectFactory = new ObjectFactory();

	private TextStreamUtil() {
	}

	public static Network buildNetwork(List<String> columns) throws IOException {
		if (columns != null && columns.size() == 5) {
			Network network = new Network();
			network.setCode((String)columns.get(0));
			network.setDescription((String)columns.get(1));

			try {
				network.setStartDate(DateUtil.parseAny((String)columns.get(2)));
				network.setEndDate(DateUtil.parseAny((String)columns.get(3)));
			} catch (ParseException var4) {
				throw new IOException(var4);
			}

			if (columns.size() > 4) {
				network.setTotalNumberStations(BigInteger.valueOf((long)Integer.parseInt((String)columns.get(4))));
			}

			return network;
		} else {
			String message = "Invalid array format, expected 5 but received ";
			if (columns == null) {
				message = message + " null";
			} else {
				message = message + columns.size() + " ";
				String s;
				for(Iterator<String> it = columns.iterator(); it.hasNext(); message = message + "[" + s + "]") {
					s = (String)it.next();
				}
			}
			throw new IOException(message);
		}
	}

	public static Station buildStation(List<String> columns) throws IOException {
		if (columns != null && columns.size() >= 6) {
			Station station = new Station();
			station.setCode((String)columns.get(0));
			if (columns.get(1) != null && !((String)columns.get(1)).trim().isEmpty()) {
				Latitude latitude = objectFactory.createLatitudeType();
				latitude.setValue(Double.parseDouble((String)columns.get(1)));
				station.setLatitude(latitude);
			}

			if (columns.get(2) != null && !((String)columns.get(2)).trim().isEmpty()) {
				Longitude longitude = objectFactory.createLongitudeType();
				longitude.setValue(Double.parseDouble((String)columns.get(2)));
				station.setLongitude(longitude);
			}

			if (columns.get(3) != null && !((String)columns.get(3)).trim().isEmpty()) {
				Distance distance = objectFactory.createDistanceType();
				distance.setValue(Double.parseDouble((String)columns.get(3)));
				station.setElevation(distance);
			}

			String siteName = (String)columns.get(4);
			if (siteName != null && !siteName.trim().isEmpty()) {
				Site site = objectFactory.createSiteType();
				site.setName(siteName);
				station.setSite(site);
			}

			try {
				station.setStartDate(DateUtil.parseAny((String)columns.get(5)));
				if (columns.size() > 6) {
					station.setEndDate(DateUtil.parseAny((String)columns.get(6)));
				}

				return station;
			} catch (ParseException var4) {
				throw new IOException(var4);
			}
		} else {
			String message = "Invalid array format, expected 6|7 but received ";
			if (columns == null) {
				message = message + " null";
			} else {
				message = message + columns.size() + " ";

				String s;
				for(Iterator var2 = columns.iterator(); var2.hasNext(); message = message + "[" + s + "]") {
					s = (String)var2.next();
				}
			}

			throw new IOException(message);
		}
	}

	public static Channel buildChannel(List<String> list) throws IOException {
		String s;
		if (list != null && list.size() >= 14) {
			Channel channel = new Channel();
			String location = (String)list.get(0);
			if (location == null) {
			}

			location = location.trim();
			if (location.isEmpty()) {
			}

			channel.setLocationCode((String)list.get(0));
			channel.setCode((String)list.get(1));
			s = (String)list.get(2);
			if (s != null && !s.trim().isEmpty()) {
				Latitude latitude = objectFactory.createLatitudeType();
				latitude.setValue(Double.parseDouble(s));
				channel.setLatitude(latitude);
			}

			s = (String)list.get(3);
			if (s != null && !s.trim().isEmpty()) {
				Longitude longitude = objectFactory.createLongitudeType();
				longitude.setValue(Double.parseDouble(s));
				channel.setLongitude(longitude);
			}

			s = (String)list.get(4);
			Distance distance;
			if (s != null && !s.trim().isEmpty()) {
				distance = objectFactory.createDistanceType();
				distance.setValue(Double.parseDouble(s));
				channel.setElevation(distance);
			}

			s = (String)list.get(5);
			if (s != null && !s.trim().isEmpty()) {
				distance = objectFactory.createDistanceType();
				distance.setValue(Double.parseDouble(s));
				channel.setDepth(distance);
			}

			s = (String)list.get(6);
			if (s != null && !s.trim().isEmpty()) {
				Azimuth azimuth = objectFactory.createAzimuthType();
				azimuth.setValue(Double.parseDouble(s));
				channel.setAzimuth(azimuth);
			}

			s = (String)list.get(7);
			if (s != null && !s.trim().isEmpty()) {
				Dip dip = objectFactory.createDipType();
				dip.setValue(Double.parseDouble(s));
				channel.setDip(dip);
			}

			s = (String)list.get(8);
			if (s != null && !s.trim().isEmpty()) {
				Equipment e = new Equipment();
				e.setDescription(s);
				channel.setEquipment(e);
			}

			Response response = objectFactory.createResponseType();
			Sensitivity sensitivity = objectFactory.createSensitivityType();
			s = (String)list.get(9);
			if (s != null && !s.isEmpty()) {
				sensitivity.setValue(Double.parseDouble(s));
			}

			s = (String)list.get(10);
			if (s != null && !s.isEmpty()) {
				sensitivity.setFrequency(Double.parseDouble(s));
			}

			s = (String)list.get(11);
			if (s != null && !s.isEmpty()) {
				Units unit = objectFactory.createUnitsType();
				unit.setName(s);
				sensitivity.setInputUnits(unit);
			}

			response.setInstrumentSensitivity(sensitivity);
			channel.setResponse(response);
			s = (String)list.get(12);
			if (s != null && !s.trim().isEmpty()) {
				SampleRate sr = objectFactory.createSampleRateType();
				sr.setValue(Double.parseDouble(s));
				channel.setSampleRate(sr);
			}

			try {
				s = (String)list.get(13);
				channel.setStartDate(DateUtil.parseAny(s));
				if (list.size() > 14) {
					s = (String)list.get(14);
					channel.setEndDate(DateUtil.parseAny(s));
				}
				return channel;
			} catch (ParseException var7) {
				throw new IOException(var7);
			}
		} else {
			String message = "Invalid list format, expected 15 but received ";
			if (list == null) {
				message = message + " null";
			} else {
				message = message + list.size() + " ";
				for(Iterator<String> it = list.iterator(); it.hasNext(); message = message + "[" + s + "]") {
					s = (String)it.next();
				}
			}
			throw new IOException(message);
		}
	}
}
