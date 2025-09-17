package com.c4h.solutionsupport;

import com.c4h.solutionsupport.util.Logger;
import com.c4h.solutionsupport.util.LoggerOutputStream;
import com.c4h.solutionsupport.util.PlatformHelper;
import com.c4h.solutionsupport.util.ErrorLoggerAndroid;
import com.c4h.solutionsupport.util.ErrorLoggerDesktop;
import com.c4h.solutionsupport.util.ILogger;
import com.c4h.solutionsupport.update.UpdateChecker;
import com.c4h.solutionsupport.update.UpdateControler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.PrintStream;

/**
 * Hauptklasse der Call-for-Help-Anwendung.
 * <p>
 * Diese Klasse startet die JavaFX-Anwendung, lädt die Start-View (Desktop oder Android)
 * und prüft optional auf Updates im Hintergrund.
 * </p>
 */
public class MainC4H extends Application {

    /**
     * Startet die JavaFX-Anwendung und zeigt die Start-View.
     * Führt außerdem einen Hintergrund-Thread für den Update-Check aus.
     *
     * @param stage Die primäre Stage für die Anwendung.
     */
    @Override
    public void start(Stage stage) {
        Logger.info("Starte Start-View");

        boolean isAndroid = PlatformHelper.isAndroid();

        // Start-View laden
        @SuppressWarnings("unused")
        Parent startRoot = loadStartView(stage, isAndroid);
        stage.show();

        // Update-Check im Hintergrund
        new Thread(() -> {
            try {
                boolean updateAvailable = UpdateChecker.checkVersion();
                if (updateAvailable) {
                    Platform.runLater(() -> showUpdateView(stage));
                }
            } catch (Exception e) {
                Logger.error("Fehler beim Update-Check", e);
            }
        }).start();
    }

    /**
     * Zeigt die Update-View als modales Fenster an.
     *
     * @param owner Die Stage, die als Eigentümer des modalen Fensters dient.
     */
    private void showUpdateView(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/update.fxml"));
            Parent updateRoot = loader.load();

            UpdateControler controller = loader.getController();
            controller.setBackCallback(() -> owner.toFront());

            Stage updateStage = new Stage();
            updateStage.initOwner(owner);
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.setScene(new Scene(updateRoot, 600, 800));
            updateStage.show();

            Image icon = new Image(getClass().getResourceAsStream("/Logo/3SLogo.png"));
            updateStage.getIcons().add(icon);

        } catch (Exception e) {
            Logger.error("Fehler beim Anzeigen der Update-View", e);
        }
    }

    /**
     * Lädt die Start-View für Desktop oder Android und setzt die Szene auf die Stage.
     *
     * @param stage     Die primäre Stage.
     * @param isAndroid True, falls die Anwendung auf Android läuft.
     * @return Das Root-Element der geladenen FXML-Datei oder null bei Fehlern.
     */
    private Parent loadStartView(Stage stage, boolean isAndroid) {
        try {
            String fxmlPath = isAndroid ? "/startMobile.fxml" : "/startDesktop.fxml";
            Logger.info("Lade FXML-Datei: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene;

            if (isAndroid) {
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                double width = screenBounds.getWidth();
                double height = screenBounds.getHeight();
                scene = new Scene(root, width, height);
                Logger.info("Starte Anwendung im Android-Modus mit Bildschirmgröße " + width + "x" + height);
            } else {
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

                root.getStyleClass().add(scaleClass);
                Image icon = new Image(getClass().getResourceAsStream("/Logo/3SLogo.png"));
                stage.getIcons().add(icon);

                scene = new Scene(root, sceneWidth, sceneHeight);
                Logger.info("Starte Anwendung im Desktop-Modus mit Fenstergröße " + sceneWidth + "x" + sceneHeight);
                Logger.info("Setze CSS-Skalierungsklasse: " + scaleClass);
            }

            scene.getStylesheets().add(getClass().getResource("/Style/desktop.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);

            return root;

        } catch (Exception e) {
            Logger.error("Fehler beim Laden der Start-View", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Leitet die Standard-Konsole (System.out und System.err) auf die Logger-Ausgabe um.
     */
    private static void redirectConsoleOutput() {
        ILogger loggerImpl = getLoggerImplementation();

        try {
            System.setOut(new PrintStream(new LoggerOutputStream(loggerImpl, "INFO"), true, "UTF-8"));
            System.setErr(new PrintStream(new LoggerOutputStream(loggerImpl, "ERROR"), true, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Liefert die passende Logger-Implementierung für die Plattform.
     *
     * @return Logger für Android oder Desktop.
     */
    private static ILogger getLoggerImplementation() {
        if (PlatformHelper.isAndroid()) {
            return new ErrorLoggerAndroid();
        } else {
            return new ErrorLoggerDesktop();
        }
    }

    /**
     * Hauptmethode zum Starten der Anwendung.
     * Setzt Logger, leitet Konsole um und startet JavaFX.
     *
     * @param args Kommandozeilenargumente (nicht verwendet)
     */
    public static void main(String[] args) {
        Logger.setLogger(getLoggerImplementation());
        redirectConsoleOutput();
        launch(args);
    }
}
