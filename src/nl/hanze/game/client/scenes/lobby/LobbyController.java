package nl.hanze.game.client.scenes.lobby;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.scenes.Controller;
import nl.hanze.game.client.scenes.utils.PlayerRow;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController extends Controller implements Initializable {
    @FXML
    public TableView<PlayerRow> playersTable;

    @FXML
    public TableColumn gamesColumn;

    @FXML
    public TableColumn nameColumn;

    private String gameListString = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.client.getGameList();
        Main.client.getPlayerList();

        nameColumn.prefWidthProperty().bind(playersTable.widthProperty().multiply(0.8));
        gamesColumn.prefWidthProperty().bind(playersTable.widthProperty().multiply(0.2));
    }

    @FXML
    private void btnLogout(ActionEvent event) throws IOException {
        Main.client.logout();

        goBack();
    }

    @Override
    public void update(String response) {
        super.update(response);
    }

    @Override
    public void updateGameList(List<String> list) {
        super.updateGameList(list);

        gameListString = String.join(", ", list);
    }

    @Override
    public void updatePlayerList(List<String> list) {
        super.updatePlayerList(list);

        for (String player : list) {
            playersTable.getItems().add(new PlayerRow(player, gameListString));
        }
    }
}
