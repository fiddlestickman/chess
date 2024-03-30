package webSocketMessages.serverMessages;

import chess.ChessGame;

import java.util.Objects;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;
    public ErrorMessage (ServerMessageType type, String message) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(getErrorMessage(), that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getErrorMessage());
    }
}
