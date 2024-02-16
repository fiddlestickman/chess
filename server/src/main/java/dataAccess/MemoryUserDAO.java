package dataAccess;

import model.UserData;

public class MemoryUserDAO extends MemoryDAO<UserData> implements UserDAO {
    private static MemoryUserDAO INSTANCE;
    private MemoryUserDAO() {
        super();
    }

    public static MemoryUserDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryUserDAO();
        }
        return INSTANCE;
    }
    public UserData readUserName(String username) {
        UserData temp = new UserData(username, "","");
        return super.read(temp, "username");
    }
    public UserData readUserEmail(String email) {
        UserData temp = new UserData("", "",email);
        return super.read(temp, "email");
    }
    public void updatePassword(UserData u) {
        super.update(u, "username");
    }
}
