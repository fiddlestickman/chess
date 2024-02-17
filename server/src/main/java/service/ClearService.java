package service;

import dataAccess.*;

public class ClearService {

    public ClearService() {}

    public void Clear() throws DataAccessException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        UserDAO userDAO = MemoryUserDAO.getInstance();

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
