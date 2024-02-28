package dataAccess;

import model.GameData;

import java.util.Collection;

public class SQLGameDAO implements GameDAO { //needs to convert chessgame objects to json strings
    public int create(GameData g) throws DataAccessException {
        return 0;
    }
    public Collection<GameData> readAll() throws DataAccessException {
        return null;
    }
    public GameData readGameID(int gameID) throws DataAccessException {
        return null;
    }
    public void update(GameData g) throws DataAccessException {
    }
    public void clear() throws DataAccessException {
    }

}
