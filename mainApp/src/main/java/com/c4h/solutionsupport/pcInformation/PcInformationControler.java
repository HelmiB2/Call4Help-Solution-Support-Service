package com.c4h.solutionsupport.pcInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.c4h.solutionsupport.util.Logger;
import com.c4h.solutionsupport.util.PlatformHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class PcInformationControler {

    @FXML private Button backButton;
    
    @FXML private Button btnRefreshInfo;
    @FXML private Button btnExportInfo;
    @FXML private Button btnCopyInfo;
    
    @FXML private Button btnSystemInfo;
    @FXML private Button btnHardwareInfo;
    @FXML private Button btnNetworkInfo;

    private Runnable backCallback;

    @FXML private ImageView iconBack;

    // Logo
    @FXML private ImageView logoView;

    // PCinformationHelpDeskPanel
    @FXML private VBox helpdeskBox;
    @FXML private Label helpdeskTitle;
    @FXML private AnchorPane anchorPane;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox infoBox;

    private boolean helpdeskExpanded = false;  // Startzustand: nicht erweitert
    
    
    @FXML public void getSystemInfo() {
        try {
            GeraeteInfo geraeteInfo = PlatformHelper.isAndroid() ? new InfoAndroid() : new InfoDesktop();
            JSONObject systemJson = ((InfoDesktop) geraeteInfo).getSystemJson(geraeteInfo);

            // InfoBox zuerst leeren
            infoBox.getChildren().clear();

            // Alle Keys aus dem JSON auslesen
            List<String> keys = new ArrayList<>();
            systemJson.keys().forEachRemaining(keys::add);
            Collections.sort(keys); // Optional: alphabetisch sortieren

            // Jeden Key als Label anzeigen
            for (String key : keys) {
                Object value = systemJson.opt(key);
                String text = key + ": " + (value != null ? value.toString() : "nicht verfügbar");

                Label label = new Label(text);
                label.getStyleClass().add("info-label-card"); // optional CSS-Klasse

                infoBox.getChildren().add(label);
            }

            Logger.info("[PcInformationControler] Systeminformationen in InfoBox angezeigt");

        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Ermitteln der Systeminformationen: ", e);
        }
    }
    @FXML
    public void getHardwareInfo() {
        try {
            GeraeteInfo geraeteInfo = PlatformHelper.isAndroid() ? new InfoAndroid() : new InfoDesktop();
            JSONObject hardwareJson = ((InfoDesktop) geraeteInfo).getHardwareJson(geraeteInfo);

            // InfoBox zuerst leeren
            infoBox.getChildren().clear();

            // Alle Keys aus dem JSON auslesen
            List<String> keys = new ArrayList<>();
            hardwareJson.keys().forEachRemaining(keys::add);
            Collections.sort(keys); // optional alphabetisch sortieren

            // Jeden Key als Label anzeigen
            for (String key : keys) {
                Object value = hardwareJson.opt(key);
                String text = key + ": " + (value != null ? value.toString() : "nicht verfügbar");

                Label label = new Label(text);
                label.getStyleClass().add("info-label-card");

                infoBox.getChildren().add(label);
            }

            Logger.info("[PcInformationControler] Hardwareinformationen in InfoBox angezeigt");

        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Ermitteln der Hardwareinformationen: ", e);
        }
    }

    @FXML
    public void getNetworkInfo() {
        try {
            GeraeteInfo geraeteInfo = PlatformHelper.isAndroid() ? new InfoAndroid() : new InfoDesktop();
            JSONObject networkJson = ((InfoDesktop) geraeteInfo).getNetworkJson(geraeteInfo);

            // InfoBox zuerst leeren
            infoBox.getChildren().clear();

            // Alle Keys aus dem JSON auslesen
            List<String> keys = new ArrayList<>();
            networkJson.keys().forEachRemaining(keys::add);
            Collections.sort(keys);

            // Jeden Key als Label anzeigen
            for (String key : keys) {
                Object value = networkJson.opt(key);
                String text = key + ": " + (value != null ? value.toString() : "nicht verfügbar");

                Label label = new Label(text);
                label.getStyleClass().add("info-label-card");

                infoBox.getChildren().add(label);
            }

            Logger.info("[PcInformationControler] Netzwerkinformationen in InfoBox angezeigt");

        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Ermitteln der Netzwerkinformationen: ", e);
        }
    }

    
    @FXML public void refreshSystemInfo() {
    	
    	Logger.info("[PcInformationControler] Aktualisierung der Systeminformation");
    	geraetInfo();
    }
    @FXML
    public void exportSystemInfo() {
        try {
            Logger.info("[PcInformationControler] Exportieren der Systeminformation gestartet");

            // Systeminformationen ermitteln
            GeraeteInfo geraeteInfo = PlatformHelper.isAndroid() ? new InfoAndroid() : new InfoDesktop();
            String jsonText = geraeteInfo.toJson();

            // FileChooser für den Speicherort
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Systeminformationen speichern");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("JSON-Datei", "*.json")
            );
            fileChooser.setInitialFileName("systeminfo.json");

            // Dialog anzeigen (Explorer-ähnlich)
            java.io.File file = fileChooser.showSaveDialog(null); // null öffnet in der Mitte
            if (file != null) {
                java.nio.file.Files.write(file.toPath(), jsonText.getBytes());
                Logger.info("[PcInformationControler] Systeminformationen exportiert nach: " + file.getAbsolutePath());
            } else {
                Logger.info("[PcInformationControler] Export vom Benutzer abgebrochen");
            }

        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Exportieren der Systeminformation: ", e);
        }
    }

    @FXML public void copySystemInfo() {
        try {
            Logger.info("[PcInformationControler] Kopieren der Systeminformation gestartet");

            GeraeteInfo geraeteInfo = PlatformHelper.isAndroid() ? new InfoAndroid() : new InfoDesktop();

            // JSON mit nur Ergebnissen (schön formatiert)
            String jsonText = geraeteInfo.toJson(); // toJson liefert bereits prettified JSON (indent=4)

            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(jsonText);
            clipboard.setContent(content);

            Logger.info("[PcInformationControler] Systeminformationen erfolgreich in Zwischenablage kopiert");
        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Kopieren der Systeminformation: ", e);
        }
    }

    @FXML
    public void initialize() {
        Logger.info("[PcInformationControler] initialize() gestartet");
        try {
            geraetInfo();

            // Rundes Bild mit Radius = Hälfte von Höhe oder Breite
            double radius = Math.min(logoView.getFitWidth(), logoView.getFitHeight()) / 2;
            Circle clip = new Circle(radius, radius, radius);
            logoView.setClip(clip);
            Logger.info("[PcInformationControler] Logo rund zugeschnitten");

            // Zurück-Icon setzen
            iconBack = new ImageView(getClass().getResource("/Control/left.png").toExternalForm());
            iconBack.setFitWidth(10);
            iconBack.setFitHeight(10);

            backButton.setGraphic(iconBack);
            backButton.setOnAction(event -> onBackClicked());
            Logger.info("[PcInformationControler] Back-Button initialisiert");

            helpdeskTitle.setOnMouseClicked(event -> toggleHelpdeskView());
        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler in initialize(): ", e);
        }
    }

    private void geraetInfo() {
        try {
            Logger.info("[PcInformationControler] Ermittlung der Geräteinformationen gestartet");

            GeraeteInfo geraeteInfo;

            if (PlatformHelper.isAndroid()) {
                Logger.info("[PcInformationControler] Android erkannt - InfoAndroid wird verwendet");
                geraeteInfo = new InfoAndroid();
            } else {
                Logger.info("[PcInformationControler] Desktop erkannt - InfoDesktop wird verwendet");
                geraeteInfo = new InfoDesktop();
            }

            String jsonText = geraeteInfo.toJson();
            JSONObject json = new JSONObject(jsonText);

            List<String> keys = new ArrayList<>(json.keySet());
            Collections.sort(keys);

            infoBox.getChildren().clear();

            for (String key : keys) {
                Object value = json.opt(key);
                String text = key + ": " + (value != null ? value.toString() : "nicht verfügbar");

                Label label = new Label(text);
                label.getStyleClass().add("info-label-card");

                infoBox.getChildren().add(label);
            }

            Logger.info("[PcInformationControler] Geräteinformationen erfolgreich geladen und angezeigt");
        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler beim Laden der Geräteinformationen: ", e);
        }
    }

    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
        Logger.info("[PcInformationControler] backCallback gesetzt");
    }

    @FXML
    private void onBackClicked() {
        Logger.info("[PcInformationControler] onBackClicked aufgerufen - zurück zur Startseite");
        if (backCallback != null) {
            backCallback.run();
        } else {
            Logger.warn("[PcInformationControler] backCallback ist nicht gesetzt.", null);
        }
    }

    @FXML
    private void toggleHelpdeskView() {
        try {
            if (anchorPane != null && helpdeskBox != null && infoBox != null && scrollPane != null) {
                double fullHeight = anchorPane.getHeight() - 60;
                double reservedForBackButton = 60;
                double bottomMargin = 30;
                double expandedHeight = fullHeight - reservedForBackButton - bottomMargin;
                double collapsedVBoxHeight = 250;

                Logger.info("[PcInformationControler] toggleHelpdeskView aufgerufen. Zustand helpdeskExpanded=" + helpdeskExpanded);

                if (!helpdeskExpanded) {
                    helpdeskBox.setPrefHeight(expandedHeight);
                    infoBox.setPrefHeight(expandedHeight - bottomMargin);
                    scrollPane.setPrefHeight(expandedHeight - bottomMargin);
                    AnchorPane.setTopAnchor(helpdeskBox, reservedForBackButton);

                    helpdeskExpanded = true;
                    Logger.info("[PcInformationControler] Helpdesk-Ansicht erweitert");
                } else {
                    helpdeskBox.setPrefHeight(collapsedVBoxHeight);
                    infoBox.setPrefHeight(collapsedVBoxHeight);
                    scrollPane.setPrefHeight(collapsedVBoxHeight);
                    AnchorPane.setTopAnchor(helpdeskBox, 300.0);

                    helpdeskExpanded = false;
                    Logger.info("[PcInformationControler] Helpdesk-Ansicht reduziert");
                }
            } else {
                Logger.warn("[PcInformationControler] UI-Komponenten sind nicht vollständig initialisiert", null);
            }
        } catch (Exception e) {
            Logger.error("[PcInformationControler] Fehler in toggleHelpdeskView: ", e);
        }
    }
}
