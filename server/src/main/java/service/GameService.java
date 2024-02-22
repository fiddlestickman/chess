package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.WatchDAO;
import dataAccess.MemoryWatchDAO;
import model.AuthData;
import model.GameData;
import model.WatchData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
            WatchDAO watchDAO = MemoryWatchDAO.getInstance();
            WatchData newwatcher = new WatchData (auth.username(), game.gameID());
            watchDAO.create(newwatcher);
        }
    }
    public Collection<GameData> ListGames(String authToken) throws DataAccessException , ServiceException {
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        WatchDAO watchDAO = MemoryWatchDAO.getInstance();
        AuthData auth = Authenticate(authToken);
        Collection<GameData> games = gameDAO.readUser(auth.username());
        Collection<WatchData> watch = watchDAO.readUser(auth.username());
        ArrayList<Integer> gameIDs = new ArrayList<>();

        Iterator<WatchData> watchIter = watch.iterator();
        while (watchIter.hasNext()) {
            gameIDs.add(watchIter.next().gameID());
        }
        Iterator<Integer> gameIter = gameIDs.iterator();
        while (gameIter.hasNext()){
            games.add(gameDAO.readGameID(gameIter.next()));
        }
        return games;
    }
}