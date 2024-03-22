package server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class Handler {

    public Object deserialize (String body, Class<?> classType) throws RequestException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            return gson.fromJson(body, classType);
        } catch (JsonSyntaxException e) {
            if (classType == String.class)
                throw new RequestException("unauthorized", 401);
            throw new RequestException("bad request", 400);
        }
    }

    public String serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
    }

    String error (Exception e, spark.Response res, int code) {
        Response response = new Response();
        res.status(code);
        response.success = false;
        response.code = code;
        response.message = "Error: " + e;
        return serialize(response);
    }
    class Response {
        boolean success;
        int code;
        String message = null;
    }

}