import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.*;
import java.util.Map;

public class HTTPHandler {
    String url;
    public HTTPHandler(String url) {
        this.url = url;
    }

    public Object Request(String method, Object obj, Class<?> expected) throws Exception {

        String body = serialize(obj);
        HttpURLConnection http = sendRequest(url, method, body);
        Object map = receiveResponse(http);
        return deserialize((String) map, expected);
    }

    private static HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        return http;
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static Object receiveResponse(HttpURLConnection http) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        Object responseBody = readResponseBody(http);
        System.out.printf("= Response =========\n[%d] %s\n\n", statusCode, statusMessage);
        return responseBody;
    }

    private static Object readResponseBody(HttpURLConnection http) throws IOException {
        Object responseBody = "";
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, Map.class);
        }
        return responseBody;
    }

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
}
