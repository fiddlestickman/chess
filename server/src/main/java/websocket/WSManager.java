package websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.Handler;
import service.WSService;
import spark.Spark;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class WSManager {


    public WSManager () {
    }

    public NotificationMessage joinPlayerNotify(JoinPlayerCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        ChessGame.TeamColor color = command.getColor();
        String auth = command.getAuthString();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }

        GameData gamedata = gameDAO.readGameID(gameID);
        String strcolor = null;

        if (color == ChessGame.TeamColor.WHITE) {
            strcolor = "white";
            if (!Objects.equals(gamedata.whiteUsername(), authdata.username())) {
                throw new DataAccessException("authtoken didn't match white player");
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            strcolor = "black";
            if (!Objects.equals(gamedata.blackUsername(), authdata.username())) {
                throw new DataAccessException("authtoken didn't match black player");
            }
        } else {
            throw new DataAccessException("No color in join player command");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Player ");
        builder.append(authdata.username());
        builder.append(" joining on the ");
        builder.append(strcolor);
        builder.append("team\n");

        String join = builder.toString();

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }

    public NotificationMessage joinObserverNotify(JoinObserverCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        WatchDAO watchDAO = SQLWatchDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }

        WatchData watch = watchDAO.findWatch(authdata.username(), gameID);

        if (watch == null) {
            throw new DataAccessException("No watcher with that authtoken found");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Observer ");
        builder.append(authdata.username());
        builder.append(" has joined\n");

        String join = builder.toString();

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }

    public LoadGameMessage loadGame(JoinPlayerCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        GameDAO gameDAO = SQLGameDAO.getInstance();
        GameData gamedata = gameDAO.readGameID(gameID);

        if (gamedata == null) {
            throw new DataAccessException("No game found with given gameID");
        }
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gamedata.game());
    }
    public LoadGameMessage loadGame(JoinObserverCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        GameDAO gameDAO = SQLGameDAO.getInstance();
        GameData gamedata = gameDAO.readGameID(gameID);

        if (gamedata == null) {
            throw new DataAccessException("No game found with given gameID");
        }
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gamedata.game());
    }


    public LoadGameMessage makeMove() {
        //server verifies validity of the move
        //updates the game to represent the move
        //send a load_game message to all clients
        //send a notification to all other clients
        return null;
    }

    public NotificationMessage leave() {
        //update the game to remove root client
        //send a notification to all other clients
        return null;
    }

    public LoadGameMessage resign() {
        //update game as over in the database
        //send a notification to all other clients
        return null;
    }
    public NotificationMessage resignMessage() {
        return null;
    }

    //have a list of all the connections that matter
    //pair connections with usernames somehow
    //when you try to broadcast, look up with DAOs the relevant users
    //if there are connections (there should be), broadcast to each of those paired sessions
    //also use DAOs to update who's on what game
    //and game state
    //make sure game moves are legal

}
