package service;

import dataAccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class WSService {

    public WSService() {}

    public ArrayList<String> getUsers(int gameID) throws DataAccessException {
        ArrayList<String> users = new ArrayList<>();

        GameDAO gameDAO = SQLGameDAO.getInstance();
        WatchDAO watchDAO = SQLWatchDAO.getInstance();

        GameData game = gameDAO.readGameID(gameID);
        Collection<WatchData> watchers = watchDAO.readGameID(gameID);

        if (game.whiteUsername() != null) {
            users.add(game.whiteUsername());
        } if (game.blackUsername() != null) {
            users.add(game.blackUsername());
        }

        Iterator<WatchData> iter = watchers.iterator();

        while (iter.hasNext()){
            WatchData next = iter.next();
            users.add(next.username());
        }
        return users;
    }
}
