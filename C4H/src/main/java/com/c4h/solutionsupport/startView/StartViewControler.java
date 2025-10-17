package com.c4h.solutionsupport.startView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import com.c4h.solutionsupport.util.Logger;

public class StartViewControler {

    @FXML
    private AnchorPane anchorPane;

    // Labels
    @FXML private Label labelUnten;
    @FXML private Label labelOnten;

    // Buttons
    @FXML private Button btnCallSupport;
    @FXML private Button btnSendTicket;
    @FXML private Button btn3sMitarbeiter;
    @FXML private Button btnGeraeteInfo;

    // Logo
    @FXML private ImageView logoView;
    
    @FXML private Set<String> enabledModules = new HashSet<>();

    public void setEnabledModules(String... modules) {
        enabledModules.addAll(Arrays.asList(modules));
        updateButtons();
    }

    @FXML private void updateButtons() {
    	btnCallSupport.setVisible(enabledModules.contains("chat"));
    	btnSendTicket.setVisible(enabledModules.contains("Ticket"));
    	btnGeraeteInfo.setVisible(enabledModules.contains("pcInfo"));
    	btn3sMitarbeiter.setVisible(enabledModules.contains("intern"));
    	
    }

    @FXML
    public void initialize() {
        try {
            Logger.info("[StartViewControler] initialize() gestartet");

            if (logoView != null) {
                double radius = Math.min(logoView.getFitWidth(), logoView.getFitHeight()) / 2;
                if (radius <= 0) {
                    Logger.warn("[StartViewControler] Logo-ImageView hat ungültige Dimensionen (evtl. noch nicht geladen?)", null);
                } else {
                    Circle clip = new Circle(radius, radius, radius);
                    logoView.setClip(clip);
                    Logger.info("[StartViewControler] Logo-Clipping gesetzt");
                }
            } else {
                Logger.warn("[StartViewControler] logoView ist null – Logo-Clipping wird übersprungen", null);
            }

            if (btnCallSupport == null || btnSendTicket == null || btn3sMitarbeiter == null || btnGeraeteInfo == null) {
                Logger.error("[StartViewControler] Ein oder mehrere Buttons konnten nicht initialisiert werden. Bitte FXML prüfen.", null);
                return;
            }

            btnGeraeteInfo.setOnAction(e -> 
                navigateTo("/pcinfo.fxml", btnGeraeteInfo.getScene(),
                    com.c4h.solutionsupport.pcInformation.PcInformationControler.class));

            btnCallSupport.setOnAction(e -> 
                navigateTo("/chat.fxml", btnCallSupport.getScene(),
                    com.c4h.solutionsupport.chat.ChatControler.class));

            btnSendTicket.setOnAction(e -> 
                navigateTo("/ticket.fxml", btnSendTicket.getScene(),
                    com.c4h.solutionsupport.ticket.TicketControler.class));

            btn3sMitarbeiter.setOnAction(e -> 
                navigateTo("/mitarbeiter.fxml", btn3sMitarbeiter.getScene(),
                    com.c4h.solutionsupport.mitarbeiter.MitarbeiterControler.class));

            Logger.info("[StartViewControler] Button-Aktionen gesetzt");

        } catch (Exception ex) {
            Logger.error("[StartViewControler] Fehler in initialize(): ", ex);
        }
    }

    private <T> void navigateTo(String fxmlPath, Scene currentScene, Class<T> controllerClass) {
        try {
            var resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                Logger.error("[StartViewControler] FXML-Datei nicht gefunden: " + fxmlPath, null);
                return;
            } else {
                Logger.info("[StartViewControler] Resource gefunden: " + resource);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = null;

            try {
                root = loader.load();
            } catch (IOException e) {
                Logger.error("[StartViewControler] Fehler beim Laden der FXML-Datei " + fxmlPath, e);

                Throwable cause = e.getCause();
                while (cause != null) {
                    Logger.error("[StartViewControler] Ursache: " + cause.getMessage(), cause);
                    cause = cause.getCause();
                }
                return;
            }

            Object controller = loader.getController();
            if (controllerClass.isInstance(controller)) {
                try {
                    Method setCallback = controllerClass.getMethod("setBackCallback", Runnable.class);
                    Runnable callback = () -> backToStartView(currentScene);
                    setCallback.invoke(controller, callback);
                    Logger.info("[StartViewControler] Rücksprung-Callback gesetzt für " + fxmlPath);
                } catch (NoSuchMethodException e) {
                    Logger.info("[StartViewControler] Controller für " + fxmlPath + " hat keine setBackCallback()-Methode.");
                } catch (Exception ex) {
                    Logger.warn("[StartViewControler] Fehler beim Setzen des Rücksprung-Callbacks für " + fxmlPath, ex);
                }
            } else {
                Logger.warn("[StartViewControler] Controller-Typ passt nicht für " + fxmlPath, null);
            }

            applyTransition(currentScene, root);

        } catch (Exception e) {
            Logger.error("[StartViewControler] Allgemeiner Fehler beim Navigieren zu " + fxmlPath, e);

            Throwable cause = e.getCause();
            while (cause != null) {
                Logger.error("[StartViewControler] Ursache: " + cause.getMessage(), cause);
                cause = cause.getCause();
            }
        }
    }

    private void backToStartView(Scene scene) {
        try {
            var url = getClass().getResource("/startDesktop.fxml");
            if (url == null) {
                Logger.error("[StartViewControler] Startansicht (startDesktop.fxml) konnte nicht gefunden werden", null);
                return;
            }

            Parent startRoot = FXMLLoader.load(url);
            applyTransition(scene, startRoot);
            Logger.info("[StartViewControler] Zurück zur Startansicht navigiert");
        } catch (IOException e) {
            Logger.error("[StartViewControler] Fehler beim Zurücknavigieren zur Startansicht", e);
        }
    }

    private void applyTransition(Scene scene, Parent newRoot) {
        if (scene != null && newRoot != null) {
            newRoot.setOpacity(0);
            scene.setRoot(newRoot);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            Logger.info("[StartViewControler] Fade-Transition gestartet");
        } else {
            Logger.warn("[StartViewControler] Scene oder Root ist null – applyTransition fehlgeschlagen", null);
        }
    }

	public Node getBtn3sMitarbeiter() {
		// TODO Auto-generated method stub
		return btn3sMitarbeiter;
	}

	public Node getBtnGeraeteInfo() {
		// TODO Auto-generated method stub
		return btnGeraeteInfo;
	}
	public Node getBtnCallSupport() {
		// TODO Auto-generated method stub
		return btnCallSupport;
	}
}
