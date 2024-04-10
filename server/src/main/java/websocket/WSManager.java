package websocket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.Handler;
import service.WSService;
import spark.Spark;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.Iterator;
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
        //session.getRemote().sendString("WebSocket response: " + message);
    }

    public void errorBroadcast(String host, ErrorMessage message) {

        Connection connection = connections.get(host);
        try {
            if (connection.getSession().isOpen()) {
                connection.getSession().getRemote().sendString(serialize(message));
            }
        } catch (Exception e) {
            //error handling
        }
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

    public String serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
    }
}
