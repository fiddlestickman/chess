package dataAccess;

import model.GameData;

public interface GameDAO {
    public void createGame(GameData g) throws DataAccessException;
    public GameData readWhiteUser(String username) throws DataAccessException;
    public GameData readBlackUser(String username) throws DataAccessException;
    public GameData readGameID(int ID) throws DataAccessException;
    public void update(GameData g) throws DataAccessException;
    public void delete(GameData g) throws DataAccessException;
}
