package edu.iris.dmc.ws.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.fdsn.station.model.Channel;
import edu.iris.dmc.fdsn.station.model.Coefficients;
import edu.iris.dmc.fdsn.station.model.Decimation;
import edu.iris.dmc.fdsn.station.model.FIR;
import edu.iris.dmc.fdsn.station.model.FIR.NumeratorCoefficient;
import edu.iris.dmc.fdsn.station.model.Gain;
import edu.iris.dmc.fdsn.station.model.Network;
import edu.iris.dmc.fdsn.station.model.PoleZero;
import edu.iris.dmc.fdsn.station.model.PolesZeros;
import edu.iris.dmc.fdsn.station.model.Polynomial;
import edu.iris.dmc.fdsn.station.model.Polynomial.Coefficient;
import edu.iris.dmc.fdsn.station.model.Response;
import edu.iris.dmc.fdsn.station.model.ResponseList;
import edu.iris.dmc.fdsn.station.model.ResponseListElement;
import edu.iris.dmc.fdsn.station.model.ResponseStage;
import edu.iris.dmc.fdsn.station.model.Sensitivity;
import edu.iris.dmc.fdsn.station.model.Station;
import edu.iris.dmc.service.NoDataFoundException;
import edu.iris.dmc.service.ServiceNotSupportedException;
import edu.iris.dmc.service.ServiceUtil;
import edu.iris.dmc.service.StationService;

public class RespUtil {

	static public String formatScientific(String pattern, String positive,
			Double value) {
		ScientificDecimalFormat formatter = new ScientificDecimalFormat(pattern);

		formatter.setPositivePrefix(positive);
		if (value == null) {
			return formatter.format(0);
		}
		return formatter.format(value);
	}

	public static String formatDate(XMLGregorianCalendar xmlCal) {

		if (xmlCal == null) {
			return null;
		}
		Date d = xmlCal.toGregorianCalendar().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy,DDD,HH:mm:ss");
		String s = formatter.format(d);
		return s;
	}

	public static String formatDate(XMLGregorianCalendar xmlCal, String pattern) {
		if (xmlCal == null || pattern == null) {
			return null;
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
		Date d = xmlCal.toGregorianCalendar().getTime();
		String s = timeFormat.format(d);
		return s;
	}

	public static void write(OutputStream out, List<Network> networks) {
		RespUtil.write(new PrintWriter(out), networks);
	}

	public static void write(PrintWriter out, List<Network> networks) {

		for (Network network : networks) {
			for (Station station : network.getStations()) {
				for (Channel channel : station.getChannels()) {
					RespUtil.write(out, network.getCode(), station.getCode(),
							channel);
				}
			}
		}
	}

	public static void write(PrintWriter out, String network, String station,
			Channel channel) {
		out.println("#");
		out.println("###################################################################################");
		out.println("#");
		out.println("B050F03     Station:     " + station);
		out.println("B050F16     Network:     " + network);
		if (channel.getLocationCode().equals("  ")
				|| channel.getLocationCode().equals("")) {
			out.println("B052F03     Location:    ??");
		} else {
			out.println("B052F03     Location:    " + channel.getLocationCode());
		}
		out.println("B052F04     Channel:     " + channel.getCode());
		out.println("B052F22     Start date:  "
				+ RespUtil.formatDate(channel.getStartDate()));
		out.println("B052F23     End date:    "
				+ RespUtil.formatDate(channel.getEndDate()));

		Response response = channel.getResponse();

		for (ResponseStage stage : response.getStage()) {

			if (stage.getPolesZeros() != null) {
				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getPolesZeros());
			}
			if (stage.getCoefficients() != null) {

				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getCoefficients());

			}

			if (stage.getResponseList() != null) {

				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getResponseList());

			}

			if (stage.getFIR() != null) {

				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getFIR());
			}

			if (stage.getPolynomial() != null) {
				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getPolynomial());
			}

			if (stage.getDecimation() != null) {

				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getDecimation());

			}

			if (stage.getStageGain() != null) {

				RespUtil.write(out, stage.getNumber().intValue(), network,
						station, channel.getLocationCode(), channel.getCode(),
						channel.getStartDate(), channel.getEndDate(),
						stage.getStageGain());

			}

		}

		if (response.getInstrumentSensitivity() != null) {
			RespUtil.write(out, 0, network, station, channel.getLocationCode(),
					channel.getCode(), channel.getStartDate(),
					channel.getEndDate(), response.getInstrumentSensitivity());
		}

		if (response.getInstrumentPolynomial() != null) {
			RespUtil.write(out, 0, network, station, channel.getLocationCode(),
					channel.getCode(), channel.getStartDate(),
					channel.getEndDate(), response.getInstrumentPolynomial());
		}
		out.flush();
	}

	private static void writeBox(PrintWriter out, String title, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime) {
		out.println("#");
		out.println("#                  +-----------------------------------+");
		String s = String.format("%-4s%-7s%-4s", network, station, location)
				+ channel;

		out.println("#" + String.format("%19s", "|") + center(title, 35) + "|");

		out.println("#" + String.format("%19s", "|") + center(s, 35) + "|");
		out.println("#                  |     "
				+ RespUtil.formatDate(startTime, "MM/dd/yyyy") + " to "
				+ RespUtil.formatDate(endTime, "MM/dd/yyyy") + "      |");
		out.println("#                  +-----------------------------------+");

		out.println("#");
	}

	private static String center(String s, int size) {
		if (s == null || size == 0 || s.length() >= size) {
			return s;
		}

		int count = (size - s.length()) / 2;
		while (count-- > 0) {
			s = " " + s + " ";
		}
		if (((size - s.length()) % 2) > 0) {
			s = s + " ";
		}
		return s;
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			PolesZeros pzs) {
		RespUtil.writeBox(out, "Response (Poles and Zeros)", network, station,
				location, channel, startTime, endTime);
		String fType = "";
		if ("LAPLACE (RADIANS/SECOND)".equals(pzs.getPzTransferFunctionType())) {
			fType = "A";
		} else if ("LAPLACE (HERTZ)".equals(pzs.getPzTransferFunctionType())) {
			fType = "B";
		} else if ("DIGITAL (Z-TRANSFORM)".equals(pzs
				.getPzTransferFunctionType())) {
			fType = "D";
		} else {
			fType = "Undefined";
		}

		out.println("B053F03     Transfer function type:                "
				+ fType);
		out.println("B053F04     Stage sequence number:                 "
				+ stage);
		out.println("B053F05     Response in units lookup:              "
				+ pzs.getInputUnits().getName() + " - "
				+ pzs.getInputUnits().getDescription());
		out.println("B053F06     Response out units lookup:             "
				+ pzs.getOutputUnits().getName() + " - "
				+ pzs.getOutputUnits().getDescription());
		out.println("B053F07     A0 normalization factor:               "
				+ RespUtil.formatScientific("0.00000E00", "+",
						pzs.getNormalizationFactor()));
		out.println("B053F08     Normalization frequency:               "
				+ RespUtil.formatScientific("0.00000E00", "+", pzs
						.getNormalizationFrequency().getValue()));
		if (pzs.getZero() != null) {
			out.println("B053F09     Number of zeroes:                      "
					+ pzs.getZero().size());
		} else {
			out.println("B053F09     Number of zeroes:                      0");
		}
		if (pzs.getPole() != null) {
			out.println("B053F14     Number of poles:                       "
					+ pzs.getPole().size());
		} else {
			out.println("B053F14     Number of poles:                       0");
		}

		if (pzs.getZero() != null && pzs.getZero().size() > 0) {
			out.println("#              Complex zeroes:");
			out.println("#              i  real          imag          real_error    imag_error");
			for (PoleZero zero : pzs.getZero()) {
				out.println("B053F10-13"
						+ String.format("%6d", zero.getNumber().intValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", zero
								.getReal().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", zero
								.getImaginary().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", zero
								.getReal().getMinusError())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", zero
								.getImaginary().getMinusError()));
			}
		}

		if (pzs.getPole() != null && pzs.getPole().size() > 0) {
			out.println("#              Complex poles:");
			out.println("#              i  real          imag          real_error    imag_error");
			for (PoleZero pole : pzs.getPole()) {
				out.println("B053F15-18"
						+ String.format("%6d", pole.getNumber().intValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", pole
								.getReal().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", pole
								.getImaginary().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", pole
								.getReal().getMinusError())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", pole
								.getImaginary().getMinusError()));
			}
		}

		/*
		 * if( cr.repeat1 is not null or cr.repeat2 is not null ) then
		 * showb53rep( id_in ); end if;
		 */
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			Coefficients coefficients) {
		RespUtil.writeBox(out, "Response (Coefficients)", network, station,
				location, channel, startTime, endTime);
		String fType = "";
		if ("LAPLACE (RADIANS/SECOND)".equals(coefficients
				.getCfTransferFunctionType())) {
			fType = "A";
		} else if ("LAPLACE (HERTZ)".equals(coefficients
				.getCfTransferFunctionType())) {
			fType = "B";
		} else if ("DIGITAL".equals(coefficients.getCfTransferFunctionType())) {
			fType = "D";
		} else {
			fType = "Undefined";
		}
		out.println("B054F03     Transfer function type:                "
				+ fType);
		out.println("B054F04     Stage sequence number:                 "
				+ stage);
		out.println("B054F05     Response in units lookup:              "
				+ coefficients.getInputUnits().getName() + " - "
				+ coefficients.getInputUnits().getDescription());
		out.println("B054F06     Response out units lookup:             "
				+ coefficients.getOutputUnits().getName() + " - "
				+ coefficients.getOutputUnits().getDescription());

		if (coefficients.getNumerator() != null) {
			out.println("B054F07     "
					+ String.format("%-39s%s", "Number of numerators:",
							coefficients.getNumerator().size()));
		} else {
			out.println("B054F07     "
					+ String.format("%-39s%s", "Number of numerators:", 0));
		}
		if (coefficients.getDenominator() != null) {
			out.println("B054F10     "
					+ String.format("%-39s%s", "Number of denominators:",
							coefficients.getDenominator().size()));
		} else {
			out.println("B054F10     "
					+ String.format("%-39s%s", "Number of denominators:", 0));
		}
		if (coefficients.getNumerator() != null
				&& !coefficients.getNumerator().isEmpty()) {
			out.println("#              Numerator coefficients:");
			out.println("#              i  coefficient   error");
			int index = 0;
			for (edu.iris.dmc.fdsn.station.model.Float f : coefficients
					.getNumerator()) {
				out.println("B054F08-09"
						+ String.format("%6s", index++)
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getMinusError()));
			}
		}

		if (coefficients.getDenominator() != null
				&& !coefficients.getDenominator().isEmpty()) {
			out.println("#              Denominator coefficients:");
			out.println("#              i  coefficient   error");
			int index = 0;
			for (edu.iris.dmc.fdsn.station.model.Float f : coefficients
					.getDenominator()) {
				out.println("B054F11-12"
						+ String.format("%6s", index++)
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getMinusError()));
			}
		}
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			ResponseList list) {
		RespUtil.writeBox(out, "Response List", network, station, location,
				channel, startTime, endTime);

		out.println("B055F03     Stage sequence number:                 "
				+ stage);
		out.println("B055F04     Response in units lookup:              "
				+ list.getInputUnits().getName() + " - "
				+ list.getInputUnits().getDescription());
		out.println("B055F05     Response out units lookup:             "
				+ list.getOutputUnits().getName() + " - "
				+ list.getOutputUnits().getDescription());

		if (list.getResponseListElement() != null) {
			out.println("B055F06     "
					+ String.format("%-39s%s", "Number of responses listed:",
							list.getResponseListElement().size()));
		} else {
			out.println("B055F06     "
					+ String.format("%-39s%s", "Number of responses listed:", 0));
		}

		if (list.getResponseListElement() != null
				&& !list.getResponseListElement().isEmpty()) {
			out.println("#              i  frequency     amplitude     amplitude err phase angle   phase err");
			int index = 1;
			String spaceFormat = "%-4s";
			if (list.getResponseListElement().size() > 999) {
				spaceFormat = "%-5s";
			}
			for (ResponseListElement e : list.getResponseListElement()) {
				out.println("B055F07-11     "
						+ String.format(spaceFormat, index++)
						+ RespUtil.formatScientific("0.00000E00", "+", e
								.getFrequency().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", e
								.getAmplitude().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", e
								.getAmplitude().getMinusError())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", e
								.getPhase().getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+", e
								.getPhase().getMinusError()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			Decimation decimation) {
		RespUtil.writeBox(out, "Decimation", network, station, location,
				channel, startTime, endTime);

		out.println("B057F03     Stage sequence number:                  "
				+ stage);

		out.println("B057F04     Input sample rate (HZ):                 "
				+ RespUtil.formatScientific("0.0000E00", "", decimation
						.getInputSampleRate().getValue()));
		out.println("B057F05     Decimation factor:                      "
				+ String.format("%05d", decimation.getFactor().intValue()));
		out.println("B057F06     Decimation offset:                      "
				+ String.format("%05d", decimation.getOffset().intValue()));
		out.println("B057F07     Estimated delay (seconds):             "
				+ RespUtil.formatScientific("0.0000E00", "+", decimation
						.getDelay().getValue()));
		out.println("B057F08     Correction applied (seconds):          "
				+ RespUtil.formatScientific("0.0000E00", "+", decimation
						.getCorrection().getValue()));
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			Gain gain) {

		RespUtil.writeBox(out, " Channel Sensitivity/Gain", network, station,
				location, channel, startTime, endTime);

		out.println("B058F03     Stage sequence number:                 "
				+ stage);

		out.println("B058F04     Sensitivity:                           "
				+ RespUtil.formatScientific("0.00000E00", "+", gain.getValue()));
		out.println("B058F05     Frequency of sensitivity:              "
				+ RespUtil.formatScientific("0.00000E00", "+",
						gain.getFrequency()));
		out.println("B058F06     Number of calibrations:                0");
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			Sensitivity gain) {
		if (gain.getFrequency() == null && gain.getValue() == null) {
			return;
		}

		RespUtil.writeBox(out, " Channel Sensitivity/Gain", network, station,
				location, channel, startTime, endTime);

		out.println("B058F03     Stage sequence number:                 "
				+ stage);

		out.println("B058F04     Sensitivity:                           "
				+ RespUtil.formatScientific("0.00000E00", "+", gain.getValue()));
		out.println("B058F05     Frequency of sensitivity:              "
				+ RespUtil.formatScientific("0.00000E00", "+",
						gain.getFrequency()));
		out.println("B058F06     Number of calibrations:                0");
	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			FIR fir) {
		RespUtil.writeBox(out, "FIR Response", network, station, location,
				channel, startTime, endTime);

		out.println("B061F03     Stage sequence number:                 "
				+ stage);
		out.println("B061F04     Response Name:                         "
				+ fir.getName());
		String symmetry = "";
		if ("EVEN".equals(fir.getSymmetry())) {
			symmetry = "C";
		} else if ("ODD".equals(fir.getSymmetry())) {
			symmetry = "B";
		} else {
			symmetry = "A";
		}
		out.println("B061F05     Symmetry Code:                         "
				+ symmetry);

		out.println("B061F06     Response in units lookup:              "
				+ fir.getInputUnits().getName() + " - "
				+ fir.getInputUnits().getDescription());
		out.println("B061F07     Response out units lookup:             "
				+ fir.getOutputUnits().getName() + " - "
				+ fir.getOutputUnits().getDescription());

		if (fir.getNumeratorCoefficient() != null) {
			out.println("B061F08     "
					+ String.format("%-39s%s", "Number of Coefficients:", fir
							.getNumeratorCoefficient().size()));
		} else {
			out.println("B061F08     "
					+ String.format("%-39s%s", "Number of Coefficients:", 0));
		}

		if (fir.getNumeratorCoefficient() != null
				&& !fir.getNumeratorCoefficient().isEmpty()) {
			out.println("#              i  FIR Coefficient");
			int index = 0;
			for (NumeratorCoefficient f : fir.getNumeratorCoefficient()) {
				out.println("B061F09"
						+ String.format("%6d", index++)
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getValue()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network,
			String station, String location, String channel,
			XMLGregorianCalendar startTime, XMLGregorianCalendar endTime,
			Polynomial polynomial) {
		RespUtil.writeBox(out, "Response (Polynomial)", network, station,
				location, channel, startTime, endTime);

		out.println("B062F03     Transfer function type:                P");
		out.println("B062F04     Stage sequence number:                 "
				+ stage);
		out.println("B062F05     Response in units lookup:              "
				+ polynomial.getInputUnits().getName() + " - "
				+ polynomial.getInputUnits().getDescription());
		out.println("B062F06     Response out units lookup:             "
				+ polynomial.getOutputUnits().getName() + " - "
				+ polynomial.getOutputUnits().getDescription());
		String approximationType = "M";

		if (polynomial.getApproximationType() != null) {
			String temp = polynomial.getApproximationType();

			if ("MACLAURIN".equals(temp)) {
				approximationType = "M";
			}
		}
		out.println("B062F07     Polynomial Approximation Type:         "
				+ approximationType);
		// polynomial.getFrequencyLowerBound().getUnit();

		out.println("B062F08     Valid Frequency Units:                 B");// "+polynomial.getFrequencyLowerBound());
		out.println("B062F09     "
				+ String.format("%-39s%s", "Lower Valid Frequency Bound:",
						RespUtil.formatScientific("0.00000E00", "+", polynomial
								.getFrequencyLowerBound().getValue())));
		out.println("B062F10     "
				+ String.format("%-39s%s", "Upper Valid Frequency Bound:",
						RespUtil.formatScientific("0.00000E00", "+", polynomial
								.getFrequencyUpperBound().getValue())));
		out.println("B062F11     "
				+ String.format("%-39s%s", "Lower Bound of Approximation:",
						RespUtil.formatScientific("0.00000E00", "+", polynomial
								.getApproximationLowerBound().doubleValue())));
		out.println("B062F12     "
				+ String.format("%-39s%s", "Upper Bound of Approximation:",
						RespUtil.formatScientific("0.00000E00", "+", polynomial
								.getApproximationUpperBound().doubleValue())));
		out.println("B062F13     "
				+ String.format("%-39s%s", "Maximum Absolute Error:", RespUtil
						.formatScientific("0.00000E00", "+", polynomial
								.getMaximumError().doubleValue())));

		if (polynomial.getCoefficient() != null) {
			out.println("B062F14     "
					+ String.format("%-39s%s", "Number of Coefficients:",
							polynomial.getCoefficient().size()));
		} else {
			out.println("B062F14     "
					+ String.format("%-39s%s", "Number of Coefficients:", 0));
		}

		if (polynomial.getCoefficient() != null
				&& !polynomial.getCoefficient().isEmpty()) {
			out.println("#              Polynomial coefficients:");
			out.println("#              i, coefficient,  error");
			for (Coefficient f : polynomial.getCoefficient()) {
				out.println("B062F15-16     "
						+ String.format("%-4s", f.getNumber().intValue())
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getValue())
						+ "  "
						+ RespUtil.formatScientific("0.00000E00", "+",
								f.getMinusError()));
			}
		}

	}

	public static void main(String[] args) {
		ServiceUtil util = ServiceUtil.getInstance();
		StationService service = util.getStationService();

		List<Network> net;
		try {
			net = service
					.fetch("http://service.iris.edu/fdsnws/station/1/query?net=AU&sta=MCQ&loc=--&cha=BHZ&starttime=2006-01-01&level=resp");

			RespUtil.write(System.out, net);

		} catch (NoDataFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CriteriaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class ScientificDecimalFormat extends NumberFormat {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1838009550163390117L;
		private final DecimalFormat decimal;

		public ScientificDecimalFormat(String pattern) {
			decimal = new DecimalFormat(pattern);
			decimal.setRoundingMode(RoundingMode.HALF_UP);
		}

		public StringBuffer format(double number, StringBuffer toAppendTo,
				FieldPosition pos) {
			StringBuffer sb = new StringBuffer();
			sb.append(modified(number, Math.abs(number) >= 1.0,
					decimal.format(number, toAppendTo, pos).toString()));
			return sb;
		}

		private String modified(double num, boolean large, String s) {
			if (large) {
				return s.replace("E", "E+");
			} else {
				if (num == 0) {
					return s.replace("E", "E+");
				} else {
					return s;
				}
			}
			// return large ? s.replace("E", "E+") : s;
		}

		public StringBuffer format(long number, StringBuffer toAppendTo,
				FieldPosition pos) {
			StringBuffer sb = new StringBuffer();
			sb.append(modified(number, true,
					decimal.format(number, toAppendTo, pos).toString()));
			return sb;
		}

		public Number parse(String source, ParsePosition parsePosition) {
			return decimal.parse(source, parsePosition);
		}

		public void setPositivePrefix(String newValue) {
			decimal.setPositivePrefix(newValue);
		}
	}
}
