package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage{
    String message;
    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
