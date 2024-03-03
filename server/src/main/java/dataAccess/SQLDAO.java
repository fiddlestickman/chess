package dataAccess;
import com.google.gson.Gson;
import dataAccess.DatabaseManager;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import chess.ChessGame;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

public class SQLDAO {

    int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var cg = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String c) cg.setString(i + 1, c);
                    else if (param instanceof Integer c) cg.setInt(i + 1, c);
                    else if (param instanceof ChessGame c) cg.setString(i + 1, c.toString());
                    else if (param == null) cg.setNull(i + 1, NULL);
                }
                cg.executeUpdate();

                var rs = cg.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
