package websocket;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.GameHandler;
import server.Handler;
import server.UserHandler;
import spark.Spark;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {
    private final WSManager connections = new WSManager();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        //do something after creating a new websocket connection
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws Exception {
        //close the connection after doing things
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = (UserGameCommand) userHandle.deserialize(message, UserGameCommand.class);
        if (command.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {


            JoinPlayerCommand temp = (JoinPlayerCommand) command;
            temp.getAuthString();
            temp.getGameID();
            temp.getColor();

            //send a notification to all other clients that a player is joining, and what color
        } else if (command.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "idk");
            String out = handler.serialize(notification);
            session.getRemote().sendString(out);
            //send a notification to all other clients that a player is joining as an observer
        } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            //verify validity of move
            //update game in the database
            //send a load game message to all clients (including root) with updated game
            //send notification to all other clients what move was made
        } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            //remove client from the game
            //send a notification to all other clients that they left
        } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            //mark the game as over - update game in the database
            //send a notification to all clients that game is over, and all others than root that client has resigned
        }
        session.getRemote().sendString("WebSocket response: " + message);
        //put the things that handle incoming strings here - it's a really good
        //connection point for taking client commands, maybe deserialize and go from there
        //you can use session.getRemote().sendString(String str) to send a serialized response
    }

    private class Connection {
        private String username;
        private Session session;

        Connection(String username, Session session) {
            this.username = username;
            this.session = session;
        }

        String getUsername() {
            return username;
        }
        Session getSession() {
            return session;
        }
    }
}