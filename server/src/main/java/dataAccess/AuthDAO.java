package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData a) throws DataAccessException;
    public AuthData readUser(String username) throws DataAccessException;
    public AuthData readAuth(String authToken) throws DataAccessException;
    public void delete(AuthData a) throws DataAccessException;
}
