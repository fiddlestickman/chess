package server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.AuthToken;

public class Handler {

    //this class should take calls from the server (html) and convert it to a form java understands
    //then pass it off to the service.
    //also packages up the java output to something html understands

    public Object Deserialize (String body, Class<?> classType) throws RequestException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            return gson.fromJson(body, classType);
        } catch (JsonSyntaxException e) {
            if (classType == AuthToken.class)
                throw new RequestException("unauthorized", 401);
            throw new RequestException("bad request", 400);
        }
    }

    public String Serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
    }

    String Error (Exception e, spark.Response res, int code) {
        Response response = new Response();
        res.status(code);
        //String jmessage = Serialize(e);
        //res.body(jmessage);
        response.success = false;
        response.message = "Error: " + e;
        return Serialize(response);
    }

    //step 1 - create gson object using GsonBuilder - Gson gson = builder.create();
    //step 2 - deserialize JSON to Object - Chess obj = gson.fromJson(jsonString, Chess.class);
    //step 3 - serilize object to JSON - jsonString = gson.toJson(obj);

    class Response {
        boolean success;
        String message = null;
    }

}