package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        if (board.getPiece(myPosition) == null) {
            return legalMoves; //no piece on the square selected
        }
        ChessPiece piece = board.getPiece(myPosition);

        //makes sure the position given and the board state match
        // they both have a potentially different piece assigned to the same square
        if (board.getPiece(myPosition).getPieceType() != piece.getPieceType()
                || board.getPiece(myPosition).getTeamColor() != piece.getTeamColor()) {
            throw new RuntimeException("somehow the board and position given don't match");
        }

        if (piece.getPieceType() == PieceType.PAWN) {
            legalMoves.addAll(pawnMoves(myPosition, board));//adds all the legal pawn moves to the legal moves
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            legalMoves.addAll(knightMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.BISHOP) {

            legalMoves.addAll(bishopMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            legalMoves.addAll(rookMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            legalMoves.addAll(queenMoves(myPosition, board));
        }
        else if (piece.getPieceType() == PieceType.KING) {
            legalMoves.addAll(kingMoves(myPosition, board));
        }
        else
            return legalMoves; //no chess piece on that square

        return legalMoves;
    }
    //rules for pawn moves
    private Collection<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        //moves for white
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPiece.PieceType promo = null;
            if (row == 7) {
                //checks if front of pawn is legal
                ChessPosition temp = new ChessPosition(row+1, column); //selects the position in front of pawn
                legalMoves.addAll(pMove(myPosition, board, temp, true));
                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row + 1, column - 1); //selects the position in front of pawn to the left
                    legalMoves.addAll(pMove(myPosition, board, temp, true));
                }
                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row + 1, column + 1); //selects the position in front of pawn to the right
                    legalMoves.addAll(pMove(myPosition, board, temp, true));
                }
            }
            if ( row < 7) {
                //checks if front of pawn is legal
                ChessPosition temp = new ChessPosition(row+1, column); //selects the position in front of pawn
                legalMoves.addAll(pMove(myPosition, board, temp, false));
                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row + 1, column - 1); //selects the position in front of pawn to the left
                    legalMoves.addAll(pMove(myPosition, board, temp, false));
                }
                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row + 1, column + 1); //selects the position in front of pawn to the right
                    legalMoves.addAll(pMove(myPosition, board, temp, false));
                }
            }
            if (row == 2) {
                //double move at start
                ChessPosition temp = new ChessPosition(row+2, column); //selects the position two ahead of pawn
                ChessPosition temp2 = new ChessPosition(row+1, column); //checks the spot directly in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square two steps in front of pawn
                ChessPiece test2 = board.getPiece(temp2); //gets the piece on the square in front of pawn
                if (test == null && test2 == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the double move to the legal moves list

            }
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            chess.ChessPiece.PieceType promo = null; //sets promo to null unless on seventh rank
            if (row == 2) {
                //checks if front of pawn is legal
                ChessPosition temp = new ChessPosition(row-1, column); //selects the position in front of pawn
                legalMoves.addAll(pMove(myPosition, board, temp, true));
                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row - 1, column - 1); //selects the position in front of pawn to the left
                    legalMoves.addAll(pMove(myPosition, board, temp, true));
                }
                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row - 1, column + 1); //selects the position in front of pawn to the right
                    legalMoves.addAll(pMove(myPosition, board, temp, true));
                }
            }
            if ( row > 2) {
                //checks if front of pawn is legal
                ChessPosition temp = new ChessPosition(row-1, column); //selects the position in front of pawn
                legalMoves.addAll(pMove(myPosition, board, temp, false));
                //checks if left front of pawn is legal
                if (column > 1) {
                    temp = new ChessPosition(row - 1, column - 1); //selects the position in front of pawn to the left
                    legalMoves.addAll(pMove(myPosition, board, temp, false));
                }
                //checks if right front of pawn is legal
                if (column < 8) {
                    temp = new ChessPosition(row - 1, column + 1); //selects the position in front of pawn to the right
                    legalMoves.addAll(pMove(myPosition, board, temp, false));
                }
            }
            if (row == 7) {
                //double move at start
                ChessPosition temp = new ChessPosition(row-2, column); //selects the position two ahead of pawn
                ChessPosition temp2 = new ChessPosition(row-1, column); //checks the spot directly in front of pawn
                ChessMove move = new ChessMove(myPosition, temp, promo); //states that pawn is not promoting
                ChessPiece test = board.getPiece(temp); //gets the piece on the square two steps in front of pawn
                ChessPiece test2 = board.getPiece(temp2); //gets the piece on the square in front of pawn
                if (test == null && test2 == null) //checks that there's no piece in the way
                    legalMoves.add(move); //adds the double move to the legal moves list
            }
        }
        return legalMoves;
    }

    private Collection<ChessMove> pMove(ChessPosition myPosition, ChessBoard board, ChessPosition end, boolean promotion) {

        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        if (!promotion) {
            PieceType promo = null;
            ChessMove move = new ChessMove(myPosition, end, promo); //states that pawn is not promoting
            ChessPiece test = board.getPiece(end); //gets the piece on the square in front of pawn
            if (test == null && myPosition.getColumn() == end.getColumn()) //checks that there's no piece in the way
                legalMoves.add(move); //adds the move going forward to the legal moves list
            if (test != null && test.getTeamColor() != board.getPiece(myPosition).getTeamColor() && end.getColumn() != myPosition.getColumn())
                legalMoves.add(move); //adds the move going diagonally to the legal moves list
        }

        else {
            PieceType promo = chess.ChessPiece.PieceType.QUEEN;
            ChessMove move1 = new ChessMove(myPosition, end, promo); //states that pawn is not promoting
            promo = chess.ChessPiece.PieceType.BISHOP;
            ChessMove move2 = new ChessMove(myPosition, end, promo); //states that pawn is not promoting
            promo = chess.ChessPiece.PieceType.ROOK;
            ChessMove move3 = new ChessMove(myPosition, end, promo); //states that pawn is not promoting
            promo = chess.ChessPiece.PieceType.KNIGHT;
            ChessMove move4 = new ChessMove(myPosition, end, promo); //states that pawn is not promoting
            ChessPiece test = board.getPiece(end); //gets the piece on the square in front of pawn
            if (test == null && myPosition.getColumn() == end.getColumn()) {
                legalMoves.add(move1); //adds the move going forward to the legal moves list
                legalMoves.add(move2); //adds the move going forward to the legal moves list
                legalMoves.add(move3); //adds the move going forward to the legal moves list
                legalMoves.add(move4); //adds the move going forward to the legal moves list
            }
            if (test != null && test.getTeamColor() != board.getPiece(myPosition).getTeamColor()
                    && end.getColumn() != myPosition.getColumn()) {
                legalMoves.add(move1); //adds the move going forward to the legal moves list
                legalMoves.add(move2); //adds the move going forward to the legal moves list
                legalMoves.add(move3); //adds the move going forward to the legal moves list
                legalMoves.add(move4); //adds the move going forward to the legal moves list
            }
        }
        return legalMoves;
    }

    //iterates through the eight possible knight moves
    private Collection<ChessMove> knightMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
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
        if (finalRow <= 8 && finalCol <= 8 && finalRow >= 1 && finalCol >= 1) //makes sure the move is not out of bounds
        {
            ChessPosition myPosition = new ChessPosition(startRow, startCol);//sets the start position
            ChessPosition temp = new ChessPosition(finalRow, finalCol); //selects the second clockwise position around knight
            ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
            ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
            if (test == null) //checks that there's no piece in the way
                return move;
            else if (test.getTeamColor() != piece.getTeamColor()) //or that the piece there is an enemy
                return move;
            else
                return null;
        }
        else
            return null;
    }

    private Collection<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board)
    {
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        legalMoves.addAll(lineMove(myPosition, board, 1, 1));
        legalMoves.addAll(lineMove(myPosition, board, 1, -1));
        legalMoves.addAll(lineMove(myPosition, board, -1, -1));
        legalMoves.addAll(lineMove(myPosition, board, -1, 1));
        return legalMoves;
    }

    private Collection<ChessMove> rookMoves(ChessPosition myPosition, ChessBoard board) {
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        legalMoves.addAll(lineMove(myPosition, board, 1, 0));
        legalMoves.addAll(lineMove(myPosition, board, 0, 1));
        legalMoves.addAll(lineMove(myPosition, board, -1, 0));
        legalMoves.addAll(lineMove(myPosition, board, 0, -1));
        return legalMoves;
    }

    private Collection<ChessMove> queenMoves(ChessPosition myPosition, ChessBoard board) {
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);
        legalMoves.addAll(rookMoves(myPosition, board)); //queens move like rooks
        legalMoves.addAll(bishopMoves(myPosition, board)); //and bishops
        return legalMoves;
    }
    private Collection<ChessMove> kingMoves(ChessPosition myPosition, ChessBoard board)
    {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        ChessMove move = kMove(row, column, row+1, column, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row+1, column+1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row, column+1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-1, column+1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-1, column, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row-1, column-1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row, column-1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        move = kMove(row, column, row+1, column-1, piece, board);
        if (move != null)
            legalMoves.add(move); //adds the move if it's legal

        return legalMoves;
    }

    private Collection<ChessMove> lineMove(ChessPosition myPosition, ChessBoard board, int rowIncrease, int colIncrease) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        java.util.ArrayList<ChessMove> legalMoves = new java.util.ArrayList<ChessMove>(0);

        boolean legal = true;
        int distance = 0; //distance diagonally from the bishop
        while (legal) { //iterates through moves until no longer legal
            distance++;//increments the distance from the bishop

            int rowNext = row + distance * rowIncrease;
            int colNext = column + distance * colIncrease;
            if (rowNext >= 9 || colNext >= 9 || rowNext <= 0 || colNext <= 0) { //makes sure the move isn't out of bounds
                legal = false;
            } else {
                ChessPosition temp = new ChessPosition(rowNext, colNext); //selects the next spot northeast of bishop
                ChessMove move = new ChessMove(myPosition, temp, null); //sets up the move
                ChessPiece test = board.getPiece(temp); //gets the piece on the square targeted
                if (test == null) { //checks that there's no piece in the way
                    legalMoves.add(move);
                }
                else if (test.getTeamColor() != piece.getTeamColor()) {
                    legalMoves.add(move); //lets you capture the piece
                    legal = false; //further moves are illegal
                } else {
                    legal = false;
                }
            }
        }
        return legalMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    public String pieceToString() {
        if (type == PieceType.PAWN)
            return "PAWN";
        else if (type == PieceType.KNIGHT)
            return "KNIGHT";
        else if (type == PieceType.BISHOP)
            return "BISHOP";
        else if (type == PieceType.ROOK)
            return "ROOK";
        else if (type == PieceType.QUEEN)
            return "QUEEN";
        else if (type == PieceType.KING)
            return "KING";
        else
            return "NULL";
    }
}