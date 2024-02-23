package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        //let clients access the server
        Spark.port(desiredPort);
        //location of web assets
        Spark.staticFiles.location("web");
        //do stuff I guess
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();

    }

private static void createRoutes() {
    Spark.get("/hello", (req, res) -> "Greetings and good day!");
    Spark.delete("/db", (req, res) -> AdminHandler.getInstance().clearRequest(req, res));
    Spark.post("/user", (req, res) -> UserHandler.getInstance().registerRequest(req, res));
    Spark.post("/session", (req, res) -> UserHandler.getInstance().loginRequest(req, res));
    Spark.delete("/session", (req, res) -> UserHandler.getInstance().logoutRequest(req, res));
    Spark.get("/game", (req, res) -> GameHandler.getInstance().listGamesRequest(req, res));
    Spark.post("/game", (req, res) -> GameHandler.getInstance().createGameRequest(req, res));
    Spark.put("/game", (req, res) -> GameHandler.getInstance().joinGameRequest(req, res));
}
public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
