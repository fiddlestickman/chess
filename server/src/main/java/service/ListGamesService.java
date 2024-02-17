package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class ListGamesService extends Service {
    public ListGamesService () {}

    public Collection<GameData> ListGames(String authToken) throws DataAccessException , ServiceException {
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        AuthData auth = Authenticate(authToken);
        return gameDAO.readUser(auth.username());
    }
}
