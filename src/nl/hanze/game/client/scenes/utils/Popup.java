package nl.hanze.game.client.scenes.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.scenes.Controller;

import java.io.IOException;

/**
 * @source http://www.learningaboutelectronics.com/Articles/How-to-create-a-pop-up-window-in-JavaFX.php
 */
public class Popup {
    public static void display(String msg) {
        Stage popupwindow = new Stage();

        popupwindow.initStyle(StageStyle.UNDECORATED);

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Oeps, something went wrong");

        Label label1= new Label(msg);
        Button button1= new Button("Close");
        button1.setOnAction(e -> popupwindow.close());

        VBox layout= new VBox(10);

        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, 260, 100);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    public static void display(String msg, String title, int width, int height) {
        Stage popupwindow = new Stage();

        popupwindow.initStyle(StageStyle.UNDECORATED);

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle(title);

        Label label1= new Label(msg);
        label1.setWrapText(true);
        Button button1= new Button("Return to Menu");
        button1.setOnAction(e -> {
            popupwindow.close();
            try {
                if (Main.serverConnection.hasConnection()) {
                    Controller.loadScene("lobby/lobby.fxml");
                } else {
                    Controller.loadScene("start/start.fxml");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        VBox layout= new VBox(10);

        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, width, height);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }
}

