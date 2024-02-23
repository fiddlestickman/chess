package dataAccess;

import model.UserData;

public interface UserDAO {
    int create(UserData u) throws DataAccessException;
    UserData readUserName(String username) throws DataAccessException;
    UserData readUserEmail(String email) throws DataAccessException;
    void delete(UserData u) throws DataAccessException;
    void clear() throws DataAccessException;
}
