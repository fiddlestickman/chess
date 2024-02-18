package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService extends Service {

    public GameService() {
    }
    public int CreateGame(String authToken, String gameName) throws DataAccessException, ServiceException {
        Authenticate(authToken);
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        //the GameDAO handles gameID creation
        return gameDAO.create(game);
    }
    public void JoinGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws DataAccessException, ServiceException {
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        AuthData auth = Authenticate(authToken);
        GameData game = gameDAO.readGameID(gameID);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken");
            }
            GameData newgame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
        else if (teamColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken");
            }
            GameData newgame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
    }
    public Collection<GameData> ListGames(String authToken) throws DataAccessException , ServiceException {
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        AuthData auth = Authenticate(authToken);
        return gameDAO.readUser(auth.username());
    }
}