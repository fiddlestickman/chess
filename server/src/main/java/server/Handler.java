package server;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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
            throw new RequestException("Incorrect data");
        }
    }

    public String Serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
    }

    protected spark.Response Error (Exception e, spark.Response res, int code) {
        res.status(code);
        String jmessage = Serialize(e);
        res.body(jmessage);
        return res;
    }

    //step 1 - create gson object using GsonBuilder - Gson gson = builder.create();
    //step 2 - deserialize JSON to Object - Chess obj = gson.fromJson(jsonString, Chess.class);
    //step 3 - serilize object to JSON - jsonString = gson.toJson(obj);

}