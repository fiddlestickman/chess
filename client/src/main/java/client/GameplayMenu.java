package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import webSocketMessages.serverMessages.*;

import javax.websocket.*;
import java.net.URI;
import java.util.Objects;
import java.util.Scanner;

public class GameplayMenu extends Endpoint {
    private static String[] gameOptions = {"Help", "Redraw Chess Board", "Leave", "Make Move", "Resign", "Highlight Legal Moves"};
    private final Session session;
    private final String auth;

    public GameplayMenu(String auth, String gameUrl) throws Exception {
        this.auth = auth;
        URI uri = new URI(gameUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                handleInput(message);
            }
        });
    }

    public String gameLoop() {
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
        //redraw the board, using the new board
        //also update the local board?
    }
    private void notify(NotificationMessage message){
        //print text to the player
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
