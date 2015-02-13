# logback-snmp
Logback appender which sends snmp traps to openNMS

Logback SNMP appender sends events to OpenNMS when error is logged using logback in the application.

Logback Sample configuration

```xml
     <?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="SNMP" class="com.sathwik.logback.appender.SNMPTrapAppender">
        <!--properties-->
        <community>public</community>
        <enterpriseId>1.3.6.1.4.1.16625.1.12.5</enterpriseId>
        <host>localhost</host>
        <port>162</port>
        <specificTrapType>1</specificTrapType>
        <!-- Major, warning etc-->
        <priority>Critical</priority>
        <escalation>escalate immediate</escalation>

        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>

        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>error</level>
        </filter>
    </appender>
    <root level="info">
        <appender-ref ref="SNMP"/>
    </root>

</configuration>
```