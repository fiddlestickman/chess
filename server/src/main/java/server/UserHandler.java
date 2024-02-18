package server;

import dataAccess.DataAccessException;
import model.*;
import service.ServiceException;
import service.UserService;

public class UserHandler extends Handler {
    private static UserHandler INSTANCE;
    private UserService userserve;
    private UserHandler() {
        userserve = new UserService();
    }

    public static UserHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserHandler();
        }
        return INSTANCE;
    }

    public Object LoginRequest(spark.Request req, spark.Response res) {
        try {
            String json = req.body();
            LoginData data = (LoginData) Deserialize(json, LoginData.class);
            String authToken = userserve.Login(data.username(), data.password());
            res.status(200);
            AuthData authData = new AuthData(authToken, ((LoginData) data).username());
            res.body(Serialize(authData));
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }


    public Object LogoutRequest(spark.Request req, spark.Response res) {
        try {
            String auth = req.headers("authorization");
            AuthToken data = (AuthToken) Deserialize(auth, AuthToken.class);
            userserve.Logout(data.authToken());
            res.status(200);
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }

    public Object RegisterRequest(spark.Request req, spark.Response res) {
        try {
            String body = req.body();
            UserData data = (UserData) Deserialize(body, UserData.class);
            String auth = userserve.Register(data.username(), data.password(), data.email());
            String authToken = Serialize(new AuthToken(auth));
            res.body(authToken);
            res.status(200);
            return res;
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, 401);
        } catch (RequestException e) { return Error(e, res, 400);
        }
    }
}
