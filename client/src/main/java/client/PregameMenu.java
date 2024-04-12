package client;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import static ui.ChessboardUI.PrintWhite;
import static ui.ChessboardUI.PrintBlack;

public class PregameMenu {
    private static final String[] pregameOptions = {"Help", "Create Game", "List Games", "Join Game", "Join Observer", "Logout"};
    private ServerFacade facade;

    public PregameMenu(String auth, String url) {
        facade = new ServerFacade(url, auth);
    }

    public String PregameLoop() {
        String input = getString("[Logged in]>>> ");
        input = input.toLowerCase();
        if (Objects.equals(input, "1") || Objects.equals(input, "help")) {
            System.out.printf("%d. %s - Explains the different options available%n", 1, pregameOptions[0]);
            System.out.printf("%d. %s - Creates a new chess game, without players selected%n", 2, pregameOptions[1]);
            System.out.printf("%d. %s - Lists all the games created%n", 3, pregameOptions[2]);
            System.out.printf("%d. %s - Joins an existing chess game as one of the players%n", 4, pregameOptions[3]);
            System.out.printf("%d. %s - Joins an existing chess game as a spectator%n", 5, pregameOptions[4]);
            System.out.printf("%d. %s - Logs out of your account, returning to the start menu%n%n", 6, pregameOptions[5]);
            return "keep looping";
        }
        else if (Objects.equals(input, "2") || Objects.equals(input, "create game")) {
            String name = getString("Please enter a game name: ");
            int gameID = facade.createGame(name);
            System.out.printf("Created a game called '%s' with ID %d.%n", name, gameID);
            return "keep looping";
        }
        else if (Objects.equals(input, "3") || Objects.equals(input, "list games")) {
            ArrayList<GameData> games = facade.listGames();
            if (games != null) {
                Iterator<GameData> gameiter = games.iterator();
                System.out.print("ID - Game Name - White Username - Black Username\n");
                while (gameiter.hasNext()) {
                    GameData next = gameiter.next();
                    String white = next.whiteUsername();
                    String black = next.blackUsername();
                    if (next.whiteUsername() == null || next.whiteUsername().isEmpty()) {
                        white = "null";
                    }
                    if (next.blackUsername() == null || next.blackUsername().isEmpty()) {
                        black = "null";
                    }
                    System.out.printf("%d - %s - %s - %s%n", next.gameID(), next.gameName(), white, black);
                }
                return "keep looping";
            }
            else {
                System.out.print("No games found");
                return "keep looping";
            }

        }
        else if (Objects.equals(input, "4") || Objects.equals(input, "join game")) {
            String id = getString("Please enter a game ID: ");
            String team = getString("Please enter a team color to play as (white/black): ");
            ChessGame.TeamColor color = null;
            if (team.equals("white") || team.equals("w")){
                team = "w";
                color = ChessGame.TeamColor.WHITE;
            } else if (team.equals("black") || team.equals("b")) {
                team = "b";
                color = ChessGame.TeamColor.BLACK;
            } else {
                System.out.print("Did not understand input\n");
                return "keep looping";
            }
            System.out.print("Joining game...\n");
            facade.joinGame(Integer.parseInt(id), color);
            return id + " " + team;
        }
        else if (Objects.equals(input, "5") || Objects.equals(input, "join observer")) {
            String id = getString("Please enter a game ID: ");
            System.out.print("Joining game as an observer...\n");
            facade.joinGame(Integer.parseInt(id), null);
            return id + " n";
        }
        else if (Objects.equals(input, "6") || Objects.equals(input, "logout")) {
            System.out.print("Logging out...\n");
            facade.logout();
            return "stop looping";
        }
        else {
            System.out.print("Input not understood. Try entering \"Help\" to view options.\n");
            return "keep looping";
        }
    }

    private String getString(String prompt) {
        System.out.print(prompt);
        Scanner login = new Scanner(System.in);
        String line = login.nextLine();
        return line.strip();
    }

}
