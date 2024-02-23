package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService extends Service {
    public UserService() {};
    public String login(String username, String password) throws DataAccessException, ServiceException {
        UserDAO userdao = MemoryUserDAO.getInstance();
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        UserData user;
        AuthData auth;

        user = userdao.readUserName(username);
        if (user == null)
            throw new ServiceException("unauthorized", 401);
        if (!Objects.equals(user.password(), password)) {
            throw new ServiceException("unauthorized", 401);
        }
        auth = new AuthData(createAuthToken(), username);
        authDAO.create(auth);
        return auth.authToken();
    }

    public void logout(String authToken) throws DataAccessException, ServiceException {
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        auth = authenticate(authToken);
        authDAO.delete(auth);
    }

    public String register(String username, String password, String email) throws DataAccessException, ServiceException {
        UserDAO userdao = MemoryUserDAO.getInstance();
        UserData user;
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        user = userdao.readUserName(username);
        if (user != null)
            throw new ServiceException("already taken", 403);
        user = userdao.readUserEmail(email);
        if (user != null)
            throw new ServiceException("already taken", 403);
        if (username == null)
            throw new ServiceException("bad request", 400);
        if (password == null)
            throw new ServiceException("bad request", 400);
        if (email == null)
            throw new ServiceException("bad request", 400);

        user = new UserData(username, password, email);
        userdao.create(user);
        auth = new AuthData(createAuthToken(), username);
        authDAO.create(auth);
        return auth.authToken();
    }
}