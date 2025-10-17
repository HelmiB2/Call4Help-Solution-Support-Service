package com.c4h.solutionsupport.chat;

import java.awt.Desktop;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.c4h.solutionsupport.util.AntwortManager;
import com.c4h.solutionsupport.util.Logger;
import com.c4h.solutionsupport.util.OpenAIApi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Controller class for the Chat interface in the C4H Support application.
 * Handles user interactions, chat messages, helpdesk panel toggling, and integration with KI (AI) responses.
 */
public class ChatControler {

    @FXML
    private Button backButton;

    @FXML
    private ImageView logoView;

    private Runnable backCallback;
    
    // HelpDeskPanel
    @FXML private VBox helpdeskBox;
    @FXML private Label helpdeskTitle;
    @FXML private AnchorPane anchorPane;
    
    // ChatArea
    //@FXML private ListView<Label> chatListView;
    @FXML private ListView<Node> chatListView;

    @FXML private TextArea chatInput;
    @FXML private HBox inputBox; 
    
    // Startzustand: nicht erweitert
    private boolean helpdeskExpanded = false; 

    /**
     * Initializes the chat UI.
     * Sets the rounded logo, back button icon, click listeners, and displays a welcome message.
     */
    @FXML
    public void initialize() {
        // Rundes Logo
        double radius = Math.min(logoView.getFitWidth(), logoView.getFitHeight()) / 2;
        Circle clip = new Circle(radius, radius, radius);
        logoView.setClip(clip);

        // Zurück-Icon setzen
        ImageView iconBack = new ImageView(getClass().getResource("/Control/left.png").toExternalForm());
        iconBack.setFitWidth(10);
        iconBack.setFitHeight(10);

        backButton.setGraphic(iconBack);
        backButton.setOnAction(event -> onBackClicked());
        
        // Toggle Helpdesk View bei Klick auf Titel
        helpdeskTitle.setOnMouseClicked(event -> toggleHelpdeskView());
        
        // Antworten laden
        AntwortManager.ladeAntworten();

        // Begrüßungsnachricht anzeigen
        Label welcome = new Label("3S-Supporter: Hallo! Wie kann ich dir helfen?");
        welcome.setStyle("-fx-background-color: #E2E3E5; -fx-padding: 8; -fx-background-radius: 8;");
        chatListView.getItems().add(welcome);
        
        // **Enter-Taste zum Senden**
        chatInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSendChatMessage();
                event.consume(); // verhindert Zeilenumbruch
            }
        });
     // **shift+Enter-Taste zum ZeilenUmbruch**
        chatInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!event.isShiftDown()) {
                    onSendChatMessage();
                    event.consume(); // verhindert Zeilenumbruch
                }
            }
        });

        
    }

    /**
     * Sends the user's chat message.
     * Displays the message in the chat list and fetches an AI response asynchronously.
     */
    @FXML
    private void onSendChatMessage() {
        String userMsg = chatInput.getText().trim();
        if (userMsg.isEmpty()) return;
        Logger.info("Benutzer sendet Nachricht: " + userMsg);

        // Benutzer-Nachricht anzeigen
        Label userLabel = new Label("Du: " + userMsg);
        userLabel.setWrapText(true);
        userLabel.setMaxWidth(chatListView.getWidth() - 20);
        userLabel.setStyle("-fx-background-color: #D1ECF1; -fx-padding: 8; -fx-background-radius: 8;");
        chatListView.getItems().add(userLabel);
        chatListView.scrollTo(chatListView.getItems().size() - 1);

        chatInput.clear();

        // KI-Antwort asynchron holen
        new Thread(() -> {
            String kiAntwort = getKiAntwort(userMsg);

            Platform.runLater(() -> {
                // TextFlow erzeugen, um Links klickbar zu machen
                TextFlow textFlow = new TextFlow();
                textFlow.setMaxWidth(chatListView.getWidth() - 20);

                // Regex für URLs
                Pattern urlPattern = Pattern.compile("(https?://[\\w\\-\\.\\?\\&\\=\\/%#]+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = urlPattern.matcher(kiAntwort);

                int lastIndex = 0;
                while (matcher.find()) {
                    // normalen Text vor Link hinzufügen
                    if (matcher.start() > lastIndex) {
                        Text normalText = new Text(kiAntwort.substring(lastIndex, matcher.start()));
                        textFlow.getChildren().add(normalText);
                    }

                    // Hyperlink hinzufügen
                    String url = matcher.group(1);
                    Hyperlink link = new Hyperlink(url);
                    link.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    textFlow.getChildren().add(link);

                    lastIndex = matcher.end();
                }

                // Restlichen Text hinzufügen
                if (lastIndex < kiAntwort.length()) {
                    Text remainingText = new Text(kiAntwort.substring(lastIndex));
                    textFlow.getChildren().add(remainingText);
                }

                // VBox für Hintergrund, Padding, Rundung
                VBox botMessage = new VBox(textFlow);
                botMessage.setStyle("-fx-background-color: #E2E3E5; -fx-padding: 8; -fx-background-radius: 8;");

                chatListView.getItems().addAll(botMessage);
                chatListView.scrollTo(chatListView.getItems().size() - 1);
            });
        }).start();
    }

    /**
     * Returns a response for the given input.
     * First checks predefined answers, then falls back to OpenAI API if available.
     * 
     * @param eingabe The user input message
     * @return Response message as a String
     */
    private String getKiAntwort(String eingabe) {
        // 1. Antwort aus dem AntwortManager holen
        String regelAntwort = AntwortManager.findeAntwort(eingabe);
        if (regelAntwort != null) return regelAntwort;

        // 2. KI-Antwort über OpenAI API
        String reply = null;
        try {
            reply = OpenAIApi.sendPrompt(eingabe);
        } catch (Exception e) {
            Logger.error("Fehler beim Aufruf der OpenAI API", e);
            e.printStackTrace();
        }

        return (reply != null) ? reply : "Ich habe das leider nicht verstanden.";
    }

    /**
     * Sets a callback to be executed when the back button is clicked.
     * 
     * @param backCallback Runnable callback
     */
    public void setBackCallback(Runnable backCallback) {
        this.backCallback = backCallback;
    }

    /**
     * Executes the back callback when the back button is clicked.
     * Logs a warning if no callback is set.
     */
    @FXML
    private void onBackClicked() {
        if (backCallback != null) {
            backCallback.run();
        } else {
            Logger.warn("Warnung: backCallback ist nicht gesetzt.", null);
        }
    }

    /**
     * Toggles the helpdesk panel between expanded and collapsed states.
     * Adjusts the heights of the panel and chat list accordingly.
     */
    @FXML
    private void toggleHelpdeskView() {
        try {
            if (anchorPane != null && helpdeskBox != null && chatListView != null) {
                double fullHeight = anchorPane.getHeight() - 60;
                double reservedForBackButton = 60;
                double bottomMargin = 30;
                double expandedHeight = fullHeight - reservedForBackButton - bottomMargin;
                double collapsedVBoxHeight = 425;

                Logger.info("[ChatControler] toggleHelpdeskView aufgerufen. Aktueller Zustand helpdeskExpanded=" + helpdeskExpanded);

                if (!helpdeskExpanded) {
                    helpdeskBox.setPrefHeight(expandedHeight);
                    chatListView.setPrefHeight(expandedHeight - bottomMargin);
                    AnchorPane.setTopAnchor(helpdeskBox, reservedForBackButton);
                    helpdeskExpanded = true;
                    Logger.info("[ChatControler] Helpdesk-Ansicht erweitert");
                } else {
                    helpdeskBox.setPrefHeight(collapsedVBoxHeight);
                    chatListView.setPrefHeight(collapsedVBoxHeight);
                    helpdeskTitle.setPrefHeight(40);
                    AnchorPane.setTopAnchor(helpdeskBox, 280.0);
                    helpdeskExpanded = false;
                    Logger.info("[ChatControler] Helpdesk-Ansicht reduziert");
                }
            } else {
                Logger.warn("[ChatControler] UI-Komponenten nicht vollständig initialisiert", null);
            }
        } catch (Exception e) {
            Logger.error("[ChatControler] Fehler in toggleHelpdeskView: ", e);
        }
    }
}
