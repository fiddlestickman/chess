package dataAccess;

import model.AuthData;

public class MemoryAuthDAO extends MemoryDAO<AuthData> implements AuthDAO {
    private static MemoryAuthDAO INSTANCE;

    private MemoryAuthDAO() {
        super();
    }

    public static MemoryAuthDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MemoryAuthDAO();
        }
        return INSTANCE;
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
