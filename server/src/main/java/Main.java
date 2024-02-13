package service;
import chess.*;

public class Main {
    //the service class should take data from handlers (login, join game, etc), and make things happen
    //get data from the database via the dataAccess class
    //main job - actual code here
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }


}