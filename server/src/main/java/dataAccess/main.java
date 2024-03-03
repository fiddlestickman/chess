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

        } catch (DataAccessException e) {
            int i = 0;
        }

        j = 1;
    }
}
