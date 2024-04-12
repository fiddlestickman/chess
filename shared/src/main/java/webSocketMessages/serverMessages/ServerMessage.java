package webSocketMessages.serverMessages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private final String errorMessage;
    private final ChessGame game;
    private final String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        if (type == ServerMessageType.NOTIFICATION) {
            this.message = message;
            this.errorMessage = null;
        } else if (type == ServerMessageType.ERROR) {
            this.message = null;
            this.errorMessage = message;
        } else {
            this.message = null;
            this.errorMessage = null;
        }
        game = null;
    }
    public ServerMessage(ServerMessageType type, ChessGame game) {
        this.serverMessageType = type;
        this.game = game;
        message = null;
        errorMessage = null;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
