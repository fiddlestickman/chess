package webSocketMessages.userCommands;

import chess.ChessGame;

import java.util.Objects;

public class JoinObserverCommand extends UserGameCommand {
    private final int gameID;
    public JoinObserverCommand(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JoinObserverCommand that = (JoinObserverCommand) o;
        return getGameID() == that.getGameID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGameID());
    }
}