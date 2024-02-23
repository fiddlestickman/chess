package dataAccess;

import model.WatchData;

import java.util.Collection;

public class MemoryWatchDAO extends MemoryDAO<WatchData> implements WatchDAO {

    private static MemoryWatchDAO instance;
    private MemoryWatchDAO() {
        super();
    }

    public static MemoryWatchDAO getInstance() {
        if (instance == null) {
            instance = new MemoryWatchDAO();
        }
        return instance;
    }

    public Collection<WatchData> readUser(String username) {
        WatchData user = new WatchData(username, 0);
        return super.readAll(user, "username");
    }
}
