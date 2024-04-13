package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.ChessboardUI;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.net.URI;
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
    private ChessGame game;

    public GameplayMenu(String auth, String portNum, int gameID, ChessGame.TeamColor color) throws Exception {
        this.auth = auth;
        this.gameID = gameID;
        this.color = color;
        String gameUrl = "ws://localhost:" + portNum + "/connect";
        URI uri = new URI(gameUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                handleInput(message);
            }
        });

        if (color != null) {
            send(serialize(new UserGameCommand(UserGameCommand.CommandType.JOIN_PLAYER, auth, gameID, color)));
        } else {
            send(serialize(new UserGameCommand(UserGameCommand.CommandType.JOIN_OBSERVER, auth, gameID)));
        }
    }

    public String gameLoop() throws Exception{
        if (color == null) {
            return ObserveLoop();
        }
        //get input here
        String input = getString("[Do a thing]>>> \n");
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
            if (color == ChessGame.TeamColor.WHITE) {
                ChessboardUI.PrintWhite(game.getBoard());
            } else if (color == ChessGame.TeamColor.BLACK) {
                ChessboardUI.PrintBlack(game.getBoard());
            }
            return "keep looping";
        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "leave")) {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            send(serialize(command));
            System.out.println("Leaving game (anyone can join)");
            return "stop looping";
        }
        else if (Objects.equals(input, "4") || Objects.equals(input, "make move")) {
            if (color != game.getTeamTurn()) {
                System.out.println("Can't make a move on opponent's turn");
                return "keep looping";
            }
            ChessMove move = getMove();
            Collection<ChessMove> legalmoves = game.validMoves(move.getStartPosition());

            if (legalmoves.contains(move)) {
                UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameID, move);
                send(serialize(command));
            } else {
                System.out.println("That move is not legal (try highlighting legal moves)");
            }
        }
        else if (Objects.equals(input, "5") || Objects.equals(input, "resign")) {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
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
            } if(game.getBoard().getPiece(pos) == null) {
                System.out.print("No piece at that position.");
                return "keep looping";

            }
            Collection<ChessMove> moves = game.validMoves(pos);
            if (color == ChessGame.TeamColor.WHITE) {
                ChessboardUI.PrintWhiteHighlight(game.getBoard(), moves);
            } else if (color == ChessGame.TeamColor.BLACK) {
                ChessboardUI.PrintBlackHighlight(game.getBoard(), moves);
            }
            return "keep looping";
        }
        return "keep looping";
    }

    public String ObserveLoop() throws Exception {
        String input = getString("[Observing]>>> ");
        input = input.toLowerCase();
        if (input.equals("quit")) {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            send(serialize(command));
            System.out.println("Leaving game...");
            return "stop looping";
        } else if (input.equals("redraw board")) {
            String strcolor = getString("Which perspective? (b/w))");
            strcolor = strcolor.toLowerCase();
            if (strcolor.equals("w") || strcolor.equals("white")) {
                ChessboardUI.PrintWhite(game.getBoard());
            } else if (strcolor.equals("b") || strcolor.equals("black")) {
                ChessboardUI.PrintBlack(game.getBoard());
            }
            return "keep looping";
        } else {
            System.out.println("Did not understand input (type 'quit' or 'redraw board')");
            return "keep looping";
        }
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
                loadGame(message);
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                notify(message);
            } else if (message.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                //error handling
            }
        } catch (RequestException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadGame(ServerMessage message){
        this.game = message.getGame();
        if (color == null || color == ChessGame.TeamColor.WHITE) {
            ChessboardUI.PrintWhite(game.getBoard());
        } else if (color == ChessGame.TeamColor.BLACK) {
            ChessboardUI.PrintBlack(game.getBoard());
        }
        if (color == null)
            System.out.print("[Observing]>>> ");
        else
            System.out.print("[Do a thing]>>> ");
    }

    private void notify(ServerMessage message){
        System.out.println(message.getMessage());
    }


    private ServerMessage deserialize (String body) throws RequestException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.fromJson(body, ServerMessage.class);
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

    private ChessMove getMove() {
        String strmove = getString("Type the piece move: ");
        String[] temp;
        if (strmove.contains(" ")) {
            temp = strmove.split(" ");
        } else {
            System.out.println("Input not understood (please type like so: a3 a4)");
            return null;
        }
        ChessPosition start = getPos(temp[0]);
        ChessPosition end = getPos(temp[1]);
        if (start == null || end == null) {
            System.out.println("Input not understood (please type like so: a3 a4)");
            return null;
        } else if (game.getBoard().getPiece(start) == null || game.getBoard().getPiece(start).getTeamColor() != color) {
            System.out.println("Please choose one of your pieces on the board");
            return null;
        }
        ChessPiece.PieceType promo = null;

        if (game.getBoard().getPiece(start).getPieceType() == ChessPiece.PieceType.PAWN) {
            if (end.getRow() == 1 || end.getRow() == 8) {
                while (promo == null) {
                    String getpromo = getString("What promotion?");
                    getpromo = getpromo.toLowerCase();
                    if (getpromo.equals("q") || getpromo.equals("queen")) {
                        promo = ChessPiece.PieceType.QUEEN;
                    } else if (getpromo.equals("r") || getpromo.equals("rook")) {
                        promo = ChessPiece.PieceType.ROOK;
                    } else if (getpromo.equals("k") || getpromo.equals("knight")) {
                        promo = ChessPiece.PieceType.KNIGHT;
                    } else if (getpromo.equals("b") || getpromo.equals("bishop")) {
                        promo = ChessPiece.PieceType.BISHOP;
                    } else {
                        System.out.println("Input not understood (please type q/r/k/b)");
                    }
                }
            }
        }
        return new ChessMove(start, end, promo);
    }
}
