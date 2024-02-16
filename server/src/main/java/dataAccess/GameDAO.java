package dataAccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    public void create(GameData g) throws DataAccessException;
    public Collection<GameData> readUser(String username) throws DataAccessException;
    public GameData readGameID(int ID) throws DataAccessException;
    public void update(GameData g) throws DataAccessException;
    public void delete(GameData g) throws DataAccessException;
    public void clear() throws DataAccessException;
}
