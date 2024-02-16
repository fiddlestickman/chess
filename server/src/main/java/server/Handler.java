package server;
import chess.*;

public class Handler {

    //this class should take calls from the server (html) and convert it to a form java understands
    //then pass it off to the service.
    //also packages up the java output to something html understands
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }

    /*
    Here's how you do singleton implementation (good for handlers)

    private static MemoryAuthDAO INSTANCE;
    private final ArrayList<AuthData> authDatabase;

    private MemoryAuthDAO() {
        authDatabase = new ArrayList<>();
    }

    public static MemoryAuthDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MemoryAuthDAO();
        }
        return INSTANCE;
    }

     */


}