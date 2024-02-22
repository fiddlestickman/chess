package server;

import chess.ChessGame;
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
        ListResponse response = new ListResponse();
        try {
            String auth = req.headers("authorization");
            AuthToken data = (AuthToken) Deserialize(auth, AuthToken.class);
            ArrayList<GameData> games = new ArrayList<>(gameserve.ListGames(data.authToken()));
            res.body(Serialize(games));
            res.status(200);
            response.success=true;
            response.games = games;
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
        }
    }

    public Object CreateGameRequest(spark.Request req, spark.Response res) {
        CreateResponse response = new CreateResponse();
        try {
            String auth = req.headers("authorization");
            AuthToken authToken = (AuthToken) Deserialize(auth, AuthToken.class);
            CreateGameData data = (CreateGameData) Deserialize(req.body(), CreateGameData.class);
            int gameID = gameserve.CreateGame(authToken.authToken(), data.gameName());
            GameIDData gameIDdata = new GameIDData(gameID);
            res.body(Serialize(gameIDdata));
            res.status(200);
            response.success = true;
            response.gameID = gameID;
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
        }
    }
    public Object JoinGameRequest(spark.Request req, spark.Response res) {
        JoinResponse response = new JoinResponse();
        try {
            String auth = req.headers("authorization");
            AuthToken authToken = (AuthToken) Deserialize(auth, AuthToken.class);
            JoinGameData data = (JoinGameData) Deserialize(req.body(), JoinGameData.class);
            gameserve.JoinGame(authToken.authToken(), data.playerColor(), data.gameID());
            res.status(200);
            response.success = true;
            response.GameID = data.gameID();
            if (data.playerColor() == ChessGame.TeamColor.WHITE) {
                response.playerColor = "WHITE";
            } else if (data.playerColor() == ChessGame.TeamColor.BLACK) {
                response.playerColor = "BLACK";
            }
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
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
        int GameID;
    }
}
