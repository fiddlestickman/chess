package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPosition[][] chessBoard = new ChessPosition[9][9]; //creates the chess board, separated into 8 rows and columns (with a ghostly zeroeth rank and file)

    public ChessBoard() {
        for (int i = 1; i <= 8; i++) // rows (white to black)
        {
            for (int j = 1; j <= 8; j++) // columns (q. rook to k. rook)
            {
                chessBoard[i][j] = new ChessPosition(i, j);
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int column = position.getColumn();
        chessBoard[row][column].removePiece();
        chessBoard[row][column].setPiece(piece); //sets the piece on the chess position indicated.
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return chessBoard[row][column].getPiece();

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        //clears the board of all pieces
        for (int i = 1; i <= 8; i++) // rows (white to black)
        {
            for (int j = 1; j <= 8; j++) // columns (q. rook to k. rook)
            {
                chessBoard[i][j].removePiece();
            }
        }

        //fills the 2nd and 7th ranks with pawns and the 1st and 8th rank with pieces
        for (int i = 1; i <= 8; i++) // columns (q. rook to k. rook)
        {
            //fills the 2nd and 7th ranks with pawns
            chessBoard[2][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            chessBoard[7][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));

            //fills in the 1st and 8th ranks with pieces
            if (i == 1 || i == 8) {
                chessBoard[1][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                chessBoard[8][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
            else if (i == 2 || i == 7){
                chessBoard[1][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                chessBoard[8][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
            }
            else if (i == 3 || i == 6){
                chessBoard[1][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                chessBoard[8][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
            }
            else if (i == 4){
                chessBoard[1][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                chessBoard[8][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
            }
            else if (i == 5){
                chessBoard[1][i].setPiece(new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                chessBoard[8][i].setPiece(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
            }

        }


    }

    public void removePiece(ChessPosition position)
    {
        int row = position.getRow();
        int col = position.getColumn();

        chessBoard[row][col].removePiece();
    }

    //checks if a square is under attack (useful for legal king moves)
    public Collection<ChessMove> squareAttacked (ChessGame.TeamColor attacker, ChessPosition attackedSquare)
    {

        int row = attackedSquare.getRow();
        int col = attackedSquare.getColumn();
        java.util.Collection<ChessMove> attackmoves = new java.util.ArrayList<>();

        //finds all the enemy pieces on the board
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece temp = chessBoard[i][j].getPiece();
                if (temp != null && temp.getTeamColor() == attacker)  //gets their potential moves
                {
                    java.util.Collection<ChessMove> tempmoves = temp.pieceMoves(this, new ChessPosition(i, j));
                    java.util.Iterator<ChessMove> temparray = tempmoves.iterator();

                    for (int k = 0; k < tempmoves.size(); k++) //checks the moves for moves that attack the square given
                    {
                        ChessMove next = temparray.next();
                        if (next.getEndPosition().getRow() == attackedSquare.getRow()
                                && next.getEndPosition().getColumn() == attackedSquare.getColumn())
                            attackmoves.add(next); //adds the move if it attacks the square
                    }
                }
            }
        }
        return attackmoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }
}
