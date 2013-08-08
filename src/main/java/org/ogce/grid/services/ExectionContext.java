package org.ogce.grid.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.ogce.grid.utils.ServiceConstants;

public class ExectionContext {

    private String rsl;
    private String host;

    public ExectionContext(String resourceName) throws IOException {
        loadConfigration(resourceName);
    }

    private void loadConfigration(String resourceName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertyStream = classLoader.getResourceAsStream(ServiceConstants.GRIDCLIENT_PROPERTY);
        Properties properties = new Properties();
        if (propertyStream != null) {
            properties.load(propertyStream);
            String rsl = properties.getProperty(resourceName + ".rsl");
            if (rsl == null) {
                throw new IOException("Failed to find RSL for " + resourceName + ".rsl");
            } else {
                this.rsl = rsl;
            }
            String host = properties.getProperty(resourceName + ".gram.epr");
            if (host == null) {
                throw new IOException("Failed to find GRAM EPR property for " + resourceName + ".gram.epr");
            } else {
                this.rsl = rsl;
            }
        }
    }

    public String getHost() {
        return host;
    }

    /**
     * @param host
     *  the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    public String getRSL() {
        return rsl;
    }

    public void setRSL(String rsl) {
        this.rsl = rsl;

    }
}
