package com.c4h.solutionsupport.ticket;

import com.c4h.solutionsupport.pcInformation.GeraeteInfo;
import com.c4h.solutionsupport.pcInformation.InfoAndroid;
import com.c4h.solutionsupport.pcInformation.InfoDesktop;
import com.c4h.solutionsupport.util.Logger;
import com.c4h.solutionsupport.util.PlatformHelper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Controller for the Ticket view in the application.
 * <p>
 * This class handles the initialization of the ticket view UI, including:
 * <ul>
 *   <li>Logo clipping</li>
 *   <li>Back button functionality</li>
 *   <li>Helpdesk panel expansion/collapse</li>
 *   <li>Loading the WebView with the appropriate URL</li>
 * </ul>
 * It also detects the platform (Android or Desktop) and adapts behavior accordingly.
 * </p>
 * 
 * <p>On Android, the WebView is replaced with a placeholder message because it is not supported.</p>
 * 
 * <p>Supports a back callback to return to the previous view.</p>
 * 
 * @author Helmi Bani
 */
public class TicketControler {

    // -------------------------
    // FXML UI Components
    // -------------------------

    /** Button to navigate back to the previous view. */
    @FXML private Button backButton;

    /** ImageView for the application logo. */
    @FXML private ImageView logoView;

    /** Runnable callback invoked when backButton is clicked. */
    @FXML private Runnable backCallback;

    /** Icon for the back button. */
    @FXML private ImageView iconBack;

    /** VBox container for the Helpdesk panel. */
    @FXML private VBox helpdeskBox;

    /** Label for the Helpdesk panel title (clickable to expand/collapse). */
    @FXML private Label helpdeskTitle;

    /** Main AnchorPane for layout calculations. */
    @FXML private AnchorPane anchorPane;

    /** WebView to display the Helpdesk web page. */
    @FXML private WebView helpdeskWebView;

    /** AnchorPane container for the WebView. */
    @FXML private AnchorPane helpdeskWebViewPane;

    /** Flag indicating whether the Helpdesk panel is currently expanded. */
    private boolean helpdeskExpanded = false;

    // -------------------------
    // Platform-specific Info
    // -------------------------

    /** Android implementation of device information. */
    @FXML GeraeteInfo androidInfo = new InfoAndroid();

    /** Desktop implementation of device information. */
    @FXML GeraeteInfo desktopdInfo = new InfoDesktop();

    /** URL to load in the WebView, including school number from desktop info. */
    @FXML private String url = "https://fehlermeldung.3s-hamburg.de?schulnummer=" 
    		+desktopdInfo.getbetriebsNummer()
    		+"&pcname="+desktopdInfo.getHostname()
    		+"&ipadress="+desktopdInfo.getIpAddress()
    		+"&teamviewerID="+desktopdInfo.getTeamViewerID();
    		
    //@FXML private String url = "https://fehlermeldung.3s-hamburg.de?schulnummer=5678";
    //@FXML private String url = "https://fehlermeldung.3s-hamburg.de?schulnummer=PF01";
    

    // -------------------------
    // Initialization
    // -------------------------

    /**
     * Initializes the TicketControler.
     * <p>
     * - Sets up circular clipping for the logo.  
     * - Configures the back button and its action.  
     * - Configures Helpdesk title click for expanding/collapsing the panel.  
     * - Loads the Helpdesk WebView or placeholder message based on platform.
     * </p>
     */
    @FXML
    public void initialize() {
        try {
            // Setup circular logo
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

            // Setup back button
            if (backButton != null) {
                backButton.setGraphic(iconBack);
                backButton.setOnAction(event -> onBackClicked());
            } else {
                Logger.error("backButton ist null – keine Aktion gesetzt", null);
            }

            // Setup Helpdesk title click
            if (helpdeskTitle != null) {
                helpdeskTitle.setOnMouseClicked(event -> toggleHelpdeskView());
            } else {
                Logger.warn("helpdeskTitle ist null – Toggle HelpdeskView nicht möglich", null);
            }

            // Start WebView
            starteWebSeite();

        } catch (Exception e) {
            Logger.error("Fehler in initialize()", e);
        }
    }

    // -------------------------
    // WebView Handling
    // -------------------------

    /**
     * Loads the Helpdesk web page in the WebView.
     * <p>
     * On Android, displays a placeholder Label instead of WebView.
     * Handles exceptions if WebView is unavailable or cannot load the URL.
     * </p>
     */
    private void starteWebSeite() {
        String os = PlatformHelper.geStateOs();
        Logger.info("[TicketControler] Betriebssystem erkannt: " + os);

        if ("android".equals(os)) {
            Logger.info("[TicketControler] Android erkannt – WebView wird nicht geladen.");
            Label androidLabel = new Label("Webansicht ist auf Android nicht verfügbar.");
            helpdeskWebViewPane.getChildren().setAll(androidLabel);
            return;
        }

        try {
            Class.forName("javafx.scene.web.WebView");
            helpdeskWebView = new WebView();
            helpdeskWebView.setPrefSize(400, 350);

            WebEngine webEngine = helpdeskWebView.getEngine();
            Logger.info("[TicketControler] Lade URL: " + url);
            webEngine.load(url);

            helpdeskWebViewPane.getChildren().setAll(helpdeskWebView);

            AnchorPane.setTopAnchor(helpdeskWebView, 0.0);
            AnchorPane.setBottomAnchor(helpdeskWebView, 0.0);
            AnchorPane.setLeftAnchor(helpdeskWebView, 0.0);
            AnchorPane.setRightAnchor(helpdeskWebView, 0.0);

            Logger.info("WebView erfolgreich geladen und URL gesetzt");

        } catch (ClassNotFoundException e) {
            Logger.warn("[TicketControler] WebView-Modul ist nicht verfügbar – vermutlich im nativen Build.", e);
            Label errorLabel = new Label("Webansicht nicht verfügbar (WebView fehlt).");
            helpdeskWebViewPane.getChildren().setAll(errorLabel);

        } catch (Exception e) {
            Logger.error("[TicketControler] Fehler beim Laden der Webansicht: ", e);
            Label errorLabel = new Label("Webansicht konnte nicht geladen werden.");
            helpdeskWebViewPane.getChildren().setAll(errorLabel);
        }
    }

    // -------------------------
    // Back Navigation
    // -------------------------

    /**
     * Sets the back callback to return to the previous view.
     * 
     * @param backCallback Runnable to be executed when back button is clicked
     */
    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
    }

    /**
     * Invoked when the back button is clicked.
     * <p>
     * Executes the back callback if it has been set, otherwise logs a warning.
     * </p>
     */
    @FXML
    private void onBackClicked() {
        if (backCallback != null) {
            backCallback.run();
        } else {
            Logger.warn("backCallback ist nicht gesetzt – kein Rücksprung möglich", null);
        }
    }

    // -------------------------
    // Helpdesk Panel Toggle
    // -------------------------

    /**
     * Toggles the Helpdesk panel between expanded and collapsed states.
     * <p>
     * Adjusts the height of the VBox and WebView accordingly.
     * Logs the current state and any errors encountered.
     * </p>
     */
    @FXML
    private void toggleHelpdeskView() {
        try {
            if (anchorPane != null && helpdeskBox != null && helpdeskWebViewPane != null && helpdeskWebView != null) {
                double fullHeight = anchorPane.getHeight() - 60;
                double reservedForBackButton = 60;
                double bottomMargin = 30;
                double expandedHeight = fullHeight - reservedForBackButton - bottomMargin;
                double collapsedVBoxHeight = 385 + 40;

                Logger.info("[TicketControler] toggleHelpdeskView aufgerufen. Aktueller Zustand helpdeskExpanded=" + helpdeskExpanded);

                if (!helpdeskExpanded) {
                    helpdeskBox.setPrefHeight(expandedHeight);

                    helpdeskWebView.setPrefHeight(expandedHeight - bottomMargin);
                    helpdeskWebViewPane.setPrefHeight(expandedHeight - bottomMargin);

                    AnchorPane.setTopAnchor(helpdeskBox, reservedForBackButton);
                    helpdeskExpanded = true;

                    Logger.info("[TicketControler] Helpdesk-Ansicht erweitert");
                } else {
                    helpdeskBox.setPrefHeight(collapsedVBoxHeight);

                    helpdeskWebView.setPrefHeight(collapsedVBoxHeight);
                    helpdeskWebViewPane.setPrefHeight(collapsedVBoxHeight);
                    helpdeskTitle.setPrefHeight(40);

                    AnchorPane.setTopAnchor(helpdeskBox, 280.0);

                    helpdeskExpanded = false;

                    Logger.info("[TicketControler] Helpdesk-Ansicht reduziert");
                }
            } else {
                Logger.warn("[TicketControler] UI-Komponenten nicht vollständig initialisiert", null);
            }
        } catch (Exception e) {
            Logger.error("[TicketControler] Fehler in toggleHelpdeskView: ", e);
        }
    }
}
