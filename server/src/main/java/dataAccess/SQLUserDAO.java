package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SQLUserDAO implements UserDAO {
    public int create(UserData u) throws DataAccessException {
        return 0;
    }
    public UserData readUserName(String username) throws DataAccessException {
        return null;
    }
    public UserData readUserEmail(String email) throws DataAccessException {
        return null;
    }
    public void clear() throws DataAccessException {

    }

    void storeUserPassword(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(clearTextPassword);

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(providedClearTextPassword, hashedPassword);
    }

}
