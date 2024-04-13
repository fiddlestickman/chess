package service;

import chess.ChessGame;
import dataAccess.*;
import model.*;
import java.util.Collection;

public class GameService extends Service {

    public GameService() {
    }
    public int createGame(String authToken, String gameName) throws DataAccessException, ServiceException {
        authenticate(authToken);
        if (gameName == null)
            throw new ServiceException("bad request", 400);
        GameDAO gameDAO = SQLGameDAO.getInstance();
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        //the GameDAO handles gameID creation
        return gameDAO.create(game);
    }
    public void joinGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws DataAccessException, ServiceException {
        GameDAO gameDAO = SQLGameDAO.getInstance();
        AuthData auth = authenticate(authToken);
        if (gameDAO.readGameID(gameID) == null)
            throw new ServiceException("bad request", 400);
        GameData game = gameDAO.readGameID(gameID);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken", 403);
            }
            GameData newgame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
        else if (teamColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken", 403);
            }
            GameData newgame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
        else {
            WatchData newwatch = new WatchData(auth.username(), game.gameID());
            WatchDAO watchDAO = SQLWatchDAO.getInstance();
            watchDAO.create(newwatch);
        }
    }
    public Collection<GameData> listGames(String authToken) throws DataAccessException , ServiceException {
        GameDAO gameDAO = SQLGameDAO.getInstance();
        AuthData auth = authenticate(authToken);

        return gameDAO.readAll();
    }

    public GameData getGame(String authToken, int gameID) throws DataAccessException , ServiceException {
        GameDAO gameDAO = SQLGameDAO.getInstance();
        AuthData auth = authenticate(authToken);
        return gameDAO.readGameID(gameID);
    }
}