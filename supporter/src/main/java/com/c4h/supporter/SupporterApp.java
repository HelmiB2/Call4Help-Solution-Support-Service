package com.c4h.supporter;

import com.c4h.solutionsupport.startView.StartViewControler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SupporterApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // StartView FXML laden (Desktop-Variante)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/startDesktop.fxml"));
        Parent root = loader.load();

        // Controller abgreifen
        StartViewControler controller = loader.getController();

        // Nur die Module aktivieren, die sichtbar sein sollen
        controller.setEnabledModules("chat");   // Chat sichtbar
        controller.setEnabledModules("Ticket"); // Ticket sichtbar
        controller.setEnabledModules("intern"); // Mitarbeiter sichtbar
        
     // Dynamisches Verschieben des Buttons "Intern"
        controller.getBtn3sMitarbeiter().visibleProperty().addListener((obs, oldVal, newVal) -> {
            adjustInternButtonPosition(controller);
        });

        // Initial position anpassen
        adjustInternButtonPosition(controller);

        // Bildschirmgröße ermitteln
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double width = bounds.getWidth();
        String scaleClass;
        double sceneWidth, sceneHeight;

        if (width >= 3000) {
            scaleClass = "scale-large";
            sceneWidth = 700;
            sceneHeight = 900;
        } else if (width <= 1400) {
            scaleClass = "scale-small";
            sceneWidth = 500;
            sceneHeight = 700;
        } else {
            scaleClass = "scale-medium";
            sceneWidth = 600;
            sceneHeight = 800;
        }

        // CSS-Klasse für Skalierung setzen
        root.getStyleClass().add(scaleClass);

        // Scene erstellen und CSS laden
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.getStylesheets().add(getClass().getResource("/Style/desktop.css").toExternalForm());

        // Stage konfigurieren
        stage.setScene(scene);
        stage.setResizable(true);

        // Icon setzen
        Image icon = new Image(getClass().getResourceAsStream("/Logo/3SLogo.png"));
        stage.getIcons().add(icon);

        stage.setTitle("Supporter App");
        stage.show();
    }
    private void adjustInternButtonPosition(StartViewControler controller) {
        // Prüfen ob andere Buttons sichtbar sind
        boolean otherVisible = controller.getBtnGeraeteInfo().isVisible();
        
        if (otherVisible) {
            // Button bleibt in der HBox / normaler Platz
            controller.getBtn3sMitarbeiter().setTranslateY(0);
        } else {
            // Button nach unten verschieben
            double parentHeight = ((javafx.scene.layout.Region) controller.getBtn3sMitarbeiter().getParent()).getHeight();
            double buttonHeight = controller.getBtn3sMitarbeiter().getLayoutBounds().getWidth();

            
            // Abstand zum unteren Rand z.B. 20px
            controller.getBtn3sMitarbeiter().setTranslateY(parentHeight - buttonHeight - 20);
        }
    }


    public static void main(String[] args) {
        System.out.println("Supporter App startet...");
        launch(args);
    }
}
