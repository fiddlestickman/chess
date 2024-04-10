package dataAccess;

import model.WatchData;

import java.util.Collection;

public class MemoryWatchDAO extends MemoryDAO<WatchData> implements WatchDAO {

    private static MemoryWatchDAO INSTANCE;
    private MemoryWatchDAO() {
        super();
    }

    public static MemoryWatchDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryWatchDAO();
        }
        return INSTANCE;
    }

    public Collection<WatchData> readGameID(int gameID) {
        WatchData user = new WatchData("", gameID);
        return super.readAll(user, "gameID");
    }


}
