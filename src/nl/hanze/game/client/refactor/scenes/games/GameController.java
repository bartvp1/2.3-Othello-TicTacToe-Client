package nl.hanze.game.client.refactor.scenes.games;

import nl.hanze.game.client.refactor.Main;
import nl.hanze.game.client.refactor.players.AI.AIStrategy;
import nl.hanze.game.client.refactor.players.AI.OthelloAI;
import nl.hanze.game.client.refactor.players.AI.TicTacToeAI;
import nl.hanze.game.client.refactor.players.AIPlayer;
import nl.hanze.game.client.refactor.players.Player;
import nl.hanze.game.client.refactor.scenes.Controller;

import java.io.IOException;

public abstract class GameController extends Controller {
    protected Player player1;
    protected Player player2;
    protected Player currentPlayer;

    public static void start(String ignPlayer1, String ignPlayer2, int boardSize, String game, boolean fullscreen, boolean isMultiPlayer) throws IOException {
        // corrects overlong and empty playernames
        if (ignPlayer1.length() > 10) ignPlayer1 = ignPlayer1.substring(0,11);
        else if (ignPlayer1.length() == 0) ignPlayer1 = "player1";
        if (ignPlayer2.length() > 10) ignPlayer2 = ignPlayer2.substring(0,11);
        else if (ignPlayer2.length() == 0) ignPlayer2 = "player2";

        AIStrategy aiStrategy = null;
        switch (game) {
            case "tictactoe":
                aiStrategy = new TicTacToeAI();
                break;
            case "othello":
                aiStrategy = new OthelloAI();
                break;
        }

        Player player1 = new Player(ignPlayer1);
        Player player2 = (isMultiPlayer) ? new Player(ignPlayer2) : new AIPlayer(ignPlayer2, aiStrategy);

        GameController controller = (GameController) loadScene("games/" + game + "/" + game + ".fxml");

        controller.setPlayer1(player1);
        controller.setPlayer2(player2);

        Main.primaryStage.setFullScreen(fullscreen);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
