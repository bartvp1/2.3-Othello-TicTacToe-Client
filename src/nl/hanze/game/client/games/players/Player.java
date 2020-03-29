package nl.hanze.game.client.games.players;

import nl.hanze.game.client.Application;
import nl.hanze.game.client.util.Move;

public abstract class Player {

    private String name;
    private int score;
    private String color;
    private String textcolor;

    public Player(String ign) {
        this.name = name;
        score = 0;
        color = Application.BTN_COLOR;
        textcolor = Application.BTN_TEXT_COLOR;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String[] getColors() {
        return new String[]{color, textcolor};
    }

    public void setStartingColors() {
        color = Application.BTN_ACTIVE_COLOR;
        textcolor = Application.BTN_ACTIVE_TEXT_COLOR;
    }

    public String getName() {
        return name;
    }

    // TODO: I don't know about this one...
    // abstract public Move move(char[][] board);

}
