package dataAccess;

import model.UserData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private static MemoryUserDAO INSTANCE;
    private final ArrayList<UserData> userDatabase;

    private MemoryUserDAO() {
        userDatabase = new ArrayList<>();
    }

    public static MemoryUserDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryUserDAO();
        }
        return INSTANCE;
    }
    public void createUser(UserData u) throws DataAccessException {
        userDatabase.add(u);
    }
    public UserData readUserName(String username) throws DataAccessException {
        Iterator<UserData> iter =  userDatabase.iterator();
        while(iter.hasNext()) {
            UserData next = iter.next();
            if (Objects.equals(next.username(), username))
                return next;
        }
        return null;
    }
    public UserData readUserEmail(String email) throws DataAccessException {
        Iterator<UserData> iter =  userDatabase.iterator();
        while(iter.hasNext()) {
            UserData next = iter.next();
            if (Objects.equals(next.email(), email))
                return next;
        }
        return null;
    }
    public void updatePassword(UserData u) throws DataAccessException {
        Iterator<UserData> iter =  userDatabase.iterator();
        while(iter.hasNext()) {
            UserData next = iter.next();
            if (Objects.equals(next.username(), u.username())){
                userDatabase.remove(next);
                userDatabase.add(u);
            }
        }
    }
    public void delete(UserData u) throws DataAccessException {
        userDatabase.remove(u);
    }

}
