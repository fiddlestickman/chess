package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.Handler;
import service.WSService;
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
            NotificationMessage notification = manager.joinPlayerNotify(command);
            LoadGameMessage loadgame = manager.loadGame(command);

            broadcastOne(session, loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            NotificationMessage notification = manager.joinObserverNotify(command);
            LoadGameMessage loadgame = manager.loadGame(command);

            broadcastOne(session, loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            LoadGameMessage loadgame = manager.makeMove(command);
            NotificationMessage notification = manager.makeMoveNotification(command);

            broadcastAll(command.getGameID(), loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            NotificationMessage notification = manager.leave(command);
            String username = manager.getLeave(command);

            broadcastAllOthers(command.getGameID(), session, notification);
            session.close();
            connections.remove(username);
        } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            LoadGameMessage loadgame = manager.resign(command);
            NotificationMessage notification = manager.resignMessage(command);

            broadcastAll(command.getGameID(), loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);
        }
    }

    public void add(String username, Session session) {
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcastAll(int gameID, ServerMessage message) {
        //send the message to each client

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
    public void broadcastAllOthers(int gameID, Session session, ServerMessage message) {

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
            if (!connection.session.equals(session)) {
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

    public void broadcastOne(Session session, ServerMessage message) {
        //only sends an error to the user that made the command
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(serialize(message));
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