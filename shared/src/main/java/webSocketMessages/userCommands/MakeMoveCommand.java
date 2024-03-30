package webSocketMessages.userCommands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;
    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }
    public int getGameID() {
        return gameID;
    }
    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MakeMoveCommand that = (MakeMoveCommand) o;
        return getGameID() == that.getGameID() && Objects.equals(getMove(), that.getMove());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGameID(), getMove());
    }
}
