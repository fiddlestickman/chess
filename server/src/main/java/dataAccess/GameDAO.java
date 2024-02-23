package dataAccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    int create(GameData g) throws DataAccessException;
    Collection<GameData> readAll() throws DataAccessException;
    GameData readGameID(int gameID) throws DataAccessException;
    void update(GameData g) throws DataAccessException;
    void clear() throws DataAccessException;
}
