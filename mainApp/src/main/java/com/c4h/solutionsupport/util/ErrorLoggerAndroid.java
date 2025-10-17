package com.c4h.solutionsupport.util;

public class ErrorLoggerAndroid implements ILogger {

    private final boolean androidAvailable;
    private final Class<?> logClass;

    public ErrorLoggerAndroid() {
        boolean available = false;
        Class<?> clazz = null;
        try {
            clazz = Class.forName("android.util.Log");
            available = true;
        } catch (ClassNotFoundException e) {
            // Android Log-Klasse nicht verfügbar – fallback
        }
        this.androidAvailable = available;
        this.logClass = clazz;
    }

    @Override
    public void logInfo(String message) {
        log("i", message, null);
    }

    @Override
    public void logWarn(String message, Throwable t) {
        log("w", message, t);
    }

    @Override
    public void logError(String message, Throwable t) {
        log("e", message, t);
    }

    private void log(String level, String message, Throwable t) {
        if (!androidAvailable || logClass == null) {
            System.out.println("[ANDROID-LOG-SIM] " + level.toUpperCase() + ": " + message);
            if (t != null) t.printStackTrace();
            return;
        }

        try {
            if (t != null) {
                logClass.getMethod(level, String.class, String.class, Throwable.class)
                        .invoke(null, "C4HApp", message, t);
            } else {
                logClass.getMethod(level, String.class, String.class)
                        .invoke(null, "C4HApp", message);
            }
        } catch (Exception e) {
            System.err.println("[LOGGING ERROR] " + message);
            if (t != null) t.printStackTrace();
        }
    }
}
