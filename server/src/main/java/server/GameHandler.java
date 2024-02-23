package server;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.*;
import service.GameService;
import service.ServiceException;

import java.util.ArrayList;

public class GameHandler extends Handler {
    private static GameHandler instance;
    private final GameService gameserve;

    private GameHandler() {
        gameserve = new GameService();
    }

    public static GameHandler getInstance() {
        if(instance == null) {
            instance = new GameHandler();
        }
        return instance;
    }

    public Object listGamesRequest(spark.Request req, spark.Response res) {
        ListResponse response = new ListResponse();
        try {
            String auth = (String) deserialize(req.headers("authorization"), String.class);
            ArrayList<GameData> games = new ArrayList<>(gameserve.listGames(auth));
            res.body(serialize(games));
            res.status(200);
            response.success=true;
            response.games = games;
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }

    public Object createGameRequest(spark.Request req, spark.Response res) {
        CreateResponse response = new CreateResponse();
        try {
            String auth = (String) deserialize(req.headers("authorization"), String.class);
            CreateGameData data = (CreateGameData) deserialize(req.body(), CreateGameData.class);
            int gameID = gameserve.createGame(auth, data.gameName());
            GameIDData gameIDdata = new GameIDData(gameID);
            res.body(serialize(gameIDdata));
            res.status(200);
            response.success = true;
            response.gameID = gameID;
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }
    public Object joinGameRequest(spark.Request req, spark.Response res) {
        JoinResponse response = new JoinResponse();
        try {
            String auth = (String) deserialize(req.headers("authorization"), String.class);
            JoinGameData data = (JoinGameData) deserialize(req.body(), JoinGameData.class);
            gameserve.joinGame(auth, data.playerColor(), data.gameID());
            res.status(200);
            response.success = true;
            response.gameID = data.gameID();
            if (data.playerColor() == ChessGame.TeamColor.WHITE) {
                response.playerColor = "WHITE";
            } else if (data.playerColor() == ChessGame.TeamColor.BLACK) {
                response.playerColor = "BLACK";
            }
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }

    class ListResponse extends Response {
        ArrayList<GameData> games;
    }

    class CreateResponse extends Response {
        int gameID;
    }

    class JoinResponse extends Response {
        String playerColor;
        int gameID;
    }
}
