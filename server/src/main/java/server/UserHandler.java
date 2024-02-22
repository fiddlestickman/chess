package server;

import dataAccess.DataAccessException;
import model.*;
import service.ServiceException;
import service.UserService;

public class UserHandler extends Handler {
    private static UserHandler INSTANCE;
    private final UserService userserve;
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
        LoginResponse response = new LoginResponse();
        try {
            String json = req.body();
            LoginData data = (LoginData) Deserialize(json, LoginData.class);
            String authToken = userserve.Login(data.username(), data.password());
            res.status(200);
            AuthData authData = new AuthData(authToken, data.username());
            res.body(Serialize(authData));
            response.success=true;
            response.authToken=authToken;
            response.username=data.username();
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
        }
    }

    public Object LogoutRequest(spark.Request req, spark.Response res) {
        Response response = new Response();
        try {
            String auth = req.headers("authorization");
            AuthToken data = (AuthToken) Deserialize(auth, AuthToken.class);
            userserve.Logout(data.authToken());
            res.status(200);
            response.success = true;
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
        }
    }

    public Object RegisterRequest(spark.Request req, spark.Response res) {
        LoginResponse response = new LoginResponse();
        try {
            String body = req.body();
            UserData data = (UserData) Deserialize(body, UserData.class);
            String auth = userserve.Register(data.username(), data.password(), data.email());
            String authToken = Serialize(new AuthToken(auth));
            res.body(authToken);
            res.status(200);
            response.success=true;
            response.authToken=authToken;
            response.username=data.username();
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        } catch (ServiceException e) { return Error(e, res, e.getCode());
        } catch (RequestException e) { return Error(e, res, e.getCode());
        }
    }

    class LoginResponse extends Response {
        String authToken;
        String username;
    }
}
