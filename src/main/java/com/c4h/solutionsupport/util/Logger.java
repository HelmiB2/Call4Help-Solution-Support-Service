package com.c4h.solutionsupport.util;

public class Logger {

    private static ILogger logger;

    static {
        // Plattform erkennen über PlatformHelper
        if (PlatformHelper.isAndroid()) {
            logger = new ErrorLoggerAndroid();
        } else {
            logger = new ErrorLoggerDesktop();
        }
    }

    // Setter für dynamisches Setzen eines anderen Loggers
    public static void setLogger(ILogger newLogger) {
        if (newLogger != null) {
            logger = newLogger;
        }
    }

    public static void info(String msg) {
        logger.logInfo(msg);
    }

    public static void warn(String msg, Throwable t) {
        logger.logWarn(msg, t);
    }

    public static void error(String msg, Throwable t) {
        logger.logError(msg, t);
    }
}
