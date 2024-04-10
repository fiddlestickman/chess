package service;

import dataAccess.*;

public class AdminService {

    public AdminService() {}

    public void clear() throws DataAccessException {
        AuthDAO authDAO = SQLAuthDAO.getInstance();
        GameDAO gameDAO = SQLGameDAO.getInstance();
        UserDAO userDAO = SQLUserDAO.getInstance();
        WatchDAO watchDAO = MemoryWatchDAO.getInstance();

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        watchDAO.clear();
    }
}
