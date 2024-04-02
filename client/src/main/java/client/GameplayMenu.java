package client;

import javax.websocket.*;
import java.net.URI;

public class GameplayMenu extends Endpoint {

    private final Session session;
    private final String auth;

    public GameplayMenu(String auth, String gameUrl) throws Exception {
        this.auth = auth;
        URI uri = new URI(gameUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public String gameLoop() {
        return "";
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
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
