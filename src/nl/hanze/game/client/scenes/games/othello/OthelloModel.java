package nl.hanze.game.client.scenes.games.othello;

import javafx.application.Platform;
import nl.hanze.game.client.Main;
import nl.hanze.game.client.players.AI.OthelloAIHard;
import nl.hanze.game.client.players.AI.utils.Move;
import nl.hanze.game.client.players.Player;
import nl.hanze.game.client.players.PlayerType;
import nl.hanze.game.client.scenes.games.GameModel;
import nl.hanze.game.client.scenes.games.utils.Field;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pieter Beens
 */

public class OthelloModel extends GameModel {
    public OthelloModel() {
        super(8);
    }

    @Override
    public void setup() {
        // sets up the opening state of the game with two stones of each color in the middle of the board
        getField(getBoardSize()/2, getBoardSize()/2).setOwner(getPlayer(0));
        getField(getBoardSize()/2 - 1, getBoardSize()/2 - 1).setOwner(getPlayer(0));
        getField(getBoardSize()/2 - 1, getBoardSize()/2).setOwner(getPlayer(1));
        getField(getBoardSize()/2, getBoardSize()/2 - 1).setOwner(getPlayer(1));

        players[0].changeScore(2);
        players[1].changeScore(2);

        super.setup();
    }

    @Override
    public void recordMove(Move move) {
        Field targetField = board[move.getRow()][move.getColumn()];

        // stones are captured first...
        int captureTally = enactCaptures(targetField);
        getActivePlayer().changeScore(captureTally);
        players[(turnCounter+1)%2].changeScore(-captureTally);

        // ...before the stone is placed: this is because placing the stone first would affect enactCaptures() > getCaptures() calculation
        targetField.setOwner(move.getPlayer());
        updateRecentMove(targetField);
        getActivePlayer().changeScore(1);


        System.out.println(getActivePlayer().getName() + " moved to " + Main.alphabet[move.getRow()] + move.getColumn() + ", capturing " + captureTally + " Field(s)!");

        nextTurn(false);
    }

    @Override
    public void updateFieldValidity() {
        for (Field[] row : board) {
            for (Field field : row) {
                if (getCaptures(field).isEmpty()) field.setValidity(false);
                else field.setValidity(true);
            }
        }
    }

    // Note this is not an override! The parent class' nextTurn() accepts no parameters...
    public void nextTurn(boolean lastTurnWasSkipped) {
        super.nextTurn();

        //Default of skippedTurnText is ""
        if (getActivePlayer().getPlayerType() != PlayerType.LOCAL) GameModel.skippedTurnText = "";
        //When the last turn was skipped, set skippedTurnText

        if (lastTurnWasSkipped) GameModel.skippedTurnText = getInactivePlayer().getName() + " skipped a turn!";

        if (!turnHasMoves() && lastTurnWasSkipped) {
            if (!Main.serverConnection.hasConnection()) {
                System.out.println("Neither player was able to move, so the game has ended!");
                Platform.runLater(() -> endGame(false));
            }
            return;
        }

        if (!turnHasMoves()) {
            System.out.println(getActivePlayer().getName() + " was unable to move, and had to skip their turn!");
            this.nextTurn(true);
        }
    }

    public boolean turnHasMoves() {
        for (Field[] row : board) {
            for (Field field : row) {
                if (field.getValidity()) return true;
            }
        }
        return false;
    }

    public Stack<Field> getCaptures(Field field) {
        Stack<Field> allCaptures = new Stack<>();

        // occupied Fields are never a valid move - immediately return empty Stack
        if (field.getOwner() != null) {
            return allCaptures;
        }

        for (int[] direction : GameModel.DIRECTIONS) {

            Stack<Field> capturesInThisDirection = new Stack<>();

            int currentRowID = field.getRowID();
            int currentColumnID = field.getColumnID();

            while (true) {
                int nextRowID = currentRowID + direction[0];
                int nextColumnID = currentColumnID + direction[1];

                // start with all cases where a direction will never make a move valid, so no further checks are necessary
                // directly borders a place outside the board > NO CAPTURES HERE
                if (nextRowID >= board.length || nextColumnID >= board.length || nextRowID < 0 || nextColumnID < 0) break;
                Field nextField = board[nextRowID][nextColumnID];

                // directly borders an empty Field > NO CAPTURES HERE
                if (nextField.getOwner() == null) break;
                // directly borders a Field with the same owner > NO CAPTURES HERE
                if (nextField.getOwner() == getActivePlayer() && capturesInThisDirection.size() == 0) break;

                // success condition: if the loop has looped at least once and the next Field has the same owner, the captures are added to allCaptures
                if (nextField.getOwner() == getActivePlayer()) {
                    while (!capturesInThisDirection.isEmpty()) {
                        allCaptures.push(capturesInThisDirection.pop());
                    }
                    break;
                }

                // if the next Field has another owner, this might be a capture!
                if (nextField.getOwner() == getInactivePlayer()) {
                    currentRowID = nextRowID;
                    currentColumnID = nextColumnID;
                    capturesInThisDirection.push(nextField);
                }
                // if the loop got this far, it means there is potential for a capture; the next iteration will check one Field further...
            }
        }
        // returns captures in all directions
        return allCaptures;
    }

    public int enactCaptures(Field field) {
        int captureTally = 0;
        Stack<Field> captures = getCaptures(field);
        for (Field capturedField : captures) {
            capturedField.setOwner(getActivePlayer());
            captureTally++;
        }
        return captureTally;
    }

    public void updateRecentMove(Field recentMove) {
        for (Field[] row : board) {
            for (Field field : row) {
                field.unsetRecentMove();
            }
        }
        recentMove.setRecentMove();
    }

    public static int getBoardScore(Field[][] board, Player player, Player opponent) {
        int score = 0;

        // SOURCE 1: number of valid moves ==========================================================================
        for (Field[] row : board) {
            for (Field field : row) {
                if (field.getValidity()) score += 1;
            }
        }

        for (Field[] row : board) {
            for (Field field : row) {
                if (!OthelloAIHard.getCaptures(field, board, opponent, player).isEmpty()) score += -1;
            }
        }

        // SOURCE 2: stable stones ================================================================================

        //TODO: 15 points per stable stone

//        int i = 0;
//        int x = 0;
//        while (true) {
//            try{
//                if (board[x][i].getOwner() == player) score += 15;
//                else {
//                    i = -1;
//                    x++;
//                }
//            } catch (NullPointerException ignore) {}
//            i++;
//        }
//
//        try {
//            if (board[0][0].getOwner() == player) {
//                score += stablePoints(0, 0, 1);
//            } else if (board[0][0].getOwner() == opponent) {
//                score -= stablePoints(0, 0, 1);
//            }
//        } catch (NullPointerException ignore) {}
//
//        for (Field[] row : board) {
//            for (Field field : row) {
//
//            }
//        }
//
//        for (Field[] row : board) {
//            for (Field field : row) {
//                if (isStable(field)) {
//                    if (field.getOwner() == player) score += 15;
//                    else score -= 15;
//                }
//            }
//        }

        // SOURCE 3: corners and x-corners ========================================================================


        // x-cornerscore is added the unless associated corner is occupied

        //TODO: cornerscore = 10 for every distance from stable stones (yours or opponent's)
        // amazing depending on empty fields to build with...

        if (board[0][0].getOwner() == null) {
            if (board[1][1].getOwner() != null) {
                if (board[1][1].getOwner() == player) score += -100;
                else score += +100;
            }
        }
        else if (board[0][0].getOwner() == player) score += 200;
        else score += -200;

        if (board[7][0].getOwner() == null) {
            if (board[6][1].getOwner() != null) {
                if (board[6][1].getOwner() == player) score += -100;
                else score += +100;
            }
        }
        else if (board[7][0].getOwner() == player) score += 200;
        else score += -200;

        if (board[0][7].getOwner() == null) {
            if (board[1][6].getOwner() != null) {
                if (board[1][6].getOwner() == player) score += -100;
                else score += +100;
            }
        }
        else if (board[0][7].getOwner() == player) score += 200;
        else score += -200;

        if (board[7][7].getOwner() == null) {
            if (board[6][6].getOwner() != null) {
                if (board[6][6].getOwner() == player) score += -100;
                else score += +100;
            }
        }
        else if (board[7][7].getOwner() == player) score += 200;
        else score += -200;

        // SOURCE 4: edges ===================== =====================================================================

        // every edge field is worth x points
        for (int i = 2; i < 6; i++) {
            if (board[0][i].getOwner() != null) {
                if (board[0][i].getOwner() == player) score += 20;
                else score += -20;
            }
            if (board[i][0].getOwner() != null) {
                if (board[i][0].getOwner() == player) score += 20;
                else score += -20;
            }
            if (board[7][i].getOwner() != null) {
                if (board[7][i].getOwner() == player) score += 20;
                else score += -20;
            }
            if (board[i][7].getOwner() != null) {
                if (board[i][7].getOwner() == player) score += 20;
                else score += -20;
            }
        }

        // build Strings based on edge fields
        String[] edgeStrings = new String[]{"","","",""};

        for (int j = 0; j < 8; j++) {
            if (board[0][j].getOwner() == null) edgeStrings[0] += "-";
            else edgeStrings[0] += (board[0][j].getOwner() == player) ? "X" : "O";
        }
        //System.out.println(edgeStrings[0]);
        for (int j = 0; j < 8; j++) {
            if (board[j][0].getOwner() == null) edgeStrings[1] += "-";
            else edgeStrings[1] += (board[j][0].getOwner() == player) ? "X" : "O";
        }
        //System.out.println(edgeStrings[1]);
        for (int j = 0; j < 8; j++) {
            if (board[7][j].getOwner() == null) edgeStrings[2] += "-";
            else edgeStrings[2] += (board[7][j].getOwner() == player) ? "X" : "O";
        }
        //System.out.println(edgeStrings[2]);
        for (int j = 0; j < 8; j++) {
            if (board[j][7].getOwner() == null) edgeStrings[3] += "-";
            else edgeStrings[3] += (board[j][7].getOwner() == player) ? "X" : "O";
        }
        //System.out.println(edgeStrings[3]);

        // check regex patterns
        for (String edge : edgeStrings) {
            if (Pattern.matches("-+O+X+-+", edge)) {
                score += -20;
            }
            else if (Pattern.matches("-+X+O+-+", edge)) {
                score += -20;
            }
            if (Pattern.matches("^-X+O", edge)) {
                score += -120;
            }
            else if (Pattern.matches("OX+-$", edge)) {
                score += -120;
            }
            if (Pattern.matches("^-X+-X+", edge)) {
                score += -80;
            }
            else if (Pattern.matches("X+-X+-$", edge)) {
                score += -80;
            }
            if (Pattern.matches("^--+X+-X+", edge)) {
                score += -20;
            }
            else if (Pattern.matches("X+-X+-+-$", edge)) {
                score += -20;
            }
            if (Pattern.matches("^-X+-X+O+", edge)) {
                score += +40;
            }
            else if (Pattern.matches("O+X+-X+-$", edge)) {
                score += +40;
            }
            if (Pattern.matches("^-O+X+-X+", edge)) {
                score += +120;
            }
            else if (Pattern.matches("X+-X+O+-$", edge)) {
                score += +120;
            }
            if (edge.equals("--X--X--")) score+= 25;
        }


        // FOR INCREASED PERFORMANCE:
//        Pattern simpleCapture = Pattern.compile("^-+O+X+");
//        Matcher m = Pattern.matches("^-+O+X+", "hoi");
//        boolean b = m.matches();

        return score;
    }
}
