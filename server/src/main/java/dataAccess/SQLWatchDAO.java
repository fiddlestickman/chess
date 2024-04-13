package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.WatchData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLWatchDAO extends SQLDAO implements WatchDAO {

    private static SQLWatchDAO instance;

    private SQLWatchDAO() throws DataAccessException {
        DatabaseManager data = new DatabaseManager();
        data.configureDatabase();
    }

    public static SQLWatchDAO getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new SQLWatchDAO();
        }
        return instance;
    }


    public int create(WatchData w) throws DataAccessException {
        var statement = "INSERT INTO watch (gameID, username) VALUES (?, ?)";
        var id = executeUpdate(statement, w.gameID(), w.username());
        return id;
    }

    public Collection<WatchData> readGameID(int gameID) throws DataAccessException {
        var result = new ArrayList<WatchData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT watchID, username, gameID FROM watch WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.add(readWatch(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }


    public WatchData findWatch(String username, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT watchID, gameID, username FROM watch WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if(rs.getInt("gameID") == gameID) {
                            return readWatch(rs);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void delete(WatchData w) throws DataAccessException {
        var statement = "DELETE FROM watch WHERE username=?";
        executeUpdate(statement, w.username());
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE watch";
        executeUpdate(statement);
    }

    private WatchData readWatch(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var gameID = rs.getInt("gameID");

        return new WatchData(username, gameID);
    }
}

