package com.c4h.solutionsupport.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLoggerDesktop implements ILogger {
    private static final String LOG_DIR = System.getProperty("user.home") + File.separator + ".c4h" + File.separator + "logs";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String getLogFilePath() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LOG_DIR + File.separator + "errors_" + date + ".log";
    }

    private synchronized void log(String level, String message, Throwable throwable) {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (FileWriter fw = new FileWriter(getLogFilePath(), true);
                 PrintWriter pw = new PrintWriter(fw)) {

                pw.print("[" + LocalDateTime.now().format(formatter) + "] ");
                pw.print(level + ": ");
                pw.print(message);
                pw.println(" [Thread: " + Thread.currentThread().getName() + "]");

                if (throwable != null) {
                    pw.println(throwable.toString());
                    for (StackTraceElement ste : throwable.getStackTrace()) {
                        pw.println("\tat " + ste.toString());
                    }
                }

                pw.println(); // Leerzeile zur Trennung
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logInfo(String message) {
        log("INFO", message, null);
    }

    @Override
    public void logWarn(String message, Throwable t) {
        log("WARN", message, t);
    }

    @Override
    public void logError(String message, Throwable t) {
        log("ERROR", message, t);
    }
}
