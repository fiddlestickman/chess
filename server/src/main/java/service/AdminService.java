package service;

import dataAccess.*;

public class AdminService {

    public AdminService() {}

    public void clear() throws DataAccessException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        UserDAO userDAO = MemoryUserDAO.getInstance();

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
