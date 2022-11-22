package edu.iris.dmc.service;

import edu.iris.dmc.criteria.Criteria;
import edu.iris.dmc.criteria.CriteriaException;
import edu.iris.dmc.ws.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RespService extends BaseService {
    private static final Logger logger = Logger.getLogger(RespService.class.getName());

    public RespService(String baseUrl, String version, String compatabilityVersion, String userAgent) {
        super(version, compatabilityVersion, userAgent);
        this.baseUrl = baseUrl;
    }

    public String fetch(String url) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(this.getClass().getName(), "fetch(String url)", new Object[]{url});
        }

        int i = url.indexOf(63);
        if (i < 0) {
            throw new CriteriaException("Incomplete Url...");
        } else {
            HttpURLConnection connection = null;
            try {
                String searchURL = url.substring(url.indexOf(63) + 1);
                connection = this.getConnection(this.baseUrl + "query?" + searchURL);
                connection.setRequestMethod("GET");
                String uAgent = this.userAgent;
                if (this.getAppName() != null && !"".equals(this.appName)) {
                    uAgent = uAgent + " (" + this.appName + ")";
                }

                connection.setRequestProperty("User-Agent", uAgent);
                connection.setRequestProperty("Accept", "application/xml");
                connection.connect();
                int responseCode = connection.getResponseCode();

                try (InputStream inputStream = responseCode != 200 ? connection.getErrorStream() : connection.getInputStream()) {
                    switch (responseCode) {
                        case 200:
                            String message = StringUtil.toString(inputStream);
                            if (logger.isLoggable(Level.FINER)) {
                                logger.exiting(this.getClass().getName(), "fetch(StationCriteria criteria, OutputLevel level)");
                            }

                            String var10 = message;
                            return var10;
                        case 400:
                            if (logger.isLoggable(Level.SEVERE)) {
                                logger.severe("An error occurred while making a GET request " + this.baseUrl + " " + searchURL + StringUtil.toString(inputStream));
                            }

                            throw new CriteriaException("Bad request parameter: " + StringUtil.toString(inputStream));
                        case 402:
                        case 404:
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.warning("No data Found for the GET request " + this.baseUrl + " " + searchURL + StringUtil.toString(inputStream));
                            }

                            throw new NoDataFoundException("No data found for: " + searchURL);
                        case 429:
                            if (logger.isLoggable(Level.SEVERE)) {
                                logger.severe("Too Many Requests");
                            }

                            throw new IOException("Too Many Requests");
                        case 500:
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.severe("An error occurred while making a GET request " + this.baseUrl + " " + searchURL + StringUtil.toString(inputStream));
                            }

                            throw new IOException(StringUtil.toString(inputStream));
                        default:
                            throw new IOException(connection.getResponseMessage());
                    }
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public String fetch(Criteria criteria) throws NoDataFoundException, CriteriaException, IOException, ServiceNotSupportedException {
        if (logger.isLoggable(Level.FINER)) {
            logger.entering(this.getClass().getName(), "fetch(StationCriteria criteria, OutputLevel level)", new Object[]{criteria});
        }

        return this.fetch(this.baseUrl + "query?" + (String) criteria.toUrlParams().get(0));
    }
}
