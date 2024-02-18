package server;

import dataAccess.DataAccessException;
import model.*;
import service.GameService;
import service.ServiceException;

import java.util.ArrayList;

public class GameHandler extends Handler {
    private static GameHandler INSTANCE;
    private final GameService gameserve;
    private GameHandler() {
        gameserve = new GameService();
    }

    public static GameHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GameHandler();
        }
        return INSTANCE;
    }

    public Object ListGamesRequest(spark.Request req, spark.Response res) {
        try {
            String auth = req.headers("authorization");
            AuthToken data = (AuthToken) Deserialize(auth, AuthToken.class);
            ArrayList<GameData> games = new ArrayList<>(gameserve.ListGames(data.authToken()));
            res.body(Serialize(games));
            res.status(200);
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }

    public Object CreateGameRequest(spark.Request req, spark.Response res) {
        try {
            String auth = req.headers("authorization");
            AuthToken authToken = (AuthToken) Deserialize(auth, AuthToken.class);
            CreateGameData data = (CreateGameData) Deserialize(req.body(), CreateGameData.class);
            int gameID = gameserve.CreateGame(authToken.authToken(), data.gameName());
            GameIDData gameIDdata = new GameIDData(gameID);
            res.body(Serialize(gameIDdata));
            res.status(200);
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }
    public Object JoinGameRequest(spark.Request req, spark.Response res) {
        try {
            String auth = req.headers("authorization");
            AuthToken authToken = (AuthToken) Deserialize(auth, AuthToken.class);
            JoinGameData data = (JoinGameData) Deserialize(req.body(), JoinGameData.class);
            gameserve.JoinGame(authToken.authToken(), data.playerColor(), data.gameID());
            res.status(200);
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }
}
