package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.WSManager;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WSServer {
    private ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
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
            String auth = command.getAuthString();
            if (auth.startsWith("\"")) {
                auth = auth.substring(1, 21);
            }
            add(auth, session);
            ServerMessage notification = manager.joinPlayerNotify(command);
            int j = 0;
            if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                broadcastOne(session, notification);
                return;
            }
            ServerMessage loadgame = manager.loadGame(command);

            broadcastOne(session, loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            String auth = command.getAuthString();
            if (auth.startsWith("\"")) {
                auth = auth.substring(1, 21);
            }
            add(auth, session);
            ServerMessage notification = manager.joinObserverNotify(command);
            ServerMessage loadgame = manager.loadGame(command);

            broadcastOne(session, loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            ServerMessage loadgame = manager.makeMove(command);
            ServerMessage notification = manager.makeMoveNotification(command);

            broadcastAll(command.getGameID(), loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);

        } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
            ServerMessage notification = manager.leave(command);
            broadcastAllOthers(command.getGameID(), session, notification);
            session.close();
            connections.remove(command.getAuthString());
        } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            ServerMessage loadgame = manager.resign(command);
            ServerMessage notification = manager.resignMessage(command);

            broadcastAll(command.getGameID(), loadgame);
            broadcastAllOthers(command.getGameID(), session, notification);
        }
    }

    public void add(String auth, Session session) {
        Connection connection = new Connection(auth, session);
        connections.put(auth, connection);
    }

    public void remove(String auth) {
        connections.remove(auth);
    }

    public void broadcastAll(int gameID, ServerMessage message) {
        //send the message to each client
        WSManager manager = new WSManager();
        connections.forEach(12, (k, v) -> {
            try {
                if (v.getSession().isOpen()) {
                    v.getSession().getRemote().sendString(serialize(message));
                } else {
                    manager.delete(k, gameID);
                }
            } catch (Exception e) {
                //error handling
            }
        });
    }
    public void broadcastAllOthers(int gameID, Session session, ServerMessage message) {
        //send the message to each client
        WSManager manager = new WSManager();
        connections.forEach(12, (k, v) -> {
            try {
                if (v.getSession().isOpen() && v.getSession() != session) {
                    v.getSession().getRemote().sendString(serialize(message));
                } else if (v.getSession() == session) {}
                else {
                    manager.delete(k, gameID);
                }
            } catch (Exception e) {
                //error handling
            }
        });
    }

    public void broadcastOne(Session session, ServerMessage message) {
        //only sends an error to the user that made the command
        try {
            if (session.isOpen()) {
                String out = serialize(message);
                session.getRemote().sendString(out);
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

    public class Connection {
        private String auth;
        private Session session;

        Connection(String auth, Session session) {
            this.auth = auth;
            this.session = session;
        }
        String getAuth() {
            return auth;
        }
        Session getSession() {
            return session;
        }
    }
}