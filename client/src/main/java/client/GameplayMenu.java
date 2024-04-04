package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ui.ChessboardUI;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public class GameplayMenu extends Endpoint {
    private static final String[] gameOptions = {"Help", "Redraw Board", "Leave", "Make Move", "Resign", "Highlight Legal Moves"};
    private static final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private final Session session;
    private final String auth;
    private final int gameID;
    private final ChessGame.TeamColor color;
    private ServerFacade facade;
    private ChessGame game;

    public GameplayMenu(String auth, String portNum, int gameID, ChessGame.TeamColor color) throws Exception {
        this.auth = auth;
        this.gameID = gameID;
        this.color = color;
        String url = "http://localhost:" + portNum;
        facade = new ServerFacade(url, auth);
        String gameUrl = "ws://localhost:" + portNum + "/connect";
        URI uri = new URI(gameUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                handleInput(message);
            }
        });

        send(serialize(new JoinPlayerCommand(auth, gameID, color)));
    }

    public String gameLoop() throws Exception{
        //get input here
        String input = getString("[Do a thing]>>> ");
        input = input.toLowerCase();
        if (Objects.equals(input, "1") || Objects.equals(input, "help")) {
            System.out.printf("%d. %s - Explains the different options available%n", 1, gameOptions[0]);
            System.out.printf("%d. %s - Shows the current board state%n", 2, gameOptions[1]);
            System.out.printf("%d. %s - Returns to the logged in menu%n", 3, gameOptions[2]);
            System.out.printf("%d. %s - Lets you make a move%n", 4, gameOptions[3]);
            System.out.printf("%d. %s - Forfeits the game and returns to the logged in menu%n", 5, gameOptions[4]);
            System.out.printf("%d. %s - Shows the legal moves a piece can make%n%n", 6, gameOptions[5]);
            return "keep looping";
        }
        else if (Objects.equals(input, "2") || Objects.equals(input, "redraw board")) {
            if (color == null || color == ChessGame.TeamColor.WHITE) {
                ChessboardUI.PrintWhite();
            } else if (color == ChessGame.TeamColor.BLACK) {
                ChessboardUI.PrintBlack();
            }
        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "leave")) {
            LeaveCommand command = new LeaveCommand(auth, gameID);
            send(serialize(command));
            System.out.println("Leaving game (anyone can join)");
            return "stop looping";
        }
        else if (Objects.equals(input, "4") || Objects.equals(input, "make move")) {
            if (color != game.getTeamTurn()) {
                System.out.println("Can't make a move on opponent's turn");
                return "keep looping";
            }
            //actually make the move


        }
        else if (Objects.equals(input, "5") || Objects.equals(input, "resign")) {
            ResignCommand command = new ResignCommand(auth, gameID);
            send(serialize(command));
            System.out.println("Resigning...");
            return "stop looping";
        }
        else if (Objects.equals(input, "6") || Objects.equals(input, "highlight") || Objects.equals(input, "highlight legal moves")) {
            String strpos = getString("Type the piece position");
            ChessPosition pos = getPos(strpos);
            if (pos == null) {
                System.out.println("Did not understand input (format like a3, c6)");
                return "keep looping";
            }
            Collection<ChessMove> moves = game.validMoves(pos);

            //highlight legal moves
            //design chessboard thing to do that for you
        }
        return "keep looping";
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void handleInput(String json) {
        try {
            ServerMessage message = deserialize(json);
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                loadGame((LoadGameMessage) message);
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                notify((NotificationMessage) message);
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                //error handling
            }
        } catch (RequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadGame(LoadGameMessage message){
        this.game = message.getGame();
        if (color == null || color == ChessGame.TeamColor.WHITE) {
            ChessboardUI.PrintWhite();
        } else if (color == ChessGame.TeamColor.BLACK) {
            ChessboardUI.PrintBlack();
        }
    }

    private void notify(NotificationMessage message){
        System.out.println(message.getMessage());
    }


    private ServerMessage deserialize (String body) throws RequestException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        ServerMessage temp = gson.fromJson(body, ServerMessage.class);
        if (temp.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            return gson.fromJson(body, LoadGameMessage.class);
        } else if (temp.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            return gson.fromJson(body, NotificationMessage.class);
        } else if (temp.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            return gson.fromJson(body, ErrorMessage.class);
        } else {
            throw new RequestException("there's no message type, what?", 500);
        }
    }

    private String serialize (Object thing) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.toJson(thing);
    }

    private String getString(String prompt) {
        System.out.print(prompt);
        Scanner login = new Scanner(System.in);
        String line = login.nextLine();
        return line.strip();
    }

    private ChessPosition getPos(String pos) {
        int row = 0;
        int col = 0;
        for (int i = 0; i < columns.length; i++) {
            if (pos.startsWith(columns[i])) {
                col = i+1;
            }
        }
        for (int i = 0; i < rows.length; i++) {
            if (pos.endsWith(rows[i])) {
                row = i+1;
            }
        }
        if (row != 0 && col != 0) {
            return new ChessPosition(row, col);
        } else {
            return null;
        }
    }

}




/*
when you join as a player or observer, open a websocket connection
use the new connection to join
load up the UI for the joined player
wait for client response

client can send join (two types), make move, leave, and resign commands
join is only used to connect players to games
make move tells the server which move they want to make
leave tells the server they don't want to play anymore (others can join)
resign tells the server that your side loses

server can send load game, error, and notification messages
load game is used when someone makes a move
error for some kind of mistake
notification for things like players joining, making a move, being in check or checkmate, leaving or resigning

server can interrupt client actions -
things like help and show moves may be interrupted by the other client through the server

client can also do things like help, redraw board, leave, make move, resign, and highlight legal moves
not all of these require interaction with the server


 */
