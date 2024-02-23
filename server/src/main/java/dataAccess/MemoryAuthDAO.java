package dataAccess;

import model.AuthData;

public class MemoryAuthDAO extends MemoryDAO<AuthData> implements AuthDAO {
    private static MemoryAuthDAO instance;

    private MemoryAuthDAO() {
        super();
    }

    public static MemoryAuthDAO getInstance() {
        if(instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }

    public AuthData readUser(String username) {
        AuthData temp = new AuthData("", username);
        return super.read(temp, "username");
    }
    public AuthData readAuth(String authToken) throws DataAccessException {
        AuthData temp = new AuthData(authToken, "");
        return super.read(temp, "authToken");
    }
}
