package com.c4h.solutionsupport.mitarbeiter;

import java.io.FileOutputStream;
import java.io.OutputStream;

import com.c4h.solutionsupport.util.Logger;
import com.c4h.solutionsupport.util.PlatformHelper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class MitarbeiterControler {

    @FXML
    private Button backButton;

    @FXML
    private ImageView logoView;

    @FXML
    private VBox helpdeskBox;

    @FXML
    private Label helpdeskTitle;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane helpdeskWebViewPane;

    private WebView helpdeskWebView;
    private boolean helpdeskExpanded = false;  // Startzustand: nicht erweitert

    private Runnable backCallback;

    @FXML
    public void initialize() {
        Logger.info("[MitarbeiterControler] initialize() gestartet");

        try {
            // Rundes Logo
            double radius = Math.min(logoView.getFitWidth(), logoView.getFitHeight()) / 2;
            Circle clip = new Circle(radius, radius, radius);
            logoView.setClip(clip);
            Logger.info("Logo mit rundem Clip initialisiert");

            // Zurück-Icon setzen
            ImageView iconBack = new ImageView(getClass().getResource("/Control/left.png").toExternalForm());
            iconBack.setFitWidth(10);
            iconBack.setFitHeight(10);

            backButton.setGraphic(iconBack);
            backButton.setOnAction(event -> onBackClicked());
            Logger.info("Back-Button initialisiert");

            // WebView starten
            starteWebSeite();

            // Klick-Handler für Titel
            helpdeskTitle.setOnMouseClicked(event -> toggleHelpdeskView());

        } catch (Exception e) {
            Logger.error("[MitarbeiterControler] Fehler in initialize(): ", e);
        }
    }

    private void starteWebSeite() {
        String os = PlatformHelper.geStateOs();
        Logger.info("[MitarbeiterControler] Betriebssystem erkannt: " + os);

        if ("android".equals(os)) {
            Logger.info("[MitarbeiterControler] Android erkannt – WebView wird nicht geladen.");
            Label androidLabel = new Label("Webansicht ist auf Android nicht verfügbar.");
            helpdeskWebViewPane.getChildren().setAll(androidLabel);
            return;
        }

        try {
            Class.forName("javafx.scene.web.WebView");
            helpdeskWebView = new WebView();
            helpdeskWebView.setPrefSize(400, 250);

            WebEngine webEngine = helpdeskWebView.getEngine();
            webEngine.load("https://fehlermeldung.3s-hamburg.de/mitarbeiter");

            helpdeskWebViewPane.getChildren().setAll(helpdeskWebView);

            AnchorPane.setTopAnchor(helpdeskWebView, 0.0);
            AnchorPane.setBottomAnchor(helpdeskWebView, 0.0);
            AnchorPane.setLeftAnchor(helpdeskWebView, 0.0);
            AnchorPane.setRightAnchor(helpdeskWebView, 0.0);
            
			/*
			 * // Druck auslösen, wenn Seite vollständig geladen ist
			 * webEngine.getLoadWorker().stateProperty().addListener((obs, oldState,
			 * newState) -> { if (newState == Worker.State.SUCCEEDED) {
			 * Logger.info("Seite geladen – Druckdialog wird geöffnet.");
			 * 
			 * // Kurze Verzögerung vor dem Druckdialog PauseTransition pause = new
			 * PauseTransition(Duration.seconds(1)); pause.setOnFinished(e -> {
			 * webEngine.executeScript("window.print()");
			 * exportWebPageAsPdf(webEngine.getLocation(), "output.pdf"); }); pause.play();
			 * } });
			 */
            webEngine.load("https://fehlermeldung.3s-hamburg.de/mitarbeiter");
            Logger.info("WebView erfolgreich geladen und URL gesetzt");

        } catch (ClassNotFoundException e) {
            Logger.warn("[MitarbeiterControler] WebView-Modul ist nicht verfügbar – vermutlich im nativen Build.", e);
            Label errorLabel = new Label("Webansicht nicht verfügbar (WebView fehlt).");
            helpdeskWebViewPane.getChildren().setAll(errorLabel);

        } catch (Exception e) {
            Logger.error("[MitarbeiterControler] Fehler beim Laden der Webansicht: ", e);
            Label errorLabel = new Label("Webansicht konnte nicht geladen werden.");
            helpdeskWebViewPane.getChildren().setAll(errorLabel);
        }
    }

    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
        Logger.info("[MitarbeiterControler] backCallback gesetzt");
    }

    @FXML
    private void onBackClicked() {
        if (backCallback != null) {
            Logger.info("[MitarbeiterControler] onBackClicked aufgerufen, backCallback wird ausgeführt");
            backCallback.run();
        } else {
            Logger.warn("[MitarbeiterControler] backCallback ist nicht gesetzt.", null);
        }
    }
    public void exportWebPageAsPdf(String url, String outputPath) {
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(url); // HTML-Seite als URL
            builder.toStream(os);
            builder.run();
            System.out.println("✅ PDF wurde erstellt: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void toggleHelpdeskView() {
    	 try {
             if (anchorPane != null && helpdeskBox != null && helpdeskWebViewPane != null) {
                 double fullHeight = anchorPane.getHeight() - 60;
                 double reservedForBackButton = 60;
                 double bottomMargin = 30;
                 double expandedHeight = fullHeight - reservedForBackButton - bottomMargin;
                 double collapsedVBoxHeight = 400;

                 Logger.info("[MitarbeiterControler] toggleHelpdeskView aufgerufen. Aktueller Zustand helpdeskExpanded=" + helpdeskExpanded);

                 if (!helpdeskExpanded) {
                     helpdeskBox.setPrefHeight(expandedHeight);

                     helpdeskWebViewPane.setPrefHeight(expandedHeight - bottomMargin);
                     helpdeskWebViewPane.setPrefHeight(expandedHeight - bottomMargin);
                  
                     AnchorPane.setTopAnchor(helpdeskBox, reservedForBackButton);
                     helpdeskExpanded = true;

                     Logger.info("[MitarbeiterControler] Helpdesk-Ansicht erweitert");
                 } else {
                     helpdeskBox.setPrefHeight(collapsedVBoxHeight);

                     helpdeskWebViewPane.setPrefHeight(collapsedVBoxHeight);
                     helpdeskWebViewPane.setPrefHeight(collapsedVBoxHeight);
                     helpdeskTitle.setPrefHeight(40);

                     AnchorPane.setTopAnchor(helpdeskBox, 280.0);
            
                     helpdeskExpanded = false;

                     Logger.info("[MitarbeiterControler] Helpdesk-Ansicht reduziert");
                 }
             } else {
                 Logger.warn("[ChatControler] UI-Komponenten nicht vollständig initialisiert", null);
             }
         } catch (Exception e) {
             Logger.error("[ChatControler]  Fehler in toggleHelpdeskView: ", e);
         }
    }
}
