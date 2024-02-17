package service;

import dataAccess.*;
import model.AuthData;

public class LogoutService extends Service {
    public LogoutService() {}
    public void Logout(String authToken) throws DataAccessException, ServiceException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        auth = Authenticate(authToken);
        authDAO.delete(auth);
    }
}
