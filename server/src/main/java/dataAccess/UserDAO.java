package dataAccess;

import model.UserData;

public interface UserDAO {
    int create(UserData u) throws DataAccessException;
    UserData readUserName(String username, String password) throws DataAccessException;
    UserData readUserEmail(String email, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}
