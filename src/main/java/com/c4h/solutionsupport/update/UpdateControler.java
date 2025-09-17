package com.c4h.solutionsupport.update;


import java.nio.file.Path;

import com.c4h.solutionsupport.pcInformation.GeraeteInfo;
import com.c4h.solutionsupport.pcInformation.InfoAndroid;
import com.c4h.solutionsupport.pcInformation.InfoDesktop;
import com.c4h.solutionsupport.util.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class UpdateControler {

    @FXML private Button backButton;
    @FXML private Button updateStartButton;

    @FXML private ImageView logoView;

    @FXML private Runnable backCallback;

    @FXML private ImageView iconBack;

    // HelpDeskPanel
    @FXML private VBox helpdeskBox;
    @FXML private Label helpdeskTitle;
    @FXML private AnchorPane anchorPane;
    @FXML private TextArea changelogArea;
    @FXML private AnchorPane helpdesktextPane;
    
    private boolean helpdeskExpanded = false;  // Startzustand: nicht erweitert
    
    @FXML private ProgressBar downloadProgressBar;
    @FXML private Label progressLabel;

    @FXML GeraeteInfo androidInfo = new InfoAndroid();
    @FXML GeraeteInfo desktopdInfo = new InfoDesktop();

    @FXML
    public void initialize() {
        try {
            // Rundes Logo setzen
            if (logoView != null) {
                double radius = Math.min(logoView.getFitWidth(), logoView.getFitHeight()) / 2;
                if (radius <= 0) {
                    Logger.warn("LogoView hat ungültige Dimensionen (evtl. noch nicht geladen?)", null);
                } else {
                    Circle clip = new Circle(radius, radius, radius);
                    logoView.setClip(clip);
                }
            } else {
                Logger.warn("logoView ist null – Logo-Clipping übersprungen", null);
            }
            
            if (backButton != null) {
                backButton.setGraphic(iconBack);
                backButton.setOnAction(event -> onBackClicked());
            } else {
                Logger.error("backButton ist null – keine Aktion gesetzt", null);
            }

            if (helpdeskTitle != null) {
                helpdeskTitle.setOnMouseClicked(event -> toggleHelpdeskView());
            } else {
                Logger.warn("helpdeskTitle ist null – Toggle HelpdeskView nicht möglich", null);
            }
            changeLogForTextBar();
            

        } catch (Exception e) {
            Logger.error("Fehler in initialize()", e);
        }
    }
    @FXML private void changeLogForTextBar() {
    	Logger.info("ChangeLogs aus dem Server Laden");
    	changelogArea = new TextArea();
    	changelogArea.setPrefSize(350, 300);
    	 helpdesktextPane.getChildren().setAll(changelogArea);

    	 AnchorPane.setTopAnchor(changelogArea, 10.0);
         AnchorPane.setBottomAnchor(changelogArea, 10.0);
         AnchorPane.setLeftAnchor(changelogArea, 10.0);
         AnchorPane.setRightAnchor(changelogArea, 10.0);
         
         String changelogText = UpdateChecker.getChangelogText();
         changelogArea.setText(changelogText);
         changelogArea.positionCaret(0);
    }

    @FXML private void updateStart() {
    	Logger.info("Updates aus dem Server Laden");
    	//UpdateChecker.createUpdateTask();
    	
    	 Task<Path> updateTask = UpdateChecker.createUpdateTask();

    	    // Binden
    	    downloadProgressBar.progressProperty().bind(updateTask.progressProperty());
    	    progressLabel.textProperty().bind(updateTask.messageProperty());

    	    // Fehlerbehandlung
    	    updateTask.setOnFailed(e -> {
    	        progressLabel.textProperty().unbind();
    	        progressLabel.setText("Fehler: " + updateTask.getException().getMessage());
    	    });

    	    // Task starten
    	    Thread thread = new Thread(updateTask);
    	    thread.setDaemon(true);
    	    thread.start();
    	
    	}

    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
    }

    @FXML
    private void onBackClicked() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close(); // Fenster schließen
        Logger.info("Update-Fenster geschlossen");
    }

    @FXML
    private void toggleHelpdeskView() {
    	 try {
             if (anchorPane != null && helpdeskBox != null && helpdesktextPane != null && changelogArea != null) {
                 double fullHeight = anchorPane.getHeight() - 60;
                 double reservedForBackButton = 60;
                 double bottomMargin = 30;
                 double expandedHeight = fullHeight - reservedForBackButton - bottomMargin;
                 double collapsedVBoxHeight = 385+40;

                 Logger.info("[UpdateControler] toggleHelpdeskView aufgerufen. Aktueller Zustand helpdeskExpanded=" + helpdeskExpanded);

                 if (!helpdeskExpanded) {
                     helpdeskBox.setPrefHeight(expandedHeight);

                     changelogArea.setPrefHeight(expandedHeight - bottomMargin);
                     helpdesktextPane.setPrefHeight(expandedHeight - bottomMargin);
                  
                     AnchorPane.setTopAnchor(helpdeskBox, reservedForBackButton);
                     helpdeskExpanded = true;

                     Logger.info("[UpdateControler] Helpdesk-Ansicht erweitert");
                 } else {
                     helpdeskBox.setPrefHeight(collapsedVBoxHeight);

                     changelogArea.setPrefHeight(collapsedVBoxHeight);
                     helpdesktextPane.setPrefHeight(collapsedVBoxHeight);
                     helpdeskTitle.setPrefHeight(40);

                     AnchorPane.setTopAnchor(helpdeskBox, 280.0);
            
                     helpdeskExpanded = false;

                     Logger.info("[UpdateControler] Helpdesk-Ansicht reduziert");
                 }
             } else {
                 Logger.warn("[UpdateControler] UI-Komponenten nicht vollständig initialisiert", null);
             }
         } catch (Exception e) {
             Logger.error("[UpdateControler] Fehler in toggleHelpdeskView: ", e);
         }
     
    }
}
