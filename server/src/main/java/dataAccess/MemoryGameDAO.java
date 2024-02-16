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

    public Collection<GameData> readUser(String username) {
        ArrayList<GameData> output = new ArrayList<>();
        GameData temp = new GameData(0, username, username,"", null);
        output.addAll(super.readAll(temp, "whiteUsername"));
        output.addAll(super.readAll(temp, "blackUsername"));
        return output;
    }

    public GameData readGameID(int ID) {
        GameData temp = new GameData(ID, "", "","", null);
        return super.read(temp, "gameID");
    }

    public void update(GameData g) {
        super.update(g, "gameID");
    }
}