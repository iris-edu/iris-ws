//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.ws.util;

import edu.iris.dmc.fdsn.station.model.Float;
import edu.iris.dmc.fdsn.station.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.math.RoundingMode;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RespUtil {
	private static String NO_ENDING_TIME = "No Ending Time";

	public RespUtil() {
	}

	public static String formatScientific(String pattern, String positive, Double value) {
		RespUtil.ScientificDecimalFormat formatter = new RespUtil.ScientificDecimalFormat(pattern);
		formatter.setPositivePrefix(positive);
		return value == null ? formatter.format(0L) : formatter.format(value);
	}

	public static Date readDate(String text) throws ParseException {
		return text != null && !NO_ENDING_TIME.equalsIgnoreCase(text) ? DateUtil.parseAny(text) : null;
	}

	public static String formatDate(Date date) {
		return formatDate(date, "yyyy,DDD,HH:mm:ss");
	}

	public static String formatDate(XMLGregorianCalendar xmlCal) {
		if (xmlCal == null) {
			return null;
		} else {
			Date d = xmlCal.toGregorianCalendar().getTime();
			return DateUtil.format(d, "yyyy,DDD,HH:mm:ss");
		}
	}

	public static String formatDate(Date date, String pattern) {
		return date != null && pattern != null ? DateUtil.format(date, pattern) : null;
	}

	public static String formatDate(XMLGregorianCalendar xmlCal, String pattern) {
		if (xmlCal != null && pattern != null) {
			Date d = xmlCal.toGregorianCalendar().getTime();
			return DateUtil.format(d, pattern);
		} else {
			return null;
		}
	}

	public static void write(OutputStream out, List<Network> networks) {
		write(new PrintWriter(out), networks);
	}

	public static void write(PrintWriter out, List<Network> networks) {

		for (Network network : networks) {
			if(network.getStations()!=null) {
				for (Station station : network.getStations()) {
					if(station.getChannels()!=null) {
						for (Channel channel : station.getChannels()) {
							write(out, network.getCode(), station.getCode(), channel);
						}
					}
				}
			}
		}

	}

	public static void write(PrintWriter out, String network, String station, Channel channel) {
		out.println("#");
		out.println("###################################################################################");
		out.println("#");
		out.println("B050F03     Station:     " + station);
		out.println("B050F16     Network:     " + network);
		if (!channel.getLocationCode().equals("  ") && !channel.getLocationCode().equals("")) {
			out.println("B052F03     Location:    " + channel.getLocationCode());
		} else {
			out.println("B052F03     Location:    ??");
		}

		out.println("B052F04     Channel:     " + channel.getCode());
		out.println("B052F22     Start date:  " + formatDate(channel.getStartDate()));
		String end = formatDate(channel.getEndDate());
		if (end == null) {
			end = NO_ENDING_TIME;
		}

		out.println("B052F23     End date:    " + end);
		Response response = channel.getResponse();

		for (ResponseStage stage : response.getStage()) {
			if (stage.getPolesZeros() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getPolesZeros());
			}

			if (stage.getCoefficients() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getCoefficients());
			}

			if (stage.getResponseList() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getResponseList());
			}

			if (stage.getFIR() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getFIR());
			}

			if (stage.getPolynomial() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getPolynomial());
			}

			if (stage.getDecimation() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getDecimation());
			}

			if (stage.getStageGain() != null) {
				write(out, stage.getNumber().intValue(), network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), stage.getStageGain());
			}
		}

		if (response.getInstrumentSensitivity() != null) {
			write(out, 0, network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), (Sensitivity)response.getInstrumentSensitivity());
		}

		if (response.getInstrumentPolynomial() != null) {
			write(out, 0, network, station, channel.getLocationCode(), channel.getCode(), channel.getStartDate(), channel.getEndDate(), (Polynomial)response.getInstrumentPolynomial());
		}

		out.flush();
	}

	private static void writeBox(PrintWriter out, String title, String network, String station, String location, String channel, Date startTime, Date endTime) {
		out.println("#");
		out.println("#                  +-----------------------------------+");
		String s = String.format("%-4s%-7s%-4s", network, station, location) + channel;
		out.println("#" + String.format("%19s", "|") + center(title, 35) + "|");
		out.println("#" + String.format("%19s", "|") + center(s, 35) + "|");
		String end = formatDate(endTime, "MM/dd/yyyy");
		String line = "#                  |     " + formatDate(startTime, "MM/dd/yyyy") + " to " + end + "      |";
		if (end == null) {
			end = NO_ENDING_TIME;
			line = "#                  |    " + formatDate(startTime, "MM/dd/yyyy") + " to " + end + "   |";
		}

		out.println(line);
		out.println("#                  +-----------------------------------+");
		out.println("#");
	}

	private static String center(String s, int size) {
		if (s != null && size != 0 && s.length() < size) {
			for(int var2 = (size - s.length()) / 2; var2-- > 0; s = " " + s + " ") {
			}

			if ((size - s.length()) % 2 > 0) {
				s = s + " ";
			}

			return s;
		} else {
			return s;
		}
	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, PolesZeros pzs) {
		writeBox(out, "Response (Poles and Zeros)", network, station, location, channel, startTime, endTime);
		String fType = "";
		if ("LAPLACE (RADIANS/SECOND)".equals(pzs.getPzTransferFunctionType())) {
			fType = "A - Laplace transform analog response, in rad/sec";
		} else if ("LAPLACE (HERTZ)".equals(pzs.getPzTransferFunctionType())) {
			fType = "B - Analog response, in Hz";
		} else if ("DIGITAL (Z-TRANSFORM)".equals(pzs.getPzTransferFunctionType())) {
			fType = "D - Digital (Z - transform)";
		} else {
			fType = "Undefined";
		}

		out.println("B053F03     Transfer function type:                " + fType);
		out.println("B053F04     Stage sequence number:                 " + stage);
		out.println("B053F05     Response in units lookup:              " + pzs.getInputUnits().getName() + " - " + pzs.getInputUnits().getDescription());
		out.println("B053F06     Response out units lookup:             " + pzs.getOutputUnits().getName() + " - " + pzs.getOutputUnits().getDescription());
		out.println("B053F07     A0 normalization factor:               " + formatScientific("0.00000E00", "+", pzs.getNormalizationFactor()));
		out.println("B053F08     Normalization frequency:               " + formatScientific("0.00000E00", "+", pzs.getNormalizationFrequency()==null?null:pzs.getNormalizationFrequency().getValue()));
		if (pzs.getZero() != null) {
			out.println("B053F09     Number of zeroes:                      " + pzs.getZero().size());
		} else {
			out.println("B053F09     Number of zeroes:                      0");
		}

		if (pzs.getPole() != null) {
			out.println("B053F14     Number of poles:                       " + pzs.getPole().size());
		} else {
			out.println("B053F14     Number of poles:                       0");
		}

		PoleZero pole;
		if (pzs.getZero() != null && pzs.getZero().size() > 0) {
			out.println("#              Complex zeroes:");
			out.println("#              i  real          imag          real_error    imag_error");

			for (PoleZero poleZero : pzs.getZero()) {
				pole = poleZero;
				out.println("B053F10-13" + String.format("%6d", pole.getNumber() == null ? 0 : pole.getNumber().intValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getReal() == null ? null : pole.getReal().getValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getImaginary() == null ? null : pole.getImaginary().getValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getReal() == null ? null : pole.getReal().getMinusError()) + "  " +
						formatScientific("0.00000E00", "+", pole.getImaginary() == null ? null : pole.getImaginary().getMinusError()));
			}
		}

		if (pzs.getPole() != null && pzs.getPole().size() > 0) {
			out.println("#              Complex poles:");
			out.println("#              i  real          imag          real_error    imag_error");

			for (PoleZero poleZero : pzs.getPole()) {
				pole = poleZero;
				out.println("B053F15-18" + String.format("%6d", pole.getNumber() == null ? null : pole.getNumber().intValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getReal() == null ? null : pole.getReal().getValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getImaginary() == null ? null : pole.getImaginary().getValue()) + "  " +
						formatScientific("0.00000E00", "+", pole.getReal() == null ? null : pole.getReal().getMinusError()) + "  " +
						formatScientific("0.00000E00", "+", pole.getImaginary() == null ? null : pole.getImaginary().getMinusError()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, Coefficients coefficients) {
		writeBox(out, "Response (Coefficients)", network, station, location, channel, startTime, endTime);
		String fType = "";
		if ("ANALOG (RADIANS/SECOND)".equals(coefficients.getCfTransferFunctionType())) {
			fType = "A";
		} else if ("ANALOG (HERTZ)".equals(coefficients.getCfTransferFunctionType())) {
			fType = "B";
		} else if ("DIGITAL".equals(coefficients.getCfTransferFunctionType())) {
			fType = "D";
		} else {
			fType = "Undefined";
		}

		out.println("B054F03     Transfer function type:                " + fType);
		out.println("B054F04     Stage sequence number:                 " + stage);
		out.println("B054F05     Response in units lookup:              " + coefficients.getInputUnits().getName() + " - " + coefficients.getInputUnits().getDescription());
		out.println("B054F06     Response out units lookup:             " + coefficients.getOutputUnits().getName() + " - " + coefficients.getOutputUnits().getDescription());
		if (coefficients.getNumerator() != null) {
			out.println("B054F07     " + String.format("%-39s%s", "Number of numerators:", coefficients.getNumerator().size()));
		} else {
			out.println("B054F07     " + String.format("%-39s%s", "Number of numerators:", 0));
		}

		if (coefficients.getDenominator() != null) {
			out.println("B054F10     " + String.format("%-39s%s", "Number of denominators:", coefficients.getDenominator().size()));
		} else {
			out.println("B054F10     " + String.format("%-39s%s", "Number of denominators:", 0));
		}

		int index;
		if (coefficients.getNumerator() != null && !coefficients.getNumerator().isEmpty()) {
			out.println("#              Numerator coefficients:");
			out.println("#              i  coefficient   error");
			index = 0;

			for (Float aFloat : coefficients.getNumerator()) {
				out.println("B054F08-09" + String.format("%6s", index++) + "  " +
						formatScientific("0.00000E00", "+", aFloat==null?null:aFloat.getValue()) + "  " +
						formatScientific("0.00000E00", "+", aFloat==null?null:aFloat.getMinusError()));
			}
		}

		if (coefficients.getDenominator() != null && !coefficients.getDenominator().isEmpty()) {
			out.println("#              Denominator coefficients:");
			out.println("#              i  coefficient   error");
			index = 0;

			for (Float aFloat : coefficients.getDenominator()) {
				out.println("B054F11-12" + String.format("%6s", index++) + "  " +
						formatScientific("0.00000E00", "+", aFloat==null?null:aFloat.getValue()) + "  " +
						formatScientific("0.00000E00", "+", aFloat==null?null:aFloat.getMinusError()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, ResponseList list) {
		writeBox(out, "Response List", network, station, location, channel, startTime, endTime);
		out.println("B055F03     Stage sequence number:                 " + stage);
		out.println("B055F04     Response in units lookup:              " + list.getInputUnits().getName() + " - " + list.getInputUnits().getDescription());
		out.println("B055F05     Response out units lookup:             " + list.getOutputUnits().getName() + " - " + list.getOutputUnits().getDescription());
		if (list.getResponseListElement() != null) {
			out.println("B055F06     " + String.format("%-39s%s", "Number of responses listed:", list.getResponseListElement().size()));
		} else {
			out.println("B055F06     " + String.format("%-39s%s", "Number of responses listed:", 0));
		}

		if (list.getResponseListElement() != null && !list.getResponseListElement().isEmpty()) {
			out.println("#              i  frequency     amplitude     amplitude err phase angle   phase err");
			int index = 1;
			String spaceFormat = "%-4s";
			if (list.getResponseListElement().size() > 999) {
				spaceFormat = "%-5s";
			}

			for (ResponseListElement e : list.getResponseListElement()) {
				out.println("B055F07-11     " + String.format(spaceFormat, index++) + formatScientific("0.00000E00", "+", e.getFrequency().getValue()) + "  " + formatScientific("0.00000E00", "+", e.getAmplitude().getValue()) + "  " + formatScientific("0.00000E00", "+", e.getAmplitude().getMinusError()) + "  " + formatScientific("0.00000E00", "+", e.getPhase().getValue()) + "  " + formatScientific("0.00000E00", "+", e.getPhase().getMinusError()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, Decimation decimation) {
		writeBox(out, "Decimation", network, station, location, channel, startTime, endTime);
		out.println("B057F03     Stage sequence number:                  " + stage);
		out.println("B057F04     Input sample rate (HZ):                 " + formatScientific("0.0000E00", "", decimation.getInputSampleRate().getValue()));
		out.println("B057F05     Decimation factor:                      " + String.format("%05d", decimation.getFactor().intValue()));
		out.println("B057F06     Decimation offset:                      " + String.format("%05d", decimation.getOffset().intValue()));
		out.println("B057F07     Estimated delay (seconds):             " + formatScientific("0.0000E00", "+", decimation.getDelay().getValue()));
		out.println("B057F08     Correction applied (seconds):          " + formatScientific("0.0000E00", "+", decimation.getCorrection().getValue()));
	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, Gain gain) {
		writeBox(out, " Channel Sensitivity/Gain", network, station, location, channel, startTime, endTime);
		out.println("B058F03     Stage sequence number:                 " + stage);
		out.println("B058F04     Sensitivity:                           " + formatScientific("0.00000E00", "+", gain.getValue()));
		out.println("B058F05     Frequency of sensitivity:              " + formatScientific("0.00000E00", "+", gain.getFrequency()));
		out.println("B058F06     Number of calibrations:                0");
	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, Sensitivity gain) {
		if (gain.getFrequency() != null || gain.getValue() != null) {
			writeBox(out, " Channel Sensitivity/Gain", network, station, location, channel, startTime, endTime);
			out.println("B058F03     Stage sequence number:                 " + stage);
			out.println("B058F04     Sensitivity:                           " + formatScientific("0.00000E00", "+", gain.getValue()));
			out.println("B058F05     Frequency of sensitivity:              " + formatScientific("0.00000E00", "+", gain.getFrequency()));
			out.println("B058F06     Number of calibrations:                0");
		}
	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, FIR fir) {
		writeBox(out, "FIR Response", network, station, location, channel, startTime, endTime);
		out.println("B061F03     Stage sequence number:                 " + stage);
		out.println("B061F04     Response Name:                         " + fir.getName());
		String symmetry = "";
		if ("EVEN".equals(fir.getSymmetry())) {
			symmetry = "C";
		} else if ("ODD".equals(fir.getSymmetry())) {
			symmetry = "B";
		} else {
			symmetry = "A";
		}

		out.println("B061F05     Symmetry Code:                         " + symmetry);
		out.println("B061F06     Response in units lookup:              " + fir.getInputUnits().getName() + " - " + fir.getInputUnits().getDescription());
		out.println("B061F07     Response out units lookup:             " + fir.getOutputUnits().getName() + " - " + fir.getOutputUnits().getDescription());
		if (fir.getNumeratorCoefficient() != null) {
			out.println("B061F08     " + String.format("%-39s%s", "Number of Coefficients:", fir.getNumeratorCoefficient().size()));
		} else {
			out.println("B061F08     " + String.format("%-39s%s", "Number of Coefficients:", 0));
		}

		if (fir.getNumeratorCoefficient() != null && !fir.getNumeratorCoefficient().isEmpty()) {
			out.println("#              i  FIR Coefficient");
			int index = 0;

			for (NumeratorCoefficient f : fir.getNumeratorCoefficient()) {
				out.println("B061F09" + String.format("%6d", index++) + "  " + formatScientific("0.00000E00", "+", f.getValue()));
			}
		}

	}

	public static void write(PrintWriter out, int stage, String network, String station, String location, String channel, Date startTime, Date endTime, Polynomial polynomial) {
		writeBox(out, "Response (Polynomial)", network, station, location, channel, startTime, endTime);
		out.println("B062F03     Transfer function type:                P");
		out.println("B062F04     Stage sequence number:                 " + stage);
		out.println("B062F05     Response in units lookup:              " + polynomial.getInputUnits().getName() + " - " + polynomial.getInputUnits().getDescription());
		out.println("B062F06     Response out units lookup:             " + polynomial.getOutputUnits().getName() + " - " + polynomial.getOutputUnits().getDescription());
		String approximationType = "M";
		if (polynomial.getApproximationType() != null) {
			String temp = polynomial.getApproximationType();
			if ("MACLAURIN".equals(temp)) {
				approximationType = "M";
			}
		}

		out.println("B062F07     Polynomial Approximation Type:         " + approximationType);
		out.println("B062F08     Valid Frequency Units:                 B");
		out.println("B062F09     " + String.format("%-39s%s", "Lower Valid Frequency Bound:", formatScientific("0.00000E00", "+", polynomial.getFrequencyLowerBound().getValue())));
		out.println("B062F10     " + String.format("%-39s%s", "Upper Valid Frequency Bound:", formatScientific("0.00000E00", "+", polynomial.getFrequencyUpperBound().getValue())));
		out.println("B062F11     " + String.format("%-39s%s", "Lower Bound of Approximation:", formatScientific("0.00000E00", "+", polynomial.getApproximationLowerBound().doubleValue())));
		out.println("B062F12     " + String.format("%-39s%s", "Upper Bound of Approximation:", formatScientific("0.00000E00", "+", polynomial.getApproximationUpperBound().doubleValue())));
		out.println("B062F13     " + String.format("%-39s%s", "Maximum Absolute Error:", formatScientific("0.00000E00", "+", polynomial.getMaximumError().doubleValue())));
		if (polynomial.getCoefficient() != null) {
			out.println("B062F14     " + String.format("%-39s%s", "Number of Coefficients:", polynomial.getCoefficient().size()));
		} else {
			out.println("B062F14     " + String.format("%-39s%s", "Number of Coefficients:", 0));
		}

		if (polynomial.getCoefficient() != null && !polynomial.getCoefficient().isEmpty()) {
			out.println("#              Polynomial coefficients:");
			out.println("#              i, coefficient,  error");

			for (Coefficient f : polynomial.getCoefficient()) {
				out.println("B062F15-16     " + String.format("%-4s", f.getNumber().intValue()) + formatScientific("0.00000E00", "+", f.getValue()) + "  " + formatScientific("0.00000E00", "+", f.getMinusError()));
			}
		}

	}

	public static List<Station> parse(String text) {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		String line = null;
		ArrayList<Station> list = new ArrayList<>();

		try {
			Station station = null;
			Network network = null;
			Channel channel = null;

			while(true) {
				String[] array;
				do {
					while(true) {
						do {
							do {
								if ((line = reader.readLine()) == null) {
									return list;
								}
							} while(line.length() <= 0);
						} while(line.startsWith("#"));

						array = line.split("\\s+");
						String type = array[0];
						if ("B050F03".equals(type)) {
							break;
						}

						if ("B050F16".equals(type)) {
							if (network == null || !network.getCode().equals(array[2])) {
								network = new Network();
								network.setCode(array[2]);
							}
						} else if ("B052F03".equals(type)) {
							channel = new Channel();
							channel.setLocationCode(array[2]);
							line = reader.readLine();
							array = line.split("\\s+");
							if (!array[0].equals("B052F04")) {
								throw new IOException("Invalid format expected B052F04 but was " + array[0]);
							}

							channel.setCode(array[2]);
							station.addChannel(channel);
						} else if ("B052F22".equals(type)) {
							try {
								channel.setStartDate(readDate(array[3]));
							} catch (ParseException var11) {
								throw new IOException(var11);
							}
						} else if ("B052F23".equals(type)) {
							try {
								if (line.contains(NO_ENDING_TIME)) {
									channel.setEndDate((Date)null);
								} else {
									channel.setEndDate(readDate(array[3]));
								}
							} catch (ParseException var10) {
								throw new IOException(var10);
							}
						}
					}
				} while(station != null && station.getCode().equals(array[2]));

				station = new Station();
				station.setCode(array[2]);
				station.setNetwork(network);
				list.add(station);
			}
		} catch (IOException var12) {
			var12.printStackTrace();
			return list;
		}
	}

	private static class ScientificDecimalFormat extends NumberFormat {
		private static final long serialVersionUID = 1838009550163390117L;
		private final DecimalFormat decimal;

		public ScientificDecimalFormat(String pattern) {
			this.decimal = new DecimalFormat(pattern);
			this.decimal.setRoundingMode(RoundingMode.HALF_UP);
		}

		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			StringBuffer sb = new StringBuffer();
			sb.append(this.modified(number, Math.abs(number) >= 1.0D, this.decimal.format(number, toAppendTo, pos).toString()));
			return sb;
		}

		private String modified(double num, boolean large, String s) {
			if (large) {
				return s.replace("E", "E+");
			} else {
				return num == 0.0D ? s.replace("E", "E+") : s;
			}
		}

		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			StringBuffer sb = new StringBuffer();
			sb.append(this.modified((double)number, true, this.decimal.format(number, toAppendTo, pos).toString()));
			return sb;
		}

		public Number parse(String source, ParsePosition parsePosition) {
			return this.decimal.parse(source, parsePosition);
		}

		public void setPositivePrefix(String newValue) {
			this.decimal.setPositivePrefix(newValue);
		}
	}
}
