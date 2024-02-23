package dataAccess;

import model.WatchData;

import java.util.Collection;

public interface WatchDAO {
int create(WatchData w) throws DataAccessException;
void clear() throws DataAccessException;
}
