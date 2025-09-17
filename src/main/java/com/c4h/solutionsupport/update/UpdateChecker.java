package com.c4h.solutionsupport.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.json.JSONObject;

import com.c4h.solutionsupport.util.Logger;

import javafx.concurrent.Task;

public class UpdateChecker {

    private static final String UPDATE_URL = "https://fehlermeldung.3s-hamburg.de/c4hupdate/update.json";
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"),".c4h", "update", "config.properties"
    );
    public interface ProgressCallback {
        void onProgress(int percent);
    }

    private static String getCurrentVersion() throws IOException {
        Properties props = new Properties();

        // falls noch nicht vorhanden, vom App-Pfad kopieren
        if (!Files.exists(CONFIG_PATH)) {
            Path appConfig = Paths.get(System.getProperty("user.dir"), "update", "config.properties");
            if (Files.exists(appConfig)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                Files.copy(appConfig, CONFIG_PATH);
            } else {
                Logger.error("config.properties nicht gefunden im App-Verzeichnis!", null);
                return "0.0.0";
            }
        }

        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            props.load(in);
        }
        Logger.info("Aktuelle Version: "+ props.getProperty("current.version", "0.0.0"));
        return props.getProperty("current.version", "0.0.0");
    }

    public static void checkForUpdates() {
        Logger.info("Starte Update-Check...");
        try {
            String jsonText = readUrl(UPDATE_URL);
            if (jsonText == null) {
                Logger.error("Update-JSON konnte nicht geladen werden!", null);
                return;
            }

            JSONObject updateInfo = new JSONObject(jsonText);
            String latestVersion = updateInfo.getString("version");
            String downloadUrl = updateInfo.getString("downloadUrl");
            String currentVersion = getCurrentVersion();

            Logger.info("Gefundene Version online: " + latestVersion + " | Lokal: " + currentVersion);

            if (isNewerVersion(latestVersion, currentVersion)) {
                Logger.info("Neue Version verfügbar: " + latestVersion);

                Path downloadedFile = downloadFile(downloadUrl, "c4h_update.exe").toPath();
                Logger.info("Datei erfolgreich heruntergeladen: " + downloadedFile.toAbsolutePath());

                ProcessBuilder pb = new ProcessBuilder(
                        "cmd", "/c", "start", "\"\"", downloadedFile.toAbsolutePath().toString()
                );
                pb.start();
                Logger.info("Update-Prozess gestartet: " + downloadedFile.toAbsolutePath());
            } else {
                Logger.info("Keine neue Version verfügbar.");
            }
        } catch (Exception e) {
            Logger.error("Fehler beim Update-Check", e);
        }
    }

    private static String readUrl(String urlString) {
        Logger.info("Lese URL: " + urlString);
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                Logger.info("Erfolgreich von URL gelesen.");
                return sb.toString();
            }
        } catch (URISyntaxException | IOException e) {
            Logger.error("Fehler beim Lesen der URL: " + urlString, e);
            return null;
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        Logger.info("Vergleiche Versionen: latest=" + latest + ", current=" + current);
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");
        for (int i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
            int l = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            if (l > c) return true;
            if (l < c) return false;
        }
        return false;
    }

    public static File downloadFile(String urlString, String fileName, ProgressCallback callback) {
        Logger.info("Starte Download von: " + urlString);
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            File targetFile = new File(System.getProperty("user.home") 
            		+ File.separator + ".c4h" + File.separator + "update",
            	    fileName);
            Logger.info("Speichere Datei unter: " + targetFile.getAbsolutePath());

            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                long contentLength = url.openConnection().getContentLengthLong();
                if (contentLength <= 0) {
                    Logger.info("Dateigröße unbekannt.");
                    contentLength = -1;
                } else {
                    Logger.info("Dateigröße: " + (contentLength / 1024) + " KB");
                }

                int lastPercent = -1;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    if (contentLength > 0) {
                        int percent = (int) (totalRead * 100 / contentLength);
                        if (percent != lastPercent) {
                            if (callback != null) {
                                callback.onProgress(percent);
                            }
                       //     Logger.info("Download Fortschritt: " + percent + "%");
                            lastPercent = percent;
                        }
                    }
                }
            }

            Logger.info("Download abgeschlossen: " + targetFile.getAbsolutePath() +
                        " (" + targetFile.length() / 1024 + " KB)");
            return targetFile;
        } catch (Exception e) {
            Logger.error("Fehler beim Herunterladen der Datei: " + urlString, e);
            return null;
        }
    }

    public static File downloadFile(String urlString, String fileName) {
        return downloadFile(urlString, fileName, null);
    }

    public static boolean checkVersion() {
        Logger.info("Prüfe Version...");
        try {
            String jsonText = readUrl(UPDATE_URL);
            if (jsonText == null) {
                Logger.error("Konnte Update-Informationen nicht laden.", null);
                return false;
            }

            JSONObject updateInfo = new JSONObject(jsonText);
            String latestVersion = updateInfo.getString("version");
            String currentVersion = getCurrentVersion();

            boolean newer = isNewerVersion(latestVersion, currentVersion);
            Logger.info("Versionsergebnis: " + newer);
            return newer;

        } catch (Exception e) {
            Logger.error("Fehler beim Versions-Check", e);
            return false;
        }
    }

    public static String getChangelogText() {
        Logger.info("Lese Changelog...");
        try {
            String jsonText = readUrl(UPDATE_URL);
            if (jsonText == null) {
                Logger.error("Konnte Update-Informationen nicht laden.", null);
                return "Konnte Update-Informationen nicht laden.";
            }

            JSONObject updateInfo = new JSONObject(jsonText);
            if (!updateInfo.has("changelog")) {
                Logger.info("Kein Changelog verfügbar.");
                return "Kein Changelog verfügbar.";
            }

            var changelogArray = updateInfo.getJSONArray("changelog");
            StringBuilder sb = new StringBuilder();

            sb.append("=== Changelog Version ").append(updateInfo.getString("version")).append(" ===\n\n");

            for (int i = 0; i < changelogArray.length(); i++) {
                var entry = changelogArray.getJSONObject(i);
                String type = entry.optString("type", "Feature").toUpperCase();
                String desc = entry.optString("description", "");
                sb.append("• [").append(type).append("] ").append(desc).append("\n\n");
            }

            sb.append("=== Ende Changelog ===");

            Logger.info("Changelog erfolgreich gelesen.");
            return sb.toString();

        } catch (Exception e) {
            Logger.error("Fehler beim Auslesen des Changelogs", e);
            return "❌ Fehler beim Laden des Changelogs.";
        }
    }

    public static Task<Path> createUpdateTask() {
        Logger.info("Erstelle Update-Task...");
        return new Task<>() {
            @Override
            protected Path call() throws Exception {
                Logger.info("Update-Task gestartet.");
                String jsonText = readUrl(UPDATE_URL);
                if (jsonText == null) {
                    updateMessage("Fehler: Konnte Update-Informationen nicht laden.");
                    Logger.error("Update-Task konnte JSON nicht laden.", null);
                    return null;
                }

                JSONObject updateInfo = new JSONObject(jsonText);
                String latestVersion = updateInfo.getString("version");
                String downloadUrl = updateInfo.getString("downloadUrl");
                String currentVersion = getCurrentVersion();

                if (!isNewerVersion(latestVersion, currentVersion)) {
                    updateMessage("Keine neue Version verfügbar.");
                    Logger.info("Keine neue Version im Task verfügbar.");
                    return null;
                }

                Logger.info("Neue Version im Task verfügbar: " + latestVersion);

                Path downloadedFile = downloadFileWithProgress(downloadUrl, "c4h_update.exe");

                ProcessBuilder pb = new ProcessBuilder(
                        "cmd", "/c", "start", "\"\"", downloadedFile.toAbsolutePath().toString()
                );
                pb.start();
                Logger.info("Update im Task gestartet: " + downloadedFile.toAbsolutePath());
                return downloadedFile;
            }

            private Path downloadFileWithProgress(String fileUrl, String targetFile) throws IOException {
                Logger.info("Starte Download mit Fortschritt: " + fileUrl);

                URL url = URI.create(fileUrl).toURL();
                URLConnection conn = url.openConnection();
                int fileSize = conn.getContentLength();
                Logger.info("Dateigröße laut Server: " + fileSize / 1024 + " KB");

                Path targetPath = Paths.get(System.getProperty("user.home"),".c4h","update", targetFile);
                try (InputStream in = url.openStream();
                     FileOutputStream out = new FileOutputStream(targetPath.toFile())) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;

                        updateProgress(totalRead, fileSize);
                        updateMessage(String.format("%.2f MB / %.2f MB",
                                totalRead / (1024.0 * 1024),
                                fileSize / (1024.0 * 1024)));

//                        int percent = (int) ((totalRead * 100) / fileSize);
                    }
                }
                Logger.info("Download mit Fortschritt abgeschlossen: " + targetPath.toAbsolutePath());
                return targetPath;
            }
        };
    }
    
    public static void main(String[] args) {
        Logger.info("Manueller Start des Update-Checks...");
        
        try {
			UpdateChecker.getCurrentVersion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        UpdateChecker.createUpdateTask();
        Logger.info("Update-Check beendet.");
    }
}
