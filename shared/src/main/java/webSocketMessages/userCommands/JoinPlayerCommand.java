package webSocketMessages.userCommands;

import chess.ChessGame;

import java.util.Objects;

public class JoinPlayerCommand extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor color;
    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.color = color;
    }
    public int getGameID() {
        return gameID;
    }
    public ChessGame.TeamColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JoinPlayerCommand that = (JoinPlayerCommand) o;
        return getGameID() == that.getGameID() && getColor() == that.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGameID(), getColor());
    }
}
