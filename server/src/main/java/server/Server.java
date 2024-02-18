package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        //let clients access the server
        Spark.port(desiredPort);
        //do stuff I guess
        createRoutes();
        //location of web assets
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //main job: sort the requests to the right handlers
        //returns the output (if any) to the internet


        Spark.awaitInitialization();
        return Spark.port();

    }

private static void createRoutes() {
    Spark.get("/hello", (req, res) -> "Greetings and good day!");
    Spark.delete("/db", (req, res) -> AdminHandler.getInstance().HandleRequest(req, res));
    Spark.post("/user", (req, res) -> "This should register a user");
    Spark.post("/session", (req, res) -> "This should login a user");
    Spark.delete("/session", (req, res) -> "This should logout a user");
    Spark.get("/game", (req, res) -> "This should list a user's games");
    Spark.post("/game", (req, res) -> "This should create a new games");
    Spark.put("/game", (req, res) -> "This should let a user join a game");
}



public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
