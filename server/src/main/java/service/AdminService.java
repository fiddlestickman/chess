package service;

import dataAccess.*;

public class AdminService {

    public AdminService() {}

    public void Clear() throws DataAccessException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        UserDAO userDAO = MemoryUserDAO.getInstance();
        WatchDAO watchDAO = MemoryWatchDAO.getInstance();

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        watchDAO.clear();
    }
}
