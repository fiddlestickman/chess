package service;

import java.security.SecureRandom;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import model.AuthData;

class Service {
    protected static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public String createAuthToken() {
        SecureRandom temp = new SecureRandom();
        StringBuilder authMake = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            char next = ALPHANUM.charAt(temp.nextInt(ALPHANUM.length()));
            authMake.append(next);
        }
        return authMake.toString();
    }

    /**
     * Authenticates an authToken, and throws an error if none match
     */
    public AuthData authenticate(String authToken) throws DataAccessException, ServiceException{
        AuthDAO authDAO = SQLAuthDAO.getInstance();
        AuthData auth;
        if (authToken.startsWith("\"")) {
            authToken = authToken.substring(1, 21);
        }
        auth = authDAO.readAuth(authToken);
        if (auth == null)
            throw new ServiceException("unauthorized", 401);
        return auth;
    }
}
