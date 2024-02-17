package service;

import model.*;
import dataAccess.*;
import java.util.Objects;

public class LoginService extends Service {
    public LoginService() {};

    public String Login(String username, String password) throws DataAccessException, ServiceException {
        UserDAO userdao = MemoryUserDAO.getInstance();
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        UserData user;
        AuthData auth;

        user = userdao.readUserName(username);
        if (user == null)
            throw new ServiceException("User not found");
        if (!Objects.equals(user.password(), password)) {
            throw new ServiceException("Wrong password");
        }
        auth = new AuthData(CreateAuthToken(), username);
        authDAO.create(auth);
        return auth.authToken();
    }
}
