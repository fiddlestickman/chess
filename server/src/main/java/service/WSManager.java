package service;


import chess.*;
import dataAccess.*;
import model.*;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.xml.crypto.Data;
import java.util.Objects;

public class WSManager extends service.Service {


    public WSManager () {
    }

    public ServerMessage joinPlayerNotify(UserGameCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        ChessGame.TeamColor color = command.getColor();
        String auth = command.getAuthString();

        GameDAO gameDAO = SQLGameDAO.getInstance();
        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Bad authtoken");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Bad authtoken");
        }

        GameData gamedata = gameDAO.readGameID(gameID);
        if (gamedata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "No game with that ID found");
        }

        String strcolor = "";

        if (color == ChessGame.TeamColor.WHITE) {
            strcolor = "white";
            if (!Objects.equals(gamedata.whiteUsername(), authdata.username())) {
                return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Username wasn't for the right player");
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            strcolor = "black";
            if (!Objects.equals(gamedata.blackUsername(), authdata.username())) {
                return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Username wasn't for the right player");
            }
        } else {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Username did not match either player");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Player ");
        builder.append(authdata.username());
        builder.append(" joining on the ");
        builder.append(strcolor);
        builder.append("team\n");

        String join = builder.toString();

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }

    public ServerMessage joinObserverNotify(UserGameCommand command) throws DataAccessException {
        String auth = command.getAuthString();

        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Observer ");
        builder.append(authdata.username());
        builder.append(" has joined\n");

        String join = builder.toString();

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }

    public ServerMessage loadGame(UserGameCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        GameDAO gameDAO = SQLGameDAO.getInstance();
        GameData gamedata = gameDAO.readGameID(gameID);

        if (gamedata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "No game found with given gameID");
        }
        return new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gamedata.game());
    }

    public ServerMessage makeMove(UserGameCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        ChessMove move = command.getMove();
        String auth = command.getAuthString();

        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }

        GameData gamedata = gameDAO.readGameID(gameID);
        ChessGame game = gamedata.game();
        if (game.isGameOver()) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Can't make a move when the game is over");
        }

        ChessGame.TeamColor color = null;
        if (Objects.equals(gamedata.whiteUsername(), authdata.username())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(gamedata.blackUsername(), authdata.username())) {
            color = ChessGame.TeamColor.BLACK;
        }
        if (color != game.getTeamTurn()) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Can't make a move for your opponent");
        }

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Illegal move sent");
        }

        gameDAO.update(gamedata);
        return new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gamedata.game());

    }
    public ServerMessage makeMoveNotification(UserGameCommand command) throws DataAccessException {
        String auth = command.getAuthString();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        GameData gamedata = gameDAO.readGameID(command.getGameID());
        if (gamedata.game().isGameOver()) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Can't make a move when the game is over");
        }
        ChessMove move = command.getMove();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = gamedata.game().getBoard().getPiece(end);
        int j = 0;
        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }

        String strcolor;

        if (Objects.equals(gamedata.whiteUsername(), authdata.username())) {
            strcolor = "white";
        } else if (Objects.equals(gamedata.blackUsername(), authdata.username())) {
            strcolor = "black";
        }
        else {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Got a move request by a non-player");
        }

        String files = "ABCDEFGH";

        StringBuilder builder = new StringBuilder();
        builder.append(strcolor);
        builder.append(" has moved ");
        builder.append(piece.pieceToString());
        builder.append(" to ");
        builder.append(files.charAt(end.getColumn()));
        builder.append(end.getRow());

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, builder.toString());
    }


    public ServerMessage leave(UserGameCommand command) throws DataAccessException {
        String auth = command.getAuthString();
        int gameID = command.getGameID();

        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);

        if (Objects.equals(authdata.username(), game.whiteUsername())) {
            gameDAO.update(new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(authdata.username(), game.blackUsername())) {
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game()));
        } else {
            String notice = authdata.username() + "has stopped observing\n";
            return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notice);
        }

        String notice = authdata.username() + " has left the game\n";

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notice);
    }

    public ServerMessage resign(UserGameCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();
        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);
        if (game.game().isGameOver()) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Trying to resign when the game is already over");
        }


        if (Objects.equals(authdata.username(), game.whiteUsername())) {
            game.game().resign();
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(authdata.username(), game.blackUsername())) {
            game.game().resign();
            gameDAO.update(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        } else {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "A non-player tried to resign");
        }
        return new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
    }

    public ServerMessage resignMessage(UserGameCommand command) throws DataAccessException {
        int gameID = command.getGameID();
        String auth = command.getAuthString();

        GameDAO gameDAO = SQLGameDAO.getInstance();

        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "something went wrong with authentication");
        }
        if(authdata == null) {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken was wrong");
        }
        GameData game = gameDAO.readGameID(gameID);

        String strcolor;
        String enemycolor;
        if (Objects.equals(authdata.username(), game.whiteUsername())) {
            strcolor = "WHITE";
            enemycolor = "BLACK";
        } else if (Objects.equals(authdata.username(), game.blackUsername())) {
            strcolor = "BLACK";
            enemycolor = "WHITE";
        } else {
            return new ServerMessage(ServerMessage.ServerMessageType.ERROR, "no turn color, what?");
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

        return new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, join);
    }

    public void delete(String auth, int gameID) throws DataAccessException {
        AuthData authdata;
        try {
            authdata = authenticate(auth);
        } catch (Exception e){
            throw new DataAccessException("something went wrong with authentication");
        }
    }
    public String getUsername(String auth) throws DataAccessException {
        try {
            AuthData data = authenticate(auth);
            return data.username();
        } catch (Exception e) {
            return null;
        }

    }

}
