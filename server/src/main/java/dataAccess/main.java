package dataAccess;

import model.AuthData;
import model.UserData;

public class main {
    public static void main(String[] args) {
        try {
            DatabaseManager data = new DatabaseManager();
            data.configureDatabase();

        } catch (DataAccessException e) {
            int i = 0;

        }
        int j = 0;
        try {
            AuthDAO auth = SQLAuthDAO.getInstance();
            GameDAO game = SQLGameDAO.getInstance();
            UserDAO user = SQLUserDAO.getInstance();

            UserData tempiary = new UserData("user", "pass", "email");
            user.create(tempiary);

            AuthData temp = new AuthData("token", "user");
            AuthData temp2 = new AuthData("token2", "user");
            AuthData temp3 = new AuthData("token3", "user");

            auth.create(temp);
            AuthData username = auth.readAuth("token");
            auth.delete(temp);
            auth.create(temp2);
            auth.create(temp3);
            auth.clear();


        } catch (DataAccessException e) {
            int i = 0;
        }

        j = 1;
    }
}
