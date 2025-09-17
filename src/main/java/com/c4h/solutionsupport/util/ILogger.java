package com.c4h.solutionsupport.util;

public interface ILogger {
    void logInfo(String message);
    void logWarn(String message, Throwable t);
    void logError(String message, Throwable t);
}
