package dataAccess;

import model.UserData;

public interface UserDAO {
    public void createUser(UserData u) throws DataAccessException;
    public UserData readUserName(String username) throws DataAccessException;
    public UserData readUserEmail(String email) throws DataAccessException;
    public void updatePassword(UserData u) throws DataAccessException;
    public void delete(UserData u) throws DataAccessException;
}
