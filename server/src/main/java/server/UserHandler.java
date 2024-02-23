package server;

import dataAccess.DataAccessException;
import model.*;
import service.ServiceException;
import service.UserService;

public class UserHandler extends Handler {
    private static UserHandler instance;
    private final UserService userserve;
    private UserHandler() {
        userserve = new UserService();
    }

    public static UserHandler getInstance() {
        if(instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }

    public Object loginRequest(spark.Request req, spark.Response res) {
        LoginResponse response = new LoginResponse();
        try {
            String json = req.body();
            LoginData data = (LoginData) deserialize(json, LoginData.class);
            String authToken = userserve.login(data.username(), data.password());
            res.status(200);
            res.body(serialize(authToken));
            response.success=true;
            response.authToken=authToken;
            response.username=data.username();
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }

    public Object logoutRequest(spark.Request req, spark.Response res) {
        Response response = new Response();
        try {
            String auth = (String) deserialize(req.headers("authorization"), String.class);
            userserve.logout(auth);
            res.status(200);
            response.success = true;
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }

    public Object registerRequest(spark.Request req, spark.Response res) {
        LoginResponse response = new LoginResponse();
        try {
            String body = req.body();
            UserData data = (UserData) deserialize(body, UserData.class);
            String auth = userserve.register(data.username(), data.password(), data.email());
            String authToken = serialize(auth);
            res.body(authToken);
            res.status(200);
            response.success=true;
            response.authToken=authToken;
            response.username=data.username();
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        } catch (ServiceException e) { return error(e, res, e.getCode());
        } catch (RequestException e) { return error(e, res, e.getCode());
        }
    }

    class LoginResponse extends Response {
        String authToken;
        String username;
    }
}
