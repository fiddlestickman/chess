package chess;

import java.util.Arrays;
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
        ChessPiece piece = myPosition.getPiece();
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

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
            legalMoves.addAll(pawnMoves(myPosition, board));//adds all the legal pawn moves to the legal moves
        }
        else if (piece.getPieceType() == PieceType.KNIGHT)
        {
            legalMoves.addAll(knightMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.BISHOP)
        {
            legalMoves.addAll(bishopMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.ROOK)
        {
            legalMoves.addAll(rookMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.QUEEN)
        {
            legalMoves.addAll(queenMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.KING)
        {
            legalMoves.addAll(kingMoves(myPosition, board));
        }
        else
            return null; //no chess piece on that square

        return legalMoves;
    }

    //rules for pawn moves
    private Collection<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = myPosition.getPiece();
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        //moves for white
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            chess.ChessPiece.PieceType promo = null; //sets promo to null unless on seventh rank
            if (row == 7)
            {
                promo = chess.ChessPiece.PieceType.QUEEN; //autopromotes to queen for now, not sure how to get that input
            }
            if ( row < 7) {
                //checks if front of pawn is legal

                ChessPosition temp = new ChessPosition(row+1, column); //selects the position in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square in front of pawn
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the move going forward to the legal moves list

                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row + 1, column - 1); //selects the position in front of pawn to the left
                    move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                    test = board.getPiece(temp); //gets the piece on the square in front of pawn
                    if (test.getTeamColor() == ChessGame.TeamColor.BLACK) //checks that there's a capturable piece
                        legalMoves.add(move); //adds the capture move to the legal moves list
                }

                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row + 1, column + 1); //selects the position in front of pawn to the right
                    move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                    test = board.getPiece(temp); //gets the piece on the square in front of pawn
                    if (test.getTeamColor() == ChessGame.TeamColor.BLACK) //checks that there's a capturable piece
                        legalMoves.add(move); //adds the capture move to the legal moves list
                }
            }
            if (row == 2)
            {
                //double move at start
                ChessPosition temp = new ChessPosition(row+2, column); //selects the position two ahead of pawn
                ChessPosition temp2 = new ChessPosition(row+1, column); //checks the spot directly in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square two steps in front of pawn
                ChessPiece test2 = board.getPiece(temp2); //gets the piece on the square in front of pawn
                if (test.getPieceType() == null && test2.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the double move to the legal moves list

            }
            if (row == 5)//en passant possible if pawn is on fifth rank
            {
                ChessMove lastMove = board.getLastMove();
                if (lastMove.getStartPosition().getRow() == 7
                && lastMove.getEndPosition().getRow() == 5
                && lastMove.getStartPosition().getPiece().getPieceType() == PieceType.PAWN) //makes sure that the last move was a double pawn move
                {
                    if(myPosition.getColumn() == lastMove.getEndPosition().getColumn()+1) //if the pawn moved to the right of the pawn
                    {
                        ChessPosition temp = new ChessPosition(row + 1, column + 1); //selects the position in front of pawn to the right
                        ChessMove move = new ChessMove(myPosition, temp, promo); //sets up the capture move
                        legalMoves.add(move); //adds the move to the list
                    }
                    if(myPosition.getColumn() == lastMove.getEndPosition().getColumn()-1) //if the pawn moved to the left of the pawn
                    {
                        ChessPosition temp = new ChessPosition(row + 1, column - 1); //selects the position in front of pawn to the left
                        ChessMove move = new ChessMove(myPosition, temp, promo); //sets up the capture move
                        legalMoves.add(move); //adds the move to the list
                    }
                }
            }

        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK)
        {
            chess.ChessPiece.PieceType promo = null; //sets promo to null unless on seventh rank
            if (row == 2)
            {
                promo = chess.ChessPiece.PieceType.QUEEN; //autopromotes to queen for now, not sure how to get that input
            }
            if ( row > 2) {
                //checks if front of pawn is legal

                ChessPosition temp = new ChessPosition(row-1, column); //selects the position in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square in front of pawn
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the move going forward to the legal moves list

                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row - 1, column - 1); //selects the position in front of pawn to the left
                    move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                    test = board.getPiece(temp); //gets the piece on the square in front of pawn
                    if (test.getTeamColor() == ChessGame.TeamColor.WHITE) //checks that there's a capturable piece
                        legalMoves.add(move); //adds the capture move to the legal moves list
                }

                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row - 1, column + 1); //selects the position in front of pawn to the right
                    move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                    test = board.getPiece(temp); //gets the piece on the square in front of pawn
                    if (test.getTeamColor() == ChessGame.TeamColor.WHITE) //checks that there's a capturable piece
                        legalMoves.add(move); //adds the capture move to the legal moves list
                }
            }
            if (row == 7)
            {
                //double move at start
                ChessPosition temp = new ChessPosition(row-2, column); //selects the position two ahead of pawn
                ChessPosition temp2 = new ChessPosition(row-1, column); //checks the spot directly in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square two steps in front of pawn
                ChessPiece test2 = board.getPiece(temp2); //gets the piece on the square in front of pawn
                if (test.getPieceType() == null && test2.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the double move to the legal moves list

            }
            if (row == 4)//en passant possible if pawn is on fourth rank
            {
                ChessMove lastMove = board.getLastMove();
                if (lastMove.getStartPosition().getRow() == 2
                        && lastMove.getEndPosition().getRow() == 4
                        && lastMove.getStartPosition().getPiece().getPieceType() == PieceType.PAWN) //makes sure that the last move was a double pawn move
                {
                    if(myPosition.getColumn() == lastMove.getEndPosition().getColumn()+1) //if the pawn moved to the right of the pawn
                    {
                        ChessPosition temp = new ChessPosition(row - 1, column + 1); //selects the position in front of pawn to the right
                        ChessMove move = new ChessMove(myPosition, temp, promo); //sets up the capture move
                        legalMoves.add(move); //adds the move to the list
                    }
                    if(myPosition.getColumn() == lastMove.getEndPosition().getColumn()-1) //if the pawn moved to the left of the pawn
                    {
                        ChessPosition temp = new ChessPosition(row - 1, column - 1); //selects the position in front of pawn to the left
                        ChessMove move = new ChessMove(myPosition, temp, promo); //sets up the capture move
                        legalMoves.add(move); //adds the move to the list
                    }
                }
            }

        }
        return legalMoves;
    }

    //iterates through the eight possible knight moves
    private Collection<ChessMove> knightMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = myPosition.getPiece();
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        ChessMove move = kMove(row, column, row+2, column+1, piece, board); //creates the first knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row+1, column+2, piece, board); //creates the first knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-1, column+2, piece, board); //creates the third knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-2, column+1, piece, board); //creates the fourth knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-2, column-1, piece, board); //creates the fifth knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-1, column-2, piece, board); //creates the sixth knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row+1, column-2, piece, board); //creates the seventh knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row+2, column-1, piece, board); //creates the eighth knight move
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        return legalMoves;
    }

    //rules for the knight move
    private ChessMove kMove(int startRow, int startCol, int finalRow, int finalCol, ChessPiece piece, ChessBoard board)
    {
        if (finalRow < 9 && finalCol < 9 && finalRow > 0 && finalCol > 0) //makes sure the move is not out of bounds
        {
            ChessPosition myPosition = new ChessPosition(startRow, startCol);//sets the start position
            ChessPosition temp = new ChessPosition(finalRow, finalCol); //selects the second clockwise position around knight
            ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
            ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
            if (test.getPieceType() == null || test.getTeamColor() != piece.getTeamColor() ) //checks that there's no friendly piece in the way
                return move;
            else
                return null;
        }
        else
            return null;
    }

    private Collection<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board)
    {
        return dMoves(myPosition, board); //bishops only move diagonally
    }

    private Collection<ChessMove> rookMoves(ChessPosition myPosition, ChessBoard board)
    {
        return cMoves(myPosition, board); //rooks only move cardinally
    }

    private Collection<ChessMove> queenMoves(ChessPosition myPosition, ChessBoard board)
    {
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        legalMoves.addAll(cMoves(myPosition, board)); //queens move cardinally like rooks
        legalMoves.addAll(dMoves(myPosition, board)); //and diagonally
        return legalMoves;
    }
    private Collection<ChessMove> kingMoves(ChessPosition myPosition, ChessBoard board)
    {
        //returns +1/0/-1 row, +1/0/-1 column around the position
        //check if there are friendly pieces in the way
        //check if there are any enemy pieces that could attack that square (oof)
        //could run the same equation for each enemy piece when selecting the king (recursion though)
        //make a special piecemoves for the enemy king that avoids recursion
        //make special piecemoves that accounts for the new king position (moving one step further from bishop isn't safe)

        //make sure it's not out of bounds
        return null;
    }

    //diagonal moves (i.e. bishop and queen)
    private Collection<ChessMove> dMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = myPosition.getPiece();
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        boolean legal = true;
        int distance = 0; //distance diagonally from the bishop
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the bishop
            if (row+distance >= 9 || column+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row + distance, column + distance); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0;
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the bishop
            if (row-distance <= 0 || column+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row - distance, column + distance); //selects the next spot southeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0;
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the bishop
            if (row-distance <= 0 || column-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row - distance, column - distance); //selects the next spot southwest of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0;
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the bishop
            if (row+distance >= 9 || column-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row + distance, column - distance); //selects the next spot northwest of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        return legalMoves;

    }

    //cardinal moves (i.e. rook and queen)
    private Collection<ChessMove> cMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = myPosition.getPiece();
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        boolean legal = true;
        int distance = 0; //distance away from rook
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the rook
            if (row+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row + distance, column); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0; //distance away from rook
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the rook
            if (column+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row, column+distance); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0; //distance away from rook
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the rook
            if (row-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row-distance, column); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }

        legal = true;
        distance = 0; //distance away from rook
        while (legal) //iterates through moves until no longer legal
        {
            distance++;//increments the distance from the rook
            if (row-column <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPosition temp = new ChessPosition(row, column-distance); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test.getPieceType() == null) //checks that there's no piece in the way
                    legalMoves.add(move);
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else
                    legal = false;
            }
        }
        return legalMoves;
    }


}
