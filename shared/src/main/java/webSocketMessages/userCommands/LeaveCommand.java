package webSocketMessages.userCommands;

import java.util.Objects;

public class LeaveCommand extends UserGameCommand{
    private final int gameID;
    public LeaveCommand(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
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
        LeaveCommand that = (LeaveCommand) o;
        return getGameID() == that.getGameID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGameID());
    }
}
