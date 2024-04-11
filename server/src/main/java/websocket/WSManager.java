package websocket;


import chess.*;
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


    public LoadGameMessage makeMove(MakeMoveCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        String auth = command.getAuthString();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }

        GameData gamedata = gameDAO.readGameID(gameID);
        try {
            gamedata.game().makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException("Illegal move sent");
        }

        gameDAO.update(gamedata);
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gamedata.game());

    }
    public NotificationMessage makeMoveNotification(MakeMoveCommand command) throws DataAccessException {
        String auth = command.getAuthString();
        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        GameData gamedata = gameDAO.readGameID(command.getGameID());

        ChessGame.TeamColor newcolor = gamedata.game().getTeamTurn();

        ChessMove move = command.getMove();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = gamedata.game().getBoard().getPiece(end);

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }

        String strcolor = null;
        if (newcolor == ChessGame.TeamColor.WHITE) {
            strcolor = "BLACK";
        } else if (newcolor == ChessGame.TeamColor.BLACK) {
            strcolor = "WHITE";
        }
        if (strcolor == null) {
            throw new DataAccessException("no turn color, what?");
        }

        String files = "ABCDEFGH";

        StringBuilder builder = new StringBuilder();
        builder.append(strcolor);
        builder.append(" has moved ");
        builder.append(piece.pieceToString());
        builder.append(" to ");
        builder.append(files.charAt(end.getColumn()));
        builder.append(end.getRow());

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, builder.toString());
    }


    public NotificationMessage leave(LeaveCommand command) throws DataAccessException {
        String auth = command.getAuthString();
        int gameID = command.getGameID();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();
        WatchDAO watchDAO = SQLWatchDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);

        if (Objects.equals(authdata.username(), game.whiteUsername())) {
            gameDAO.update(new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(authdata.username(), game.blackUsername())) {
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game()));
        } else {
            WatchData watch = watchDAO.findWatch(authdata.username(), gameID);
            if (watch == null) {
                throw new DataAccessException("A non-player, non-observer tried to leave the game");
            }
            watchDAO.delete(watch);
            String notice = authdata.username() + "has stopped observing\n";
            return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notice);
        }

        String notice = authdata.username() + " has left the game\n";

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notice);
    }

    public String getLeave(LeaveCommand command) throws DataAccessException {
        String auth = command.getAuthString();
        AuthDAO authDAO = SQLAuthDAO.getInstance();
        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }
        return authdata.username();
    }

    public LoadGameMessage resign(ResignCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);

        if (Objects.equals(authdata.username(), game.whiteUsername())) {
            game.game().resign();
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(authdata.username(), game.blackUsername())) {
            game.game().resign();
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        } else {
            throw new DataAccessException("A non-player tried to resign");
        }
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
    }

    public NotificationMessage resignMessage(ResignCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();

        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata = authDAO.readAuth(auth);
        if(authdata == null) {
            throw new DataAccessException("Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);

        String strcolor = null;
        String enemycolor = null;
        if (Objects.equals(game.whiteUsername(), authdata.username())) {
            strcolor = "WHITE";
            enemycolor = "BLACK";
        } else if (Objects.equals(game.blackUsername(), authdata.username())) {
            strcolor = "BLACK";
            enemycolor = "WHITE";
        }
        if (strcolor == null) {
            throw new DataAccessException("no turn color, what?");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Player ");
        builder.append(authdata.username());
        builder.append(" on the ");
        builder.append(strcolor);
        builder.append("team has resigned\n");
        builder.append(enemycolor);
        builder.append(" wins!\n");

        String join = builder.toString();

        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }
}
