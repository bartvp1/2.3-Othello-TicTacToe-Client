package nl.hanze.game.client.scenes.games.othello;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.players.AI.utils.Move;
import nl.hanze.game.client.players.Player;
import nl.hanze.game.client.players.PlayerType;
import nl.hanze.game.client.scenes.games.GameController;
import nl.hanze.game.client.scenes.games.othello.utils.OthelloBoard;
import nl.hanze.game.client.scenes.games.utils.BoardPane;
import nl.hanze.game.client.scenes.lobby.LobbyController;
import nl.hanze.game.client.scenes.menu.online.OnlineMenuController;

import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Pieter Beens
 */

public class OthelloController extends GameController {
    @FXML private Label scoreLabel1;
    @FXML private Label scoreLabel2;

    private Label graphic1;
    private Label graphic2;

    public OthelloController(OthelloModel model) {
        super(model);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        gameBoard = new OthelloBoard(model, this);
        boardGridPane.setId("OthelloBoard");
        gameBoardPane.getChildren().add(gameBoard);
        gameTitle.setText("Othello");graphic1 = new Label();
        graphic1 = new Label();
        graphic2 = new Label();
        Font font = new Font("System Bold",24);
        graphic1.setFont(font);
        graphic2.setFont(font);
        scoreLabel1.setGraphic(graphic1);
        scoreLabel2.setGraphic(graphic2);
        gameBoard.setGridLinesVisible(true);
        drawCoordinates();
    }

    public void setup() {
        String player1Name = model.getPlayer(0).getName();
        String player2Name = model.getPlayer(1).getName();
        if(player1Name.equals("You")) graphic1.setText("Your score");
        else graphic1.setText(player1Name+"'s score");

        if(player2Name.equals("You")) graphic2.setText("Your score");
        else graphic2.setText(player2Name+"'s score");

        super.setup();
    }

    @Override
    public void updateViews() {
        super.updateViews();
        updateScoreLabels();
    }

    //TODO: refactor to use GameController.move()
    @Override
    public boolean move(Move move) {
        OthelloModel model = (OthelloModel) this.model;

        if (model.isValidMove(move)) {
            forfeitButton.setDisable(true);
            model.recordMove(move); // includes nextTurn() call
            updateViews();

            return true;
        }
        return false;
    }

    public void updateScoreLabels(){
        scoreLabel1.setText(String.valueOf(model.getPlayer(0).getScore()));
        scoreLabel2.setText(String.valueOf(model.getPlayer(1).getScore()));
    }
}
