package dataAccessTests;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataAccess.*;
import model.*;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;

import java.util.ArrayList;

public class CustomServiceTests {
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static String user;
    private static String pass;
    private static String email;
    private static String auth;
    private static GameData g;

    @BeforeAll
    public static void init() throws Exception {
        authDAO = SQLAuthDAO.getInstance();
        gameDAO = SQLGameDAO.getInstance();
        userDAO = SQLUserDAO.getInstance();

        user = "username";
        pass = "password";
        email = "email.com";
        auth = "authtoken";

        ChessGame game = new ChessGame();
        g = new GameData(0, null, null,"game1", game);

    }

    @BeforeEach
    public void setup() throws Exception {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        userDAO.create(new UserData(user, pass, email));
        authDAO.create(new AuthData(auth, user));
    }

    @Test
    @Order(1)
    @DisplayName("Read In/Out Game")
    public void checkGame() throws Exception {

        ChessGame game = new ChessGame();
        int id = gameDAO.create(g);
        GameData gameout = gameDAO.readGameID(id);
        ChessGame newGame = gameout.game();

        Assertions.assertEquals(game, newGame);
    }

    @Test
    @Order(2)
    @DisplayName("Update Game Good")
    public void updateGame() throws Exception {

        int id = gameDAO.create(g);

        ChessGame game = new ChessGame();
        //makes a move in the original game (not in database)
        ChessPosition start = new ChessPosition(2,2);
        ChessPosition end = new ChessPosition(4, 2);
        ChessMove move = new ChessMove(start, end, null);
        game.makeMove(move);

        //updates the server with the new game
        GameData newgame = new GameData(id, null, null,"game1", game);
        gameDAO.update(newgame);

        GameData gameout = gameDAO.readGameID(id);
        ChessGame dataGame = gameout.game();
        ChessGame initGame = new ChessGame();

        Assertions.assertEquals(game, dataGame);
        Assertions.assertNotEquals(initGame, dataGame);
        Assertions.assertNotEquals(game, initGame);
    }

    @Test
    @Order(3)
    @DisplayName("Auth Read Good")
    public void authReadGood() throws Exception {
        AuthData data = authDAO.readAuth(auth);
        Assertions.assertNotNull(data);
    }

    @Test
    @Order(4)
    @DisplayName("Auth Read Bad")
    public void authReadBad() throws Exception {
        AuthData data = authDAO.readAuth(auth+"bad");
        Assertions.assertNull(data);
    }

    @Test
    @Order(5)
    @DisplayName("Auth Create Good")
    public void authCreateGood() throws Exception {
        authDAO.create(new AuthData("newauth", user));

        AuthData data = authDAO.readAuth("newauth");
        Assertions.assertNotNull(data);
    }

    @Test
    @Order(6)
    @DisplayName("Auth Create Bad")
    public void authCreateBad() throws Exception {
        boolean success = false;
        try {
            authDAO.create(new AuthData("newauth", "not real user"));
        } catch (DataAccessException e) {
            success = true;
        }
        Assertions.assertTrue(success);
    }

    @Test
    @Order(7)
    @DisplayName("Auth Delete Good")
    public void authDeleteGood() throws Exception {
        authDAO.create(new AuthData("newauth", user));
        AuthData data = authDAO.readAuth("newauth");
        authDAO.delete(data);

        AuthData newdata = authDAO.readAuth(auth+"bad");
        Assertions.assertNull(newdata);
    }

    @Test
    @Order(8)
    @DisplayName("Auth Delete Bad")
    public void authDeleteBad() throws Exception {
        AuthData data = new AuthData("fake", user);
        authDAO.delete(data);
        Assertions.assertNotNull(authDAO.readAuth(auth));
        Assertions.assertNull(authDAO.readAuth("fake"));
    }


    @Test
    @Order(9)
    @DisplayName("Auth Clear")
    public void authClear() throws Exception {
        authDAO.clear();

        AuthData newdata = authDAO.readAuth(auth);
        Assertions.assertNull(newdata);
    }


    @Test
    @Order(10)
    @DisplayName("Game Create Good")
    public void gameCreateGood() throws Exception {
        Integer id = gameDAO.create(g);
        Assertions.assertNotNull(id);
    }

    @Test
    @Order(11)
    @DisplayName("Game Create Bad")
    public void gameCreateBad() throws Exception {
        GameData bad = new GameData(0, "fake", "fake", "gamename", new ChessGame());

        boolean success = false;
        try {
            Integer id = gameDAO.create(bad);
        } catch (DataAccessException e) {
            success = true;
        }
        Assertions.assertTrue(success);
    }

    @Test
    @Order(12)
    @DisplayName("Game Read Good")
    public void gameReadGood() throws Exception {
        int id = gameDAO.create(g);
        GameData out = gameDAO.readGameID(id);
        GameData in = new GameData(out.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
        Assertions.assertEquals(in, out);
    }

    @Test
    @Order(13)
    @DisplayName("Game Read Bad")
    public void gameReadBad() throws Exception {

        int id = gameDAO.create(g);
        GameData out = gameDAO.readGameID(id+5);
        Assertions.assertNull(out);
    }

    @Test
    @Order(14)
    @DisplayName("Game Update Bad")
    public void gameUpdateBad() throws Exception {

        int id = gameDAO.create(g);
        GameData fake = new GameData(27,null, null, "fake", new ChessGame());

        boolean success = false;
        try {
            gameDAO.update(fake);
        } catch (DataAccessException e) {
            success = true;
        }
        Assertions.assertTrue(success);
    }

    @Test
    @Order(15)
    @DisplayName("Game ReadAll Good")
    public void gameReadAllGood() throws Exception {

        ArrayList<GameData> games = new ArrayList<>();
        games.addAll(gameDAO.readAll());

        Assertions.assertEquals(games, new ArrayList<GameData>());

        gameDAO.create(g);
        games.addAll(gameDAO.readAll());
        Assertions.assertNotNull(games);
    }

    @Test
    @Order(16)
    @DisplayName("Game Clear")
    public void gameClear() throws Exception {

        int id = gameDAO.create(g);
        gameDAO.clear();

        GameData newgame = gameDAO.readGameID(id);
        Assertions.assertNull(newgame);
    }

    @Test
    @Order(17)
    @DisplayName("User Create Good")
    public void userCreateGood() throws Exception {
        boolean success = true;
        try {
            userDAO.create(new UserData("newuser", "newpass", "email2"));
        } catch (DataAccessException e) {
            success = false;
        }
        Assertions.assertTrue(success);
    }

    @Test
    @Order(18)
    @DisplayName("User Create Bad")
    public void userCreateBad() throws Exception {
        boolean success = false;
        try {
            userDAO.create(new UserData(user, "newpass", email));
        } catch (DataAccessException e) {
            success = true;
        }
        Assertions.assertTrue(success);
    }

    @Test
    @Order(19)
    @DisplayName("User Read Good")
    public void userReadGood() throws Exception {
        UserData in = new UserData(user, pass, email);
        UserData out = userDAO.readUserName(user, pass);
        Assertions.assertEquals(in, out);

        out = userDAO.readUserEmail(email, pass);
        Assertions.assertEquals(in, out);
    }

    @Test
    @Order(20)
    @DisplayName("User Read Bad")
    public void userReadBad() throws Exception {
        UserData out = userDAO.readUserName(user+"bad", pass);
        Assertions.assertNull(out);

        out = userDAO.readUserEmail(email+"bad", pass);
        Assertions.assertNull(out);
    }

    @Test
    @Order(21)
    @DisplayName("User Clear")
    public void userClear() throws Exception {

        userDAO.create(new UserData("newuser", "newpass", "newemail"));

        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();

        Assertions.assertNull(userDAO.readUserName(user, pass));
        Assertions.assertNull(userDAO.readUserName("newuser", "newpass"));
    }
}