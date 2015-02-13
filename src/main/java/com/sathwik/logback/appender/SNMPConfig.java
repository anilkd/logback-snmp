package com.sathwik.logback.appender;

/**
 * @author anil.dhulipalla
 */
public class SNMPConfig {
    private String community;
    private String enterpriseId;
    private String host;
    private String port;
    private String specificTrapType;
    private String systemName;

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSpecificTrapType() {
        return specificTrapType;
    }

    public void setSpecificTrapType(String specificTrapType) {
        this.specificTrapType = specificTrapType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
