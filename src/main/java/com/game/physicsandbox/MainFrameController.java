package com.game.physicsandbox;

import com.game.physicsandbox.physics.control.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

@Setter
public class MainFrameController {
    private Controller controller;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onPadClicked(MouseEvent event) {
        controller.padClicked(event);
    }

    @FXML
    protected void onKeyPressed(KeyEvent event) {
        controller.keyPressed(event);
    }
}