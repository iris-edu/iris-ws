package edu.iris.dmc.ws.util;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import edu.iris.dmc.fdsn.station.model.Azimuth;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Dip;
import edu.iris.dmc.fdsn.station.model.Distance;
import edu.iris.dmc.fdsn.station.model.Equipment;
import edu.iris.dmc.fdsn.station.model.Latitude;
import edu.iris.dmc.fdsn.station.model.Longitude;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.ObjectFactory;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.SampleRate;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Site;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.fdsn.station.model.Units;

public class TextStreamUtil {

	
	private static ObjectFactory objectFactory = new ObjectFactory();
	private static DatatypeFactory datatypeFactory;
	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static Network buildNetwork(List<String> columns) throws IOException {
		if (columns == null || columns.size() != 5) {
			String message = "Invalid array format, expected 5 but received ";

			if (columns == null) {
				message = message + " null";
			} else {
				message = message + columns.size() + " ";
				for (String s : columns) {
					message = message + "[" + s + "]";
				}
			}
			throw new IOException(message);
		}

		Network network = new Network();
		network.setCode(columns.get(0));
		network.setDescription(columns.get(1));

		try {
			network.setStartDate(TextStreamUtil.toDate(columns
							.get(2)));

			network.setEndDate(TextStreamUtil.toDate(columns
							.get(3)));
		} catch (ParseException e) {
			throw new IOException(e);
		}
		if (columns.size() > 4) {
			network.setTotalNumberStations(BigInteger.valueOf(Integer
					.parseInt(columns.get(4))));
		}
		return network;
	}

	public static Station buildStation(List<String> columns) throws IOException {
		if (columns == null || columns.size() < 6) {
			String message = "Invalid array format, expected 6|7 but received ";

			if (columns == null) {
				message = message + " null";
			} else {
				message = message + columns.size() + " ";
				for (String s : columns) {
					message = message + "[" + s + "]";
				}
			}
			throw new IOException(message);
		}

		Station station = new Station();
		station.setCode(columns.get(0));

		if (columns.get(1) != null && !columns.get(1).trim().isEmpty()) {
			Latitude latitude = TextStreamUtil.objectFactory
					.createLatitudeType();
			latitude.setValue(Double.parseDouble(columns.get(1)));
			station.setLatitude(latitude);
		}

		if (columns.get(2) != null && !columns.get(2).trim().isEmpty()) {
			Longitude longitude = TextStreamUtil.objectFactory
					.createLongitudeType();
			longitude.setValue(Double.parseDouble(columns.get(2)));
			station.setLongitude(longitude);
		}
		if (columns.get(3) != null && !columns.get(3).trim().isEmpty()) {
			Distance distance = objectFactory.createDistanceType();
			distance.setValue(Double.parseDouble(columns.get(3)));
			station.setElevation(distance);
		}
		String siteName = columns.get(4);
		if (siteName != null && !siteName.trim().isEmpty()) {
			Site site = objectFactory.createSiteType();
			site.setName(siteName);
			station.setSite(site);
		}

		try {
			station.setStartDate(TextStreamUtil.toDate(columns
							.get(5)));

			if (columns.size() > 6) {
				station.setEndDate(TextStreamUtil.toDate(columns
								.get(6)));
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}
		return station;
	}

	// Location | Channel | Latitude | Longitude | Elevation | Depth | Azimuth | Dip | Instrument | Scale | ScaleFreq | ScaleUnits | SampleRate | StartTime | EndTime
	public static Channel buildChannel(List<String> list) throws IOException {
		if (list == null || list.size() < 14) {
			String message = "Invalid list format, expected 15 but received ";

			if (list == null) {
				message = message + " null";
			} else {
				message = message + list.size() + " ";
				for (String s : list) {
					message = message + "[" + s + "]";
				}
			}
			throw new IOException(message);
		}
		Channel channel = new Channel();
		String location = list.get(0);
		if(location==null){
			
		}
		location=location.trim();
		if(location.isEmpty()){
			
		}
		channel.setLocationCode(list.get(0));
		channel.setCode(list.get(1));

		String s = list.get(2);
		if (s != null && !s.trim().isEmpty()) {
			Latitude latitude = TextStreamUtil.objectFactory
					.createLatitudeType();
			latitude.setValue(Double.parseDouble(s));
			channel.setLatitude(latitude);
		}
		s = list.get(3);
		if (s != null && !s.trim().isEmpty()) {
			Longitude longitude = TextStreamUtil.objectFactory
					.createLongitudeType();
			longitude.setValue(Double.parseDouble(s));
			channel.setLongitude(longitude);
		}
		s = list.get(4);
		if (s != null && !s.trim().isEmpty()) {
			Distance distance = objectFactory.createDistanceType();
			distance.setValue(Double.parseDouble(s));
			channel.setElevation(distance);
		}
		s = list.get(5);
		if (s != null && !s.trim().isEmpty()) {
			Distance distance = objectFactory.createDistanceType();
			distance.setValue((Double.parseDouble(s)));
			channel.setDepth(distance);
		}
		s = list.get(6);
		if (s != null && !s.trim().isEmpty()) {
			Azimuth azimuth = objectFactory.createAzimuthType();
			azimuth.setValue((Double.parseDouble(s)));
			channel.setAzimuth(azimuth);
		}
		s = list.get(7);
		if (s != null && !s.trim().isEmpty()) {
			Dip dip = objectFactory.createDipType();
			dip.setValue((Double.parseDouble(s)));
			channel.setDip(dip);
		}
		s = list.get(8);
		if (s != null && !s.trim().isEmpty()) {
			Equipment e = new Equipment();
			e.setDescription(s);
			channel.setEquipment(e);
		}

		// instrument|scale

		Response response = objectFactory.createResponseType();
		Sensitivity sensitivity = objectFactory.createSensitivityType();

		s = list.get(9);
		if (s != null && !s.isEmpty()) {
			sensitivity.setValue(Double.parseDouble(s));
		}
		s = list.get(10);
		if (s != null && !s.isEmpty()) {
			sensitivity.setFrequency(Double.parseDouble(s));
		}
		s = list.get(11);
		if (s != null && !s.isEmpty()) {
			Units unit = objectFactory.createUnitsType();
			unit.setName(s);
			sensitivity.setInputUnits(unit);
		}
		response.setInstrumentSensitivity(sensitivity);
		channel.setResponse(response);
		s = list.get(12);
		if (s != null && !s.trim().isEmpty()) {
			SampleRate sr = objectFactory.createSampleRateType();
			sr.setValue((Double.parseDouble(s)));
			channel.setSampleRate(sr);
		}

		try {
			s = list.get(13);
			channel.setStartDate(TextStreamUtil.toDate(s));

			if (list.size() > 14) {
				s = list.get(14);
				channel.setEndDate(TextStreamUtil.toDate(s));
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}
		return channel;
	}

	public static Date toDate(String string)
			throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = format.parse(string);
		return date;
	}
}
