package edu.iris.dmc.service.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.sacpz.model.NumberUnit;
import edu.iris.dmc.sacpz.model.Pole;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.sacpz.model.Zero;
import edu.iris.dmc.service.ResponseHandler;

public class SacpzResponseHandler implements ResponseHandler<Sacpz> {

	@Override
	public List<Sacpz> handle(InputStream inputStream) throws IOException {
		List<Sacpz> list = new ArrayList<Sacpz>();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");

		while ((line = br.readLine()) != null) {
			if (line.trim().length() == 0) {
				continue;
			}
			Sacpz sacpz = new Sacpz();
			if (line.startsWith("*")) {
				if (line.endsWith("*")) {
					continue;
				} else {
					String[] components = line.split(":");
					if (components != null && components.length == 2) {
						String temp = components[1].trim();
						sacpz.setNetwork(temp);
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setStation(temp);
						}
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setLocation(temp);
						}
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setChannel(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();

							try {
								sacpz.setCreated(sdf.parse(temp));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();

							try {
								sacpz.setStartTime(sdf.parse(temp));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();

							try {
								sacpz.setEndTime(sdf.parse(temp));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setDescription(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setLatitude(Double.parseDouble(temp));
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setLongitude(Double.parseDouble(temp));
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setElevation(Double.parseDouble(temp));
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setDepth(Double.parseDouble(temp));
						}
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setInclination(Double.parseDouble(temp));
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setAzimuth(Double.parseDouble(temp));
						}
					}
					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setSampleRate(Double.parseDouble(temp));
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setInputUnit(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setOutputUnit(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setInstrumentType(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String[] theStrings = components[1].trim().split(" ");
							if (theStrings.length == 2) {
								Double value = Double.parseDouble(theStrings[0]);
								sacpz.setInstrumentGain(new NumberUnit(theStrings[1], value));
							} else {
								try {
									Double value = Double.parseDouble(theStrings[0]);
									sacpz.setInstrumentGain(new NumberUnit("", value));
								} catch (NumberFormatException e) {
								}
							}
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setComment(temp);
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {

							String[] theStrings = components[1].trim().split(" ");
							if (theStrings.length == 2) {
								Double value = Double.parseDouble(theStrings[0]);
								sacpz.setSensitivity(new NumberUnit(theStrings[1], value));
							} else {
								try {
									Double value = Double.parseDouble(theStrings[0]);
									sacpz.setSensitivity(new NumberUnit("", value));
								} catch (NumberFormatException e) {
								}
							}
						}
					}

					if ((line = br.readLine()) != null) {
						components = line.split(":");
						if (components != null && components.length == 2) {
							String temp = components[1].trim();
							sacpz.setA0(Double.parseDouble(temp));
						}
					}

				}
			}

			line = br.readLine();
			if (line == null) {
				break;
			}
			line = br.readLine();
			if (line == null) {
				break;
			}

			if (!line.startsWith("ZEROS")) {
				throw new IOException("Unable to parse data: Zeros are not found where expected");
			}
			String[] components = line.split("\t");
			try {
				int count = Integer.parseInt(components[1]);
				List<Zero> zeros = new ArrayList<Zero>();
				for (int i = 0; i < count; i++) {
					line = br.readLine();
					line = line.trim();
					String[] lineComponents = line.split("\t");
					Double real = Double.parseDouble(lineComponents[0]);
					Double imaginary = Double.parseDouble(lineComponents[1]);
					Zero zero = new Zero(real, imaginary);
					zeros.add(zero);
				}
				sacpz.setZeros(zeros);
			} catch (NumberFormatException e) {
				throw new IOException("Unable to parse data: NumberFormatException");
			}

			line = br.readLine();
			if (line == null || !line.startsWith("POLES")) {
				throw new IOException("Unable to parse data: Poles are not found where expected");
			}

			line = line.trim();
			components = line.split("\t");
			try {
				int count = Integer.parseInt(components[1]);
				List<Pole> poles = new ArrayList<Pole>();
				for (int i = 0; i < count; i++) {
					line = br.readLine();
					line = line.trim();
					String[] lineComponents = line.split("\t");
					Double real = Double.parseDouble(lineComponents[0]);
					Double imaginary = Double.parseDouble(lineComponents[1]);
					Pole pole = new Pole(real, imaginary);
					poles.add(pole);
				}
				sacpz.setPoles(poles);
			} catch (NumberFormatException e) {
				throw new IOException("Unable to parse data: NumberFormatException");
			}

			line = br.readLine();
			if (line == null || !line.startsWith("CONSTANT")) {
				throw new IOException("Unable to parse data: Constant not found where expected");
			}
			line = line.trim();

			components = line.split("\t");
			try {
				Double constant = Double.parseDouble(components[1]);
				sacpz.setConstant(constant);
			} catch (NumberFormatException e) {

			}

			list.add(sacpz);
		}

		if (!list.isEmpty()) {
			return list;
		} else {
			return null;
		}
	}
	@Override
	public String getContentType() {
		return "application/text";
	}
}
