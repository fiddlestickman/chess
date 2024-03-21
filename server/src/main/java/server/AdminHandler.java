package server;

import com.mysql.cj.xdevapi.JsonString;
import dataAccess.DataAccessException;
import service.AdminService;
import spark.Spark;

public class AdminHandler extends Handler {
    private static AdminHandler instance;
    private AdminHandler() {
        admin = new AdminService();
    }
    private AdminService admin;

    public static AdminHandler getInstance() {
        if(instance == null) {
            instance = new AdminHandler();
        }
        return instance;
    }

    public Object clearRequest(spark.Request req, spark.Response res) {
        Response response = new Response();
        try {
            admin.clear();
            res.status(200);
            response.success = true;
            return serialize(response);
        } catch (DataAccessException e) { return error(e, res, 500);
        }
    }

    public Object greetRequest(spark.Request req, spark.Response res) {
        Response response = new Response();
        res.status(200);
        response.success = true;
        response.message = "Greetings and good day!";
        //res.body(serialize(response));
        return serialize(response);
    }

}
