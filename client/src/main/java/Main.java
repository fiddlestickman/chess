import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.GameData;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // Specify the desired endpoint
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        URI uri = new URI(serverUrl);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");

        // Make the request
        http.connect();
        /*
        // Output the response body
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            System.out.println(new Gson().fromJson(inputStreamReader, Map.class));
        }
        */
        HTTPHandler handler = new HTTPHandler(serverUrl + "/hello");
        Object temp;
        try {
            temp = handler.Request("GET", null, Response.class);
        } catch (Exception e) {
            temp = "wait what";
        }

        LoginMenu login = new LoginMenu(serverUrl);
        String auth = null;
        boolean loop = true;
        while (loop) {
            String out = login.LoginLoop();
            if (out == "keep looping") {} //do nothing, keep looping
            else if (out == "stop looping") {
                loop = false;
            }
            else {
                loop = false;
                auth = out;
            }
        }

        loop = true;

        while (loop) {

        }

        //connect to the server
        //look for the options (help quit login register)
        //if help, give options
        //if quit, end program
        //if register, make an account then do login
        //if login, login and give new options

        //when logged in, say so
        //look for the options (help logout create game list games join game join observer
        //if help, give the options
        //if logout, do logout and give options
        //if create game, create the game (don't join)
        //if list games, show all the games on the server
        //if join game, update the server game
        //if join observer, start watching the game

        //when join game or join observer is chosen, print the game output

    }



    public void printLines(String[] args){
        for (var i = 0; i < args.length; i++) {
            System.out.printf("%d. %s%n", i+1, args[i]);
        }
    }

    class Response {
        boolean success;
        String message = null;
    }

    class LoginResponse extends Response {
        String authToken;
        String username;
    }

    class ListResponse extends Response {
        ArrayList<GameData> games;
    }

    class CreateResponse extends Response {
        int gameID;
    }

    class JoinResponse extends Response {
        String playerColor;
        int gameID;
    }

}