package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO extends SQLDAO implements UserDAO {

    private static SQLUserDAO instance;

    private SQLUserDAO() throws DataAccessException {
        DatabaseManager data = new DatabaseManager();
        data.configureDatabase();
    }

    public static SQLUserDAO getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new SQLUserDAO();
        }
        return instance;
    }
    public int create(UserData u) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        var password = hashPass(u.password());
        var id = executeUpdate(statement, u.username(), password, u.email());
        return id;
    }
    public UserData readUserName(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (verifyUser(username, password)) {
                            return readUser(rs, password);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
    private String readPassword(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public UserData readUserEmail(String email, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE email=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs, password);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
    public void clear() throws DataAccessException {

        var statement = "DELETE FROM user";
        executeUpdate(statement);
    }

    private String hashPass(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        try {
            var hashedPassword = readPassword(username);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(providedClearTextPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    private UserData readUser(ResultSet rs, String password) throws SQLException {
        var username = rs.getString("username");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

}
