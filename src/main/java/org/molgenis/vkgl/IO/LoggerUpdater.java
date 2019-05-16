package org.molgenis.vkgl.IO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;

import java.io.Serializable;

public class LoggerUpdater {
    public static void updateLogger(String fileName, String appenderName) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        Layout<? extends Serializable> oldLayout = configuration.getAppender(appenderName).getLayout();

        //delete old appender/logger
        configuration.getRootLogger().removeAppender(appenderName);

        FileAppender appender = FileAppender.newBuilder()
                .setName(appenderName)
                .setLayout(oldLayout)
                .setConfiguration(configuration)
                .withFileName(fileName)
                .build();
        appender.start();
        configuration.addAppender(appender);
        context.updateLoggers();
    }
}
