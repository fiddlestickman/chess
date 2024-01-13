package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = myPosition.getPiece();

        if (piece.getPieceType() == null)
        {
            return null; //no piece on the square selected
        }

        //makes sure the position given and the board state match
        // they both have a potentially different piece assigned to the same square
        if (board.getPiece(myPosition).getPieceType() != piece.getPieceType()
        || board.getPiece(myPosition).getTeamColor() != piece.getTeamColor())
        {
            throw new RuntimeException("somehow the board and position given don't match");
        }

        if (piece.getPieceType() == PieceType.PAWN)
        {
            //move forward if no pieces in the way (+1 row if white, -1 row if black)
            //move diagonally if enemy piece on that square (+1 row, +/- 1 column if white, -1 row same for black)
            //check if diagonals or forward row is out of bounds
        }
        else if (piece.getPieceType() == PieceType.KNIGHT)
        {
            //eight options of 1 and 2, +/-, row or column
            //make sure that there's no friendly pieces in the way (illegal move)
            //check if row/columns are out of bounds
        }
        else if (piece.getPieceType() == PieceType.BISHOP)
        {
            //returns +n/+n, +n/-n, -n/+n, -n/-n, plus the current square
            //make sure that there's no friendly pieces in the way (cannot go to or beyond that square)
            //make sure there's no enemy pieces in the way (cannot go beyond that square)
            //make sure it's not out of bounds
        }
        else if (piece.getPieceType() == PieceType.ROOK)
        {
            //returns +n/+0, -n/+0, +0/+n, +0/-n, plus current square
            //make sure there's no friendly pieces in the way (cannot go to or beyond that square
            //make sure there's no enemy pieces in the way (cannot go beyond that square)
            //make sure it's not out of bounds
        }
        else if (piece.getPieceType() == PieceType.QUEEN)
        {
            //combination of rook and bishop moves, just copy the code from both
        }
        else if (piece.getPieceType() == PieceType.KING)
        {
            //returns +1/0/-1 row, +1/0/-1 column around the position
            //check if there are friendly pieces in the way
            //check if there are any enemy pieces that could attack that square (oof)
                //could run the same equation for each enemy piece when selecting the king (recursion though)
                //make a special piecemoves for the enemy king that avoids recursion
                    //make special piecemoves that accounts for the new king position (moving one step further from bishop isn't safe)

            //make sure it's not out of bounds
        }
        else
            return null; //no chess piece on that square

    }
}
