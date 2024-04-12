package dataAccess;

import model.AuthData;
import model.WatchData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface WatchDAO {
    int create(WatchData w) throws DataAccessException;
    Collection<WatchData> readGameID(int gameID) throws DataAccessException;
    WatchData findWatch(String auth, int gameID) throws DataAccessException;
    void delete(WatchData w) throws DataAccessException;
    void clear() throws DataAccessException;
}
