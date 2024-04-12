package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {
    public UserGameCommand(CommandType type, String authToken, int gameID, ChessMove move) {
        this.commandType = type;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = move;
        this.color = null;
    }
    public UserGameCommand(CommandType type, String authToken, int gameID, ChessGame.TeamColor color) {
        this.commandType = type;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = null;
        this.color = color;
    }
    public UserGameCommand(CommandType type, String authToken, int gameID) {
        this.commandType = type;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = null;
        this.color = null;
    }
    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;
    private final String authToken;
    private final int gameID;
    private final ChessMove move;
    private final ChessGame.TeamColor color;
    public String getAuthString() {
        return authToken;
    }
    public int getGameID() {return gameID;}
    public ChessMove getMove() {return move;}
    public ChessGame.TeamColor getColor() {return color;}
    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}