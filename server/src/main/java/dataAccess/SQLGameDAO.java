package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO extends SQLDAO implements GameDAO { //needs to convert chessgame objects to json strings
    public int create(GameData g) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUser, blackUser, gameName, chessGame) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(g.game());
        var id = executeUpdate(statement, g.whiteUsername(), g.blackUsername(), g.gameName(), json);
        return id;
    }
    public Collection<GameData> readAll() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUser, blackUser, gameName, chessGame FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }
    public GameData readGameID(int gameID) throws DataAccessException {
        return null;
    }
    public void update(GameData g) throws DataAccessException {
    }
    public void clear() throws DataAccessException {
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUser = rs.getString("whiteUser");
        var blackUser = rs.getString("blackUser");
        var gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);

        return new GameData(gameID, whiteUser, blackUser, gameName, game);
    }

}
