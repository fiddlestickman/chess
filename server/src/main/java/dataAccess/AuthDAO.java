package dataAccess;

import model.AuthData;

public interface AuthDAO {
    int create(AuthData a) throws DataAccessException;
    AuthData readUser(String username) throws DataAccessException;
    AuthData readAuth(String authToken) throws DataAccessException;
    void delete(AuthData a) throws DataAccessException;
    void clear() throws DataAccessException;
}
