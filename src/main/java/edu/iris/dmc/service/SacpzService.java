package edu.iris.dmc.service;

import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.criteria.SacpzCriteria;
import edu.iris.dmc.sacpz.SacpzReader;
import edu.iris.dmc.sacpz.model.Sacpz;
import edu.iris.dmc.ws.util.DateUtil;
import edu.iris.dmc.ws.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public List<Sacpz> toSacpz(InputStream inputStream) throws IOException {
        List<Sacpz> list = new ArrayList<>();

        try (SacpzReader reader = new SacpzReader(inputStream)) {
            while (true) {
                Sacpz record = reader.readRecord();
                if (record == null) {
                    break;
                }
                list.add(record);
            }
            return list;
        }
    }

    public List<Sacpz> fetch(SacpzCriteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException, DataFormatException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(this.getClass().getName(), "fetch(SacpzCriteria criteria)", new Object[]{criteria});
        }

        String paramsString = criteria.toUrlParams().get(0).replace(' ', '-');

        String url = this.baseUrl + "query?" + paramsString+"&format=sacpz";
        HttpURLConnection connection = this.getConnection(url);
        connection.setRequestProperty("Accept", "application/xml");
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();

        try (InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream()) {
            switch (responseCode) {
                case 200:
                    return this.toSacpz(inputStream);
                case 400:
                    throw new CriteriaException("Bad request parameter: " + criteria);
                case 404:
                    throw new NoDataFoundException("No data found for: " + criteria);
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
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new IOException(e.getMessage());
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
