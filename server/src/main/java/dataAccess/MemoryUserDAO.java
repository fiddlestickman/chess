package dataAccess;

import model.UserData;

public class MemoryUserDAO extends MemoryDAO<UserData> implements UserDAO {
    private static MemoryUserDAO instance;
    private MemoryUserDAO() {
        super();
    }

    public static MemoryUserDAO getInstance() {
        if (instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
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
