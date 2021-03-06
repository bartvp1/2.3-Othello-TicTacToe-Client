package nl.hanze.game.client.scenes.games.utils;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.players.AI.utils.Move;

/**
 * @author Pieter Beens
 */
public class FieldButton extends Button {

    private final int rowID;
    private final int columnID;

    public FieldButton(int rowID, int columnID) {
        this.rowID = rowID;
        this.columnID = columnID;

        setOnMouseClicked(e -> {
            BoardPane board = (BoardPane) getParent();
            Move move = new Move(board.getController().getActivePlayer(), getRowID(), getColumnID());
            boolean wasValid = board.getController().move(move);

            if(!wasValid) return;

            if (!Main.serverConnection.hasConnection() && !board.getController().getModel().hasGameEnded())
                board.getController().acceptNewMoves();
            else
                Main.serverConnection.move(Move.cordsToCell(move.getRow(), move.getColumn(), board.getController().getModel().getBoardSize()));
        });
    }


    public int getRowID() {
        return rowID;
    }

    public int getColumnID() {
        return columnID;
    }
}
