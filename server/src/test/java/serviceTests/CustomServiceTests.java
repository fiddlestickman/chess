package serviceTests;

import chess.ChessGame;
import org.junit.jupiter.api.*;

import java.net.HttpURLConnection;

import passoffTests.testClasses.TestException;
import passoffTests.testClasses.TestModels;
import server.Server;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class CustomServiceTests {
    private static AdminService adminserve;
    private static GameService gameserve;
    private static UserService userserve;
    private static String user;
    private static String pass;
    private static String email;
    private static String auth;

    @BeforeAll
    public static void init() throws Exception{
        adminserve = new AdminService();
        gameserve = new GameService();
        userserve = new UserService();

        user = "username";
        pass = "password";
        email = "email.com";

    }
    @BeforeEach
    public void setup() throws Exception {
        adminserve.clear();
        auth = userserve.register(user, pass, email);
    }

    @Test
    @Order(1)
    @DisplayName("Normal Register")
    public void registerSuccess() throws Exception {
        Assertions.assertNotNull(userserve.register("user", "pass", "email"));
    }
    @Test
    @Order(2)
    @DisplayName("Bad Register")
    public void registerFail() throws Exception {
        try {
            userserve.register(null, "pass", "email");
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("bad request", 400), e);
        } try {
            userserve.register("user", null, "email");
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("bad request", 400), e);
        } try {
            userserve.register("user", "pass", null);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("bad request", 400), e);
        }
    }
    @Test
    @Order(3)
    @DisplayName("Normal Login")
    public void loginSuccess() throws Exception {
        Assertions.assertNotNull(userserve.login(user, pass));
    }
    @Test
    @Order(4)
    @DisplayName("Bad Login")
    public void loginFail() throws Exception {
        try {
             userserve.login("", pass);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        } try {
            userserve.login(user, "");
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        }
    }
    @Test
    @Order(5)
    @DisplayName("Normal Logout")
    public void logoutSuccess() throws Exception {
            userserve.logout(auth);
    }
    @Test
    @Order(6)
    @DisplayName("Bad Logout")
    public void logoutFail() throws Exception {
        try {
            userserve.logout("");
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        }
    }
    @Test
    @Order(7)
    @DisplayName("Clear")
    public void clearSuccess() throws Exception {
        adminserve.clear();
    }
    @Test
    @Order(8)
    @DisplayName("Normal Create")
    public void createSuccess() throws Exception {
        gameserve.createGame(auth, "game");
    }
    @Test
    @Order(9)
    @DisplayName("Bad Create")
    public void createFail() throws Exception {
        try {
            gameserve.createGame(null, "game");
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        }try {
            gameserve.createGame(auth, null);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("bad request", 400), e);
        }
    }

    @Test
    @Order(10)
    @DisplayName("Normal Join")
    public void joinSuccess() throws Exception {
        int id = gameserve.createGame(auth, "game");
        gameserve.joinGame(auth, ChessGame.TeamColor.WHITE, id);
        gameserve.joinGame(auth, ChessGame.TeamColor.BLACK, id);
        gameserve.joinGame(auth, null, id);
    }
    @Test
    @Order(11)
    @DisplayName("Bad Join")
    public void joinFail() throws Exception {
        int id = gameserve.createGame(auth, "game");
        gameserve.joinGame(auth, ChessGame.TeamColor.WHITE, id);
        try {
            gameserve.joinGame(auth, ChessGame.TeamColor.WHITE, id);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("already taken", 403), e);
        } try {
            gameserve.joinGame(null, ChessGame.TeamColor.WHITE, id);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        } try {
            gameserve.joinGame(auth, ChessGame.TeamColor.BLACK, 24345);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("bad request", 400), e);
        }
    }
    @Test
    @Order(12)
    @DisplayName("Normal List")
    public void listSuccess() throws Exception {
        int id = gameserve.createGame(auth, "game");
        gameserve.joinGame(auth, ChessGame.TeamColor.WHITE, id);
        Assertions.assertNotNull(gameserve.listGames(auth));
    }
    @Test
    @Order(13)
    @DisplayName("Bad List")
    public void listFail() throws Exception {
        int id = gameserve.createGame(auth, "game");
        gameserve.joinGame(auth, ChessGame.TeamColor.WHITE, id);
        try {
            gameserve.listGames(null);
            throw new TestException("should not pass");
        } catch (ServiceException e) {
            Assertions.assertEquals(new ServiceException("unauthorized", 401), e);
        }
    }
}
