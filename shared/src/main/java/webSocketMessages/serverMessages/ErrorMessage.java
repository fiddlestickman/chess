package webSocketMessages.serverMessages;

import chess.ChessGame;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage (ServerMessageType type, String message) {
        super(type);
        this.serverMessageType = ServerMessageType.ERROR;
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
