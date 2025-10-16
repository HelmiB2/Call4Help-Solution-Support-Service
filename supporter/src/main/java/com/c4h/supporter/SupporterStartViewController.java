package com.c4h.supporter;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

public class SupporterStartViewController {

    @FXML
    private VBox moduleContainer;

    public void initialize() {
        // Nur diese Module laden
        loadModule("Chat");
		
		loadModule("Mitarbeiter"); loadModule("Ticket");
		
    }

    private void loadModule(String moduleName) {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + "startDesktop" + ".fxml"));
            moduleContainer.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
