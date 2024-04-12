package service;

import dataAccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class WSService extends Service {

    public WSService() {}

    public void delete(String auth, int gameID) throws DataAccessException {
        AuthDAO authDAO = SQLAuthDAO.getInstance();
        WatchDAO watchDAO = SQLWatchDAO.getInstance();
        AuthData authdata = authDAO.readAuth(auth);
        WatchData watch = watchDAO.findWatch(authdata.username(), gameID);
        if (watch != null) {
            watchDAO.delete(watch);
        }
    }
}
