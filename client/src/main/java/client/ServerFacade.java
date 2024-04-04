package client;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;

public class ServerFacade {
    private String url;
    private String auth;
    public ServerFacade(String url, String auth) {
        this.url = url;
        this.auth = auth;
    }
    public String Login(String username, String password) {
        HTTPHandler handler = new HTTPHandler(null, url + "/session");
        LoginData data = new LoginData(username, password);
        try {
            Main.LoginResponse auth = (Main.LoginResponse) handler.Request("POST", data, Main.LoginResponse.class);
            if (auth.success) {
                return auth.authToken;
            } else {
                throw new RequestException(auth.message, auth.code);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public String Register(String username, String password, String email) {
        HTTPHandler handler = new HTTPHandler(null, url + "/user");
        UserData data = new UserData(username, password, email);
        try {
            Main.LoginResponse auth = (Main.LoginResponse) handler.Request("POST", data, Main.LoginResponse.class);
            if (auth.success) {
                return auth.authToken;
            } else {
                throw new RequestException(auth.message, 500);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public int createGame(String name){
        HTTPHandler handler = new HTTPHandler(auth, url + "/game");
        CreateGameData data = new CreateGameData(name);
        try {
            Main.CreateResponse game = (Main.CreateResponse) handler.Request("POST", data, Main.CreateResponse.class);
            if (game.success) {
                return game.gameID;
            } else {
                throw new RequestException(game.message, 500);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return -1;
        }
    }

    public ArrayList<GameData> listGames() {
        HTTPHandler handler = new HTTPHandler(auth, url + "/game");
        try {
            Main.ListResponse games = (Main.ListResponse) handler.Request("GET", null, Main.ListResponse.class);
            if (games.success) {
                return games.games;
            } else {
                throw new RequestException(games.message, 500);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public GameData getGame(int gameID) {
        HTTPHandler handler = new HTTPHandler(auth, url + "/sg");
        GameIDData data = new GameIDData(gameID);
        try {
            Main.getGameResponse game = (Main.getGameResponse) handler.Request("GET", data, Main.getGameResponse.class);
            if (game.success) {
                return game.game;
            } else {
                throw new RequestException(game.message, 500);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public Main.JoinResponse joinGame(int gameID, ChessGame.TeamColor color) {
        HTTPHandler handler = new HTTPHandler(auth, url + "/game");
        JoinGameData data = new JoinGameData(color, gameID);
        try {
            Main.JoinResponse response = (Main.JoinResponse) handler.Request("PUT", data, Main.JoinResponse.class);
            if (response.success) {
                return response;
            } else {
                throw new RequestException(response.message, 500);
            }
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return null;
        }
    }

    public boolean logout(){
        HTTPHandler handler = new HTTPHandler(auth, url + "/session");
        try {
            Main.Response response = (Main.Response) handler.Request("DELETE", null, Main.Response.class);
            if (!response.success) {
                throw new RequestException(response.message, 500);
            }
            return true;
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return false;
        }
    }

    public boolean clear() {
        HTTPHandler handler = new HTTPHandler(auth, url + "/db");
        try {
            Main.Response response = (Main.Response) handler.Request("DELETE", null, Main.Response.class);
            if (!response.success) {
                throw new RequestException(response.message, 500);
            }
            return true;
        } catch (RequestException e) {
            System.out.print(e.getCode() + " - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return false;
        }
    }
}
