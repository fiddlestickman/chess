package websocket;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

@WebSocket
public class WSServer {
    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/connect", WSServer.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        //do something after creating a new websocket connection
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) throws Exception {
        //close the connection after doing things
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getRemote().sendString("WebSocket response: " + message);
        //put the things that handle incoming strings here - it's a really good
        //connection point for taking client commands, maybe deserialize and go from there
        //you can use session.getRemote().sendString(String str) to send a serialized response
    }
}