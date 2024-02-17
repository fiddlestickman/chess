package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class RegisterService extends Service {
    public RegisterService() {}
    public String Register(String username, String password, String email) throws DataAccessException, ServiceException {
        UserDAO userdao = MemoryUserDAO.getInstance();
        UserData user;
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        user = userdao.readUserName(username);
        if (user != null)
            throw new ServiceException("Username taken");
        user = userdao.readUserEmail(email);
        if (user != null)
            throw new ServiceException("Email already connected to account");
        user = new UserData(username, password, email);
        userdao.create(user);
        auth = new AuthData(CreateAuthToken(), username);
        authDAO.create(auth);
        return auth.authToken();
    }
}
