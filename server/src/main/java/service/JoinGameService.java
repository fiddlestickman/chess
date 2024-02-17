package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.*;

public class JoinGameService extends Service{
    public JoinGameService() {}

    public void JoinGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws DataAccessException, ServiceException {
        GameDAO gameDAO = MemoryGameDAO.getInstance();
        AuthData auth = Authenticate(authToken);
        GameData game = gameDAO.readGameID(gameID);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken");
            }
            GameData newgame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
        else if (teamColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) { //can't override another user
                throw new ServiceException("Player already taken");
            }
            GameData newgame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.update(newgame);
        }
    }
}
