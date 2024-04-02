package client;

import chess.*;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // Specify the desired endpoint
        String portNum = "8080";
        if (args.length == 1) {
            portNum = args[0];
        }

        login(portNum); //the login loop calls the pregame loop
    }
    private static void login(String portNum){
        String serverUrl = "http://localhost:" + portNum;
        LoginMenu login = new LoginMenu(serverUrl);
        boolean loop = true;
        while (loop) {
            String out = login.LoginLoop();
            if (Objects.equals(out, "keep looping")) {} //do nothing, keep looping
            else if (Objects.equals(out, "stop looping") || out == null) {
                loop = false;
            }
            else {
                pregame(out, serverUrl);
            }
        }
    }
    private static void pregame(String auth, String portNum) {
        String serverUrl = "http://localhost:" + portNum;
        PregameMenu pregame = new PregameMenu(auth, serverUrl);
        boolean loop = true;

        while (loop) {
            String out = pregame.PregameLoop();
            if (Objects.equals(out, "keep looping")) {} //do nothing, keep looping
            else if (Objects.equals(out, "stop looping")) {
                loop = false;
            }
            else {
                loop = false;
            }
        }
    }

    private static void gameplay (String auth, String portNum) {
        String gameUrl = "ws://localhost:" + portNum + "/connect";
        try {
            GameplayMenu game = new GameplayMenu(auth, gameUrl);
        boolean loop = true;

        while (loop) {
            String out = game.gameLoop();
            if (Objects.equals(out, "keep looping")) {} //do nothing, keep looping
            else if (Objects.equals(out, "stop looping")) {
                loop = false;
            }
            else {
                loop = false;
            }
        }

        } catch (Exception e) {
            //error handling
        }
    }

    class Response {
        boolean success;
        int code;
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