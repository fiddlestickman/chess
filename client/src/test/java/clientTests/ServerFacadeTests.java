package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import client.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private ServerFacade facade;
    private String existUser = "user";
    private String existPass = "pass";
    private String existEmail = "email";

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

        facade = new ServerFacade("http://localhost:" + port, null);
        facade.clear();

        String auth = facade.Register(existUser, existPass, existEmail);

        facade = new ServerFacade("http://localhost:" + port, auth);
    }
    @Test
    public void sampleTest() {
        assertTrue(true);
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

}
