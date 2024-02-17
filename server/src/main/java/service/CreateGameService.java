package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;

public class CreateGameService extends Service {

    public CreateGameService() {}

    public int CreateGame(String authToken, String gameName) throws DataAccessException, ServiceException {
        Authenticate(authToken);
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        GameData game = new GameData(0, null, null, gameName, new ChessGame());
        //the GameDAO handles gameID creation
        return gameDAO.create(game);
    }
}
