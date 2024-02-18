package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService extends Service {
    public UserService() {};
    public String Login(String username, String password) throws DataAccessException, ServiceException {
        UserDAO userdao = MemoryUserDAO.getInstance();
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        UserData user;
        AuthData auth;

        user = userdao.readUserName(username);
        if (user == null)
            throw new ServiceException("Unauthorized");
        if (!Objects.equals(user.password(), password)) {
            throw new ServiceException("Unauthorized");
        }
        auth = new AuthData(CreateAuthToken(), username);
        authDAO.create(auth);
        return auth.authToken();
    }

    public void Logout(String authToken) throws DataAccessException, ServiceException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        auth = Authenticate(authToken);
        authDAO.delete(auth);
    }

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