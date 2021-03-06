package nl.hanze.game.client.scenes.lobby;

public class LobbyModel {

    /**
     * @author Jasper van Dijken
     */

    private String gameMode = "ai";
    private String game;
    private boolean fullscreen;

    //GameMode
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getGameMode() {
        return gameMode;
    }

    //Game
    public void setGame(String game) { this.game = game; }

    public String getGame() { return game; }

    //Fullscreen
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

    public boolean getFullscreen() { return fullscreen; }


}
