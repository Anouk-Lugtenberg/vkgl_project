package org.molgenis.vkgl.CLI;


import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.rules.ExternalResource;

import java.io.CharArrayWriter;

/**
 * JUnit rule for testing output to Log4j.
 * https://www.dontpanicblog.co.uk/2018/04/29/test-log4j2-with-junit/
 */
public class LogAppenderResource extends ExternalResource {
    private static final String APPENDER_NAME = "log4jRuleAppender";

    private static final String PATTERN = "%-5level %msg";

    private Logger LOGGER;
    private Appender appender;
    private final CharArrayWriter outContent = new CharArrayWriter();

    public LogAppenderResource(org.apache.logging.log4j.Logger logger) {
        this.LOGGER = (org.apache.logging.log4j.core.Logger)logger;
    }

    @Override
    protected void before() {
        StringLayout layout = PatternLayout.newBuilder().withPattern(PATTERN).build();
        appender = WriterAppender.newBuilder()
                .setTarget(outContent)
                .setLayout(layout)
                .setName(APPENDER_NAME).build();
        appender.start();
        LOGGER.addAppender(appender);
    }

    @Override
    protected void after() {
        LOGGER.removeAppender(appender);
    }

    public String getOutput() {
        return outContent.toString();
    }

}
