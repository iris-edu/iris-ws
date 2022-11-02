//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.iris.dmc.service;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.sacpz.model.NumberUnit;
import edu.iris.dmc.sacpz.model.Pole;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.sacpz.model.Zero;
import edu.iris.dmc.ws.util.DateUtil;
import edu.iris.dmc.ws.util.StringUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SacpzService extends BaseService {
    private static final Logger logger = Logger.getLogger(SacpzService.class.getName());

    public SacpzService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
        super(version, compatabilityVersion, userAgent);
        this.baseUrl = baseUrl;
    }

    public List<Sacpz> toSacpz(InputStream inputStream) throws DataFormatException, IOException {
        List<Sacpz> list = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            Throwable var4 = null;

            try {
                String line = null;

                while((line = br.readLine()) != null) {
                    if (line.trim().length() != 0) {
                        Sacpz sacpz = new Sacpz();
                        String[] components;
                        if (line.startsWith("*")) {
                            if (line.endsWith("*")) {
                                continue;
                            }

                            components = line.split(":");
                            String temp;
                            if (components.length == 2) {
                                temp = components[1].trim();
                                sacpz.setNetwork(temp);
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setStation(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setLocation(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components != null && components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setChannel(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    if (!temp.isEmpty()) {
                                        sacpz.setCreated(this.parse(temp));
                                    }
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setStartTime(this.parse(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setEndTime(this.parse(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setDescription(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setLatitude(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setLongitude(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setElevation(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setDepth(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setInclination(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setAzimuth(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setSampleRate(this.parseDouble(temp));
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setInputUnit(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setOutputUnit(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setInstrumentType(temp);
                                }
                            }

                            Double value;
                            String[] theStrings;
                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    theStrings = components[1].trim().split(" ");
                                    if (theStrings.length == 2) {
                                        value = this.parseDouble(theStrings[0]);
                                        sacpz.setInstrumentGain(new NumberUnit(theStrings[1], value));
                                    } else {
                                        value = this.parseDouble(theStrings[0]);
                                        sacpz.setInstrumentGain(new NumberUnit("", value));
                                    }
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setComment(temp);
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    theStrings = components[1].trim().split(" ");
                                    if (theStrings.length == 2) {
                                        value = this.parseDouble(theStrings[0]);
                                        sacpz.setSensitivity(new NumberUnit(theStrings[1], value));
                                    } else {
                                        value = this.parseDouble(theStrings[0]);
                                        sacpz.setSensitivity(new NumberUnit("", value));
                                    }
                                }
                            }

                            if ((line = br.readLine()) != null) {
                                components = line.split(":");
                                if (components.length == 2) {
                                    temp = components[1].trim();
                                    sacpz.setA0(this.parseDouble(temp));
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
                            throw new DataFormatException("Unable to parse data: Zeros are not found where expected");
                        }

                        components = line.split("\t");
                        int count = Integer.parseInt(components[1]);
                        List<Zero> zeros = new ArrayList<>();

                        for(int i = 0; i < count; ++i) {
                            line = br.readLine();
                            line = line.trim();
                            String[] lineComponents = line.split("\t");
                            Double real = this.parseDouble(lineComponents[0]);
                            Double imaginary = this.parseDouble(lineComponents[1]);
                            Zero zero = new Zero(real, imaginary);
                            zeros.add(zero);
                        }

                        sacpz.setZeros(zeros);
                        line = br.readLine();
                        if (line == null || !line.startsWith("POLES")) {
                            throw new DataFormatException("Unable to parse data: Poles are not found where expected");
                        }

                        line = line.trim();
                        components = line.split("\t");
                        count = Integer.parseInt(components[1]);
                        List<Pole> poles = new ArrayList<>();

                        for(int i = 0; i < count; ++i) {
                            line = br.readLine();
                            line = line.trim();
                            String[] lineComponents = line.split("\t");
                            Double real = this.parseDouble(lineComponents[0]);
                            Double imaginary = this.parseDouble(lineComponents[1]);
                            Pole pole = new Pole(real, imaginary);
                            poles.add(pole);
                        }

                        sacpz.setPoles(poles);
                        line = br.readLine();
                        if (line != null && line.startsWith("CONSTANT")) {
                            line = line.trim();
                            components = line.split("\t");
                            Double constant = this.parseDouble(components[1]);
                            sacpz.setConstant(constant);
                        }

                        list.add(sacpz);
                    }
                }
            } catch (Throwable var24) {
                var4 = var24;
                throw var24;
            } finally {
                if (br != null) {
                    if (var4 != null) {
                        try {
                            br.close();
                        } catch (Throwable var23) {
                            var4.addSuppressed(var23);
                        }
                    } else {
                        br.close();
                    }
                }

            }

            return list;
        } catch (ParseException e) {
            throw new DataFormatException(e.getMessage());
        }
    }

    public List<Sacpz> fetch(SacpzCriteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, DataFormatException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(this.getClass().getName(), "fetch(SacpzCriteria criteria)", new Object[]{criteria});
        }

        HttpURLConnection connection = null;
        String paramsString = null;
        paramsString = (String)criteria.toUrlParams().get(0);
        connection = this.getConnection(this.baseUrl + "query?" + paramsString);
        connection.setRequestProperty("Accept", "application/xml");
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();

        try {
            InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream();
            Throwable var6 = null;

            try {
                Object var7;
                try {
                    switch(responseCode) {
                        case 200:
                            var7 = this.toSacpz(inputStream);
                            return (List)var7;
                        case 400:
                            throw new CriteriaException("Bad request parameter: " + criteria);
                        case 404:
                            throw new NoDataFoundException("No data found for: " + criteria.toString());
                        case 429:
                            if (logger.isLoggable(Level.SEVERE)) {
                                logger.severe("Too Many Requests");
                            }

                            throw new IOException("Too Many Requests");
                        case 500:
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.warning("An error occurred while making a GET request " + StringUtil.toString(inputStream));
                            }

                            throw new IOException("Bad request parameter: " + StringUtil.toString(inputStream));
                        default:
                            throw new IOException(connection.getResponseMessage());
                    }
                } catch (Throwable var25) {
                    var7 = var25;
                    var6 = var25;
                    throw var25;
                }
            } finally {
                if (inputStream != null) {
                    if (var6 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var24) {
                            var6.addSuppressed(var24);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }
        } catch (MalformedURLException | UnsupportedEncodingException var27) {
            throw new IOException(var27.getMessage());
        } finally {
            connection.disconnect();
        }
    }

    private Date parse(String s) throws ParseException {
        return s != null && !s.trim().isEmpty() ? DateUtil.parseAny(s) : null;
    }

    private Double parseDouble(String s) {
        return s != null && !s.trim().isEmpty() ? Double.parseDouble(s) : null;
    }

    public List<Sacpz> load(InputStream inputStream) throws IOException {
        return null;
    }
}
