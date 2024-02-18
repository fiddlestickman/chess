package server;

import com.mysql.cj.xdevapi.JsonString;
import dataAccess.DataAccessException;
import service.AdminService;
import spark.Spark;

public class AdminHandler extends Handler {
    private static AdminHandler INSTANCE;
    private AdminHandler() {}

    public static AdminHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new AdminHandler();
        }
        return INSTANCE;
    }

    public Object HandleRequest(spark.Request req, spark.Response res) {
        AdminService admin = new AdminService();
        try {
            admin.Clear();
            res.status(200);
            return res;
        } catch (DataAccessException e) {
            res.status(500);
            String jmessage = Serialize(e);
            res.body(jmessage);
            return res;
        }
    }
}
