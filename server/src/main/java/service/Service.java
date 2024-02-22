package service;

import java.security.SecureRandom;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

class Service {
    protected static String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public String CreateAuthToken() {
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
    public AuthData Authenticate(String authToken) throws DataAccessException, ServiceException{
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        auth = authDAO.readAuth(authToken);
        if (auth == null)
            throw new ServiceException("unauthorized", 401);
        return auth;
    }

    //login
    //logout
    //register
    //list games
    //create game
    //join game
    //clear application


    //service classes should take strings or other java data and do stuff with them
    //DAOs interact with database (call them), handlers deal with html (return to them)
}
