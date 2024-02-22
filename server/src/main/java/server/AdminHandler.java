package server;

import com.mysql.cj.xdevapi.JsonString;
import dataAccess.DataAccessException;
import service.AdminService;
import spark.Spark;

public class AdminHandler extends Handler {
    private static AdminHandler INSTANCE;
    private AdminHandler() {
        admin = new AdminService();
    }
    private AdminService admin;

    public static AdminHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AdminHandler();
        }
        return INSTANCE;
    }

    public Object ClearRequest(spark.Request req, spark.Response res) {
        Response response = new Response();
        try {
            admin.Clear();
            res.status(200);
            response.success = true;
            return Serialize(response);
        } catch (DataAccessException e) { return Error(e, res, 500);
        }
    }
}
