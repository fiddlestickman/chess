package dataAccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO extends MemoryDAO<GameData> implements GameDAO {
    private static MemoryGameDAO INSTANCE;

    private MemoryGameDAO() {
        super();
    }

    public static MemoryGameDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MemoryGameDAO();
        }
        return INSTANCE;
    }

    public int create(GameData g) {
        GameData indexed = new GameData(index, g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
        database.add(indexed);
        return index++;
    }

    public Collection<GameData> readUser(String username) {
        ArrayList<GameData> output = new ArrayList<>();
        GameData temp = new GameData(0, username, username,"", null);
        Collection<GameData> white = super.readAll(temp, "whiteUsername");

        output.addAll(super.readAll(temp, "whiteUsername"));
        output.addAll(super.readAll(temp, "blackUsername"));
        return output;
    }

    public GameData readGameID(int gameID) {
        GameData temp = new GameData(gameID, "", "","", null);
        return super.read(temp, "gameID");
    }

    public void update(GameData g) {
        super.update(g, "gameID");
    }
}