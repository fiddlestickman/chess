package clientTests;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.GameHandler;
import server.Server;
import client.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private ServerFacade facade;
    private ServerFacade unauthfacade;

    private String existUser = "user";
    private String existPass = "pass";
    private String existEmail = "email";
    private String existGame = "game";

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() {

        unauthfacade = new ServerFacade("http://localhost:" + port, null);
        unauthfacade.clear();
        String auth = unauthfacade.Register(existUser, existPass, existEmail);
        facade = new ServerFacade("http://localhost:" + port, auth);
        facade.createGame(existGame);
    }

    @Test
    void register() throws Exception {
        var authData = facade.Register("player1", "password", "p1@email.com");
        assertTrue(authData.length() > 10);
    }

    @Test
    void registerBad() throws Exception {
        var authData = facade.Register(existUser, "pass2", "newemail");
        assertNull(authData);

        authData = facade.Register("existUser", null, "p1@email.com");
        assertNull(authData);
    }

    @Test
    void login() throws Exception {
        var authData = facade.Login(existUser, existPass);
        assertTrue(authData.length() > 10);
    }

    @Test
    void loginBad() throws Exception {
        var authData = facade.Login(existUser + "bad text", existPass);
        assertNull(authData);

        authData = facade.Login(existUser, existPass + "bad text");
        assertNull(authData);
    }

    @Test
    void createGame() throws Exception {
        var gameID = facade.createGame("gameName");
        assertNotEquals(gameID, -1);
    }

    @Test
    void createGameBad() throws Exception {
        var gameID = facade.createGame(null);
        assertEquals(gameID, -1);


        gameID = unauthfacade.createGame(null);
        assertEquals(gameID, -1);
    }

    @Test
    void listGames() throws Exception {
        var games = facade.listGames();
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(existGame, games.getFirst().gameName());

        unauthfacade.clear();
        String auth = unauthfacade.Login(existUser, existPass);
        ServerFacade temp = new ServerFacade("http://localhost:" + port, auth);
        games = temp.listGames();
        assertNull(games);
    }

    @Test
    void listGamesBad() throws Exception {
        var games = unauthfacade.listGames();
        assertNull(games);
    }

    @Test
    void joinGame() throws Exception {
        var games = facade.joinGame(1, null);
        assertNotNull(games);

        games = facade.joinGame(1, ChessGame.TeamColor.WHITE);
        assertNotNull(games);
    }
    @Test
    void joinGameBad() throws Exception {
        var games = unauthfacade.joinGame(1, null);
        assertNull(games);

        games = facade.joinGame(-1, null);
        assertNull(games);


        games = facade.joinGame(1000, null);
        assertNull(games);
    }
}
