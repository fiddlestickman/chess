package websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.Handler;
import service.WSService;
import spark.Spark;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WSManager {

    private ConcurrentHashMap<String, WSManager.Connection> connections;

    public WSManager () {
        this.connections = new ConcurrentHashMap<>();
    }
    //have a list of all the connections that matter
    //pair connections with usernames somehow
    //when you try to broadcast, look up with DAOs the relevant users
    //if there are connections (there should be), broadcast to each of those paired sessions
    //also use DAOs to update who's on what game
    //and game state
    //make sure game moves are legal

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void loadGame(int gameID, String host, LoadGameMessage message) {

        WSService service = new WSService();
        try {
            ArrayList<String> users = service.getUsers(gameID); //these are all the users, both watching and playing
        } catch (Exception e) {

        }

        for (var c : connections.values()) {
            if (c.getSession().isOpen()) {

            }
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }
    public void notify(int gameID, String host, NotificationMessage message) {

        for (var c : connections.values()) {
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {

            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {

            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {

            }
            if (c.getSession().isOpen()) {

            } else {
                removeList.add(c);
            }
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    public void errorBroadcast(int gameID, String host, ErrorMessage message) {

        for (var c : connections.values()) {
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {

            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {

            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {

            }
            if (c.getSession().isOpen()) {

            } else {
                removeList.add(c);
            }
        }
        session.getRemote().sendString("WebSocket response: " + message);
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
