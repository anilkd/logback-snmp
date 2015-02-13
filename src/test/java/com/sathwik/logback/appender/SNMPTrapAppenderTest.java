package com.sathwik.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Assert;
import org.mockito.ArgumentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SNMPTrapAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(SNMPTrapAppenderTest.class);

    @org.junit.Test
    public void testAppender() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        Appender mockAppender = mock(Appender.class);
        root.addAppender(mockAppender);
        Appender<ILoggingEvent> appender = root.getAppender("SNMP");
        LOG.info("server error", new RuntimeException("info message"));
        LOG.error("server error", new RuntimeException("error message"));

        verify(mockAppender,times(2)).doAppend(argThat(new ArgumentMatcher<ILoggingEvent>() {
            @Override
            public boolean matches(final Object argument) {
                return ((LoggingEvent) argument).getFormattedMessage().contains("server error");
            }
        }));
        Assert.assertThat(appender.getClass().getName(), is(equalTo("com.sathwik.logback.appender.SNMPTrapAppender")));


    }
}