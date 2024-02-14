package dataAccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO INSTANCE;
    private final ArrayList<AuthData> authDatabase;

    private MemoryAuthDAO() {
        authDatabase = new ArrayList<>();
    }

    public static MemoryAuthDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MemoryAuthDAO();
        }
        return INSTANCE;
    }

    public void createAuth(AuthData a) throws DataAccessException {
        authDatabase.add(a);
    }
    public AuthData readUser(String username) throws DataAccessException {
        Iterator<AuthData> iter =  authDatabase.iterator();
        while(iter.hasNext()) {
            AuthData next = iter.next();
            if (Objects.equals(next.username(), username)) return next;
        }
        return null;
    }
    public AuthData readAuth(String authToken) throws DataAccessException {
        Iterator<AuthData> iter =  authDatabase.iterator();
        while(iter.hasNext()) {
            AuthData next = iter.next();
            if (Objects.equals(next.authToken(), authToken)) return next;
        }
        return null;
    }
    public void delete(AuthData a) throws DataAccessException {
        authDatabase.remove(a);
    }
}
