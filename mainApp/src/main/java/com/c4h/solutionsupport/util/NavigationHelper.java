package com.c4h.solutionsupport.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Method;

public class NavigationHelper {

    public static <T> void navigateTo(String fxmlPath, Scene currentScene, Class<T> controllerClass, Runnable onBack) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Rücksprung ermöglichen, wenn Methode existiert
            Object controller = loader.getController();
            if (controllerClass.isInstance(controller)) {
                try {
                    Method method = controllerClass.getMethod("setBackCallback", Runnable.class);
                    method.invoke(controller, onBack);
                } catch (NoSuchMethodException e) {
                    System.out.println("Hinweis: Controller hat keine setBackCallback()-Methode.");
                }
            }

            applyTransition(currentScene, root);

        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static void applyTransition(Scene scene, Parent newRoot) {
        if (scene != null) {
            newRoot.setOpacity(0);
            scene.setRoot(newRoot);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        } else {
            System.out.println("⚠️ Warnung: Scene ist null – applyTransition fehlgeschlagen");
        }
    }
}
