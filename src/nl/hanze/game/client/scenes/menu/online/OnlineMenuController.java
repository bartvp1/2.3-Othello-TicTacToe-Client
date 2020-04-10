package nl.hanze.game.client.scenes.menu.online;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.scenes.Controller;
import nl.hanze.game.client.scenes.games.GameModel;
import nl.hanze.game.client.scenes.lobby.LobbyController;

import java.io.IOException;

public class OnlineMenuController extends Controller {

    public static String playerName;

    @FXML
    private TextField name;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private Text errorMsg;

    @FXML
    private void btnGoBack(ActionEvent event) throws IOException {
        goBack();//loadScene("start/start.fxml");
    }

    /**
     * @author Jasper van Dijken
     */
    @FXML
    private void connect(ActionEvent event) throws IOException {

        //error indicator
        boolean error = false;

        //Check if fields are filled in
        if (name.getText().isEmpty() | ip.getText().isEmpty() | port.getText().isEmpty()) {
            errorMsg.setText("Please fill in all fields");
            error = true;
        }
        //if so, check for valid input
        else {

            //check for valid ip
            if (!checkIP(ip.getText())) {
                errorMsg.setText("Invalid IP-Address");
                error = true;
            }

            //check for valid port number
            if (!port.getText().matches("^[0-9]*$")) {
                errorMsg.setText("Invalid Port Number");
                error = true;
            }
        }

        // no errors? try to connect to the server
        if (!error) {
            if (!Main.serverConnection.connect(ip.getText(), Integer.parseInt(port.getText()))) {
                errorMsg.setText("Connection refused");
                return;
            }
            playerName = name.getText();
            Main.serverConnection.addObserver(this);
            Main.serverConnection.login(playerName);

        }

    }

    /**
     * @author Jasper van Dijken
     */
    //method that checks for a valid ip address
    public static boolean checkIP (String ip) {

        //checking the length, split at each dot
        String[] split = ip.split("\\.");
        if (split.length != 4) {
            return false;
        }

        //check if the numbers are within the boundaries
        for (String element : split) {
            int i = Integer.parseInt(element);
            if ((i < 0) || (i > 255)) {
                return false;
            }
        }

        if (ip.startsWith(".")) {
            return false;
        }
        if (ip.endsWith(".")) {
            return false;
        }

        return true;
    }

    /**
     * @author Roy Voetman
     */
    @Override
    public void update(String response) {
        super.update(response);

        if (response.equals("OK")) {

            GameModel.serverName = name.getText();
            Platform.runLater(() -> {
                try {
                    LobbyController.loggedInAs = playerName;
                    loadScene("lobby/lobby.fxml");
                } catch (IOException ignore) { ignore.printStackTrace(); }
            });
        }
    }
}
