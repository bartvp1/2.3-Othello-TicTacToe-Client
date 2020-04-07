package nl.hanze.game.client.scenes.games;

import nl.hanze.game.client.Main;
import nl.hanze.game.client.players.AI.AIStrategy;
import nl.hanze.game.client.players.AI.OthelloAIEasy;
import nl.hanze.game.client.players.AI.TicTacToeAI;
import nl.hanze.game.client.players.AIPlayer;
import nl.hanze.game.client.players.Player;
import nl.hanze.game.client.players.PlayerType;
import nl.hanze.game.client.scenes.Controller;

import java.io.IOException;
import java.util.Map;

/**
 * @author Roy Voetman
 */
public class GameLoader {
    public static GameController startOnline(Map<String, String> args, boolean fullscreen, PlayerType playerType) throws IOException {
        String game = args.get("GAMETYPE").toLowerCase().replace("-", "");
        game = game.equals("reversi") ? "othello" : game;

        Player player1;
        if (playerType == PlayerType.AI) {
            AIStrategy aiStrategy = determineAIStrategy(game);

            player1 = new AIPlayer(GameModel.serverName, playerType, aiStrategy);
        } else {
            player1 = new Player(GameModel.serverName, playerType);
        }

        Player player2 = new Player(args.get("OPPONENT"), PlayerType.REMOTE);

        GameController controller = (GameController) Controller.loadScene("games/" + game + "/" + game + ".fxml");

        GameModel model = controller.getModel();

        // Determine which player should begin (model.player1 always starts)
        model.setPlayer1(args.get("PLAYERTOMOVE").equals(GameModel.serverName) ? player1 : player2);
        model.setPlayer2(args.get("PLAYERTOMOVE").equals(GameModel.serverName) ? player2 : player1);

        return start(controller, fullscreen);
    }

    public static void startOffline(String ignPlayer1, String ignPlayer2, String game, boolean fullscreen, boolean isMultiPlayer) throws IOException {
        game = game.toLowerCase().replace("-", "");

        AIStrategy aiStrategy = determineAIStrategy(game);

        Player player1 = new Player(ignPlayer1, PlayerType.LOCAL);
        Player player2 = (isMultiPlayer) ? new Player(ignPlayer2, PlayerType.LOCAL) : new AIPlayer(ignPlayer2, PlayerType.AI, aiStrategy);

        GameController controller = (GameController) GameController.loadScene("games/" + game + "/" + game + ".fxml");

        GameModel model = controller.getModel();
        model.setPlayer1(player1);
        model.setPlayer2(player2);

        start(controller, fullscreen);
    }

    private static GameController start(GameController controller, boolean fullscreen) {
        controller.setup();

        Main.primaryStage.setFullScreen(fullscreen);

        return controller;
    }

    private static AIStrategy determineAIStrategy(String game) {
        AIStrategy aiStrategy = null; //TODO: support multiple AI's/difficulties per game
        switch (game) {
            case "tictactoe":
                aiStrategy = new TicTacToeAI();
                break;
            case "othello":
                aiStrategy = new OthelloAIEasy();
                break;
        }

        return aiStrategy;
    }
}
