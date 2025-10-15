package com.c4h.solutionsupport.util;

import java.io.IOException;
import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {

    private final StringBuilder buffer = new StringBuilder();
    private final String logLevel;
    private final ILogger logger;

    public LoggerOutputStream(ILogger logger, String logLevel) {
        this.logger = logger;
        this.logLevel = logLevel;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        if (c == '\n' || c == '\r') {
            if (buffer.length() > 0) {
                flushBuffer();
            }
        } else {
            buffer.append(c);
        }
    }

    private void flushBuffer() {
        String message = buffer.toString();
        buffer.setLength(0); // reset buffer

        switch (logLevel) {
            case "INFO":
                logger.logInfo(message);
                break;
            case "WARN":
                logger.logWarn(message, null);
                break;
            case "ERROR":
                logger.logError(message, null);
                break;
            default:
                logger.logInfo(message);
                break;
        }
    }

    @Override
    public void flush() throws IOException {
        if (buffer.length() > 0) {
            flushBuffer();
        }
    }
}
