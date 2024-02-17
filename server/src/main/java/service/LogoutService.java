package service;

import dataAccess.AuthDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
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
