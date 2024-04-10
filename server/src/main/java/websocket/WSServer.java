package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.GameHandler;
import server.Handler;
import server.UserHandler;
import service.WSService;
import spark.Spark;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {

    private ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();;

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
        Handler handler = new Handler();
        WSManager manager = new WSManager();

        UserGameCommand command = (UserGameCommand) handler.deserialize(message, UserGameCommand.class);
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

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void loadGame(int gameID, LoadGameMessage message) {
        //send the current game state to each client

        ArrayList<String> users;
        WSService service = new WSService();
        try {
            users = service.getUsers(gameID); //these are all the users, both watching and playing
        } catch (Exception e) {
            //error handling
            return;
        }

        Iterator<String> iter = users.iterator();

        while(iter.hasNext()) {
            String next = iter.next();
            Connection connection = connections.get(next);
            try {
                if (connection.getSession().isOpen()) {
                    connection.getSession().getRemote().sendString(serialize(message));
                } else {
                    service.delete(next, gameID);
                }
            } catch (Exception e) {
                //error handling
            }
        }

    }
    public void notify(int gameID, String host, NotificationMessage message) {

        ArrayList<String> users;
        WSService service = new WSService();
        try {
            users = service.getUsers(gameID); //these are all the users, both watching and playing
        } catch (Exception e) {
            //error handling
            return;
        }

        Iterator<String> iter = users.iterator();

        while(iter.hasNext()) {
            String next = iter.next();
            Connection connection = connections.get(next);
            if (!next.equals(host)) {
                try {
                    if (connection.getSession().isOpen()) {
                        connection.getSession().getRemote().sendString(serialize(message));
                    } else {
                        service.delete(next, gameID);
                    }
                } catch (Exception e) {
                    //error handling
                }
            }
        }
    }

    public void errorBroadcast(String host, ErrorMessage message) {
        //only sends an error to the user that made the command
        Connection connection = connections.get(host);
        try {
            if (connection.getSession().isOpen()) {
                connection.getSession().getRemote().sendString(serialize(message));
            }
        } catch (Exception e) {
            //error handling
        }
    }

    public String serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
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