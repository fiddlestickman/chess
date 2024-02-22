package server;

import chess.ChessGame;
import model.UserData;

public class Main {
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        server.run(2345);

        ChessGame game = new ChessGame();
        Handler temp = new Handler();
        String[] strings = new String[]{"", "", ""};
        UserData user = new UserData("a", "b", "c");
        String jsonstring = temp.Serialize(user);

        String jstring = "{ \"username\":\"\",\"password\":\"\",\"email\":\"\" }";
        try {
            UserData thingy = (UserData) temp.Deserialize(jstring, UserData.class);
        } catch (RequestException e) {}
        int i = 0;
    }
}
