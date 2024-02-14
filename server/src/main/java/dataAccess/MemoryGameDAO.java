package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private static MemoryGameDAO INSTANCE;
    private final ArrayList<GameData> gameDatabase;

    private MemoryGameDAO() {
        gameDatabase = new ArrayList<>();
    }

    public static MemoryGameDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MemoryGameDAO();
        }
        return INSTANCE;
    }

    public void createGame(GameData g) throws DataAccessException {
        gameDatabase.add(g);
    }
    public Collection<GameData> readUser(String username) throws DataAccessException {
        ArrayList<GameData> output = new ArrayList<>();
        Iterator<GameData> iter =  gameDatabase.iterator();
        while(iter.hasNext()) {
            GameData next = iter.next();
            if (Objects.equals(next.whiteUsername(), username))
                output.add(next);
        }
        return output;
    }
    public GameData readGameID(int ID) throws DataAccessException {
        Iterator<GameData> iter =  gameDatabase.iterator();
        while(iter.hasNext()) {
            GameData next = iter.next();
            if (Objects.equals(next.gameID(), ID))
                return next;
        }
        return null;
    }
    public void update(GameData g) throws DataAccessException {
        Iterator<GameData> iter =  gameDatabase.iterator();
        while(iter.hasNext()) {
            GameData next = iter.next();
            if (Objects.equals(next.gameID(), g.gameID())){
                gameDatabase.remove(next);
                gameDatabase.add(g);
            }
        }
    }
    public void delete(GameData g) throws DataAccessException {
        gameDatabase.remove(g);
    }
}