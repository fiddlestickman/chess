package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    int row; //this is the row 1-8 - 1 is white's end, 8 is black's
    int column; //this is the columns a-h - a is rook's on queenside, h is rook's on king's side
    ChessPiece piece;

    public ChessPosition(int row, int col)
    {
        if(1 > row || row > 8)
            throw new RuntimeException("Row out of bounds");
        if(1 > col || col > 8)
            throw new RuntimeException("Column out of bounds");

        this.row=row;
        this.column=col;
        piece = new ChessPiece(chess.ChessGame.TeamColor.NOTHING, chess.ChessPiece.PieceType.NOTHING);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn()
    {
        return column;
    }

    public ChessPiece getPiece()
    {
        return piece;
    }

    //checks if there's a piece; if there isn't, adds the designated piece. Otherwise returns false.
    public boolean setPiece(ChessPiece piece)
    {
        if (this.piece.getPieceType() == ChessPiece.PieceType.NOTHING) {
            this.piece = piece;
            return true;
        }
        else
            return false;
    }
    //checks if there's a piece; if there is, removes the piece. Otherwise returns false.
    public boolean removePiece()
    {
        if (this.piece.getPieceType() == ChessPiece.PieceType.NOTHING)
            return false;
        else {
            piece = new ChessPiece(ChessGame.TeamColor.NOTHING, ChessPiece.PieceType.NOTHING);
            return true;
        }
    }
}
