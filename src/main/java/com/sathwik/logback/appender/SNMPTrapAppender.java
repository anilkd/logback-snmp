package com.sathwik.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * @author anil.dhulipalla
 */
public class SNMPTrapAppender extends AppenderBase<ILoggingEvent> {
    private SNMPService snmpService;
    private SNMPConfig snmpConfig;

    private String community;
    private String enterpriseId;
    private String host;
    private String port;
    private String specificTrapType;
    private String priority;
    private String escalation;

    public SNMPTrapAppender() {

    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {

        if (null == snmpConfig && null == snmpService) {
            snmpConfig = new SNMPConfig();
            snmpConfig.setCommunity(community);
            snmpConfig.setEnterpriseId(enterpriseId);
            snmpConfig.setHost(host);
            snmpConfig.setPort(port);
            snmpConfig.setSpecificTrapType(specificTrapType);
            snmpConfig.setSystemName(getUserName());
            snmpService = new SNMPServiceImpl(snmpConfig);
        }


        StringBuffer sbuf = new StringBuffer(128);
        sbuf.append(iLoggingEvent.getTimeStamp() - iLoggingEvent.getLoggerContextVO().getBirthTime())
                .append(" ")
                .append(iLoggingEvent.getLevel())
                .append(" [")
                .append(iLoggingEvent.getThreadName())
                .append("] ")
                .append(iLoggingEvent.getLoggerName())
                .append(" - ")
                .append(iLoggingEvent.getFormattedMessage());

        snmpService.sendEvent(iLoggingEvent.getMessage(), sbuf.toString(), priority, escalation);
    }

    private String getUserName() {
        String username = System.getProperty("user.name");
        if (username != null) {
            return username + ": ";
        }
        return "";//test
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSpecificTrapType(String specificTrapType) {
        this.specificTrapType = specificTrapType;
    }


    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setEscalation(String escalation) {
        this.escalation = escalation;
    }
}
