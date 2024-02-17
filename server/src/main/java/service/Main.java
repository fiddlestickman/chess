package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameData;

import java.util.ArrayList;

public class Main {
    //the service class should take data from handlers (login, join game, etc), and make things happen
    //get data from the database via the dataAccess class
    //main job - actual code here
    public static void main(String[] args) {
        RegisterService reg = new RegisterService();
        LoginService login = new LoginService();
        LogoutService logout = new LogoutService();
        JoinGameService join = new JoinGameService();
        ListGamesService list = new ListGamesService();
        ClearService clear = new ClearService();
        CreateGameService create = new CreateGameService();

        try {
            clear.Clear();
            String authToken = reg.Register("john", "12345678", "john@me.com");
            logout.Logout(authToken);
            String auth2 = reg.Register("john2", "1234", "john2@me.com");
            authToken = login.Login("john", "12345678");
            int gameID = create.CreateGame(authToken, "mygame");
            int game2 = create.CreateGame(auth2, "mygame2");
            join.JoinGame(authToken, ChessGame.TeamColor.WHITE, gameID);
            join.JoinGame(authToken, ChessGame.TeamColor.BLACK, gameID);
            join.JoinGame(authToken, ChessGame.TeamColor.WHITE, game2);
            join.JoinGame(auth2, ChessGame.TeamColor.BLACK, game2);

            ArrayList<GameData> games = new ArrayList<>();
            games.addAll(list.ListGames(auth2));

            clear.Clear();

        } catch (DataAccessException e){
            //do nothing for tests
        } catch (ServiceException e){
            String hey = e.getMessage();
            int i = 9;
        }
    }


}