package dataAccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    public int create(AuthData a) throws DataAccessException {
        return 0;
    }
    public AuthData readAuth(String authToken) throws DataAccessException {
        return null;
    }
    public void delete(AuthData a) throws DataAccessException {

    }
    public void clear() throws DataAccessException {

    }
}
