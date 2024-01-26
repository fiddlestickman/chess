package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentPlayer;
    ChessBoard board;

    public ChessGame() {
        //make the board
        board = new ChessBoard();

        //set up the board
        board.resetBoard();

        //set start player to white
        currentPlayer = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn()
    {
        return currentPlayer;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team)
    {
        currentPlayer = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        NOTHING
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get the starting piece
        ChessPiece mypiece = board.getPiece(move.getStartPosition());

        //if no starting piece or piece is the wrong color, it's an invalid move
        if (mypiece.getTeamColor() != currentPlayer)
            throw new InvalidMoveException();

        //make sure that the move being made can be made by that piece
        java.util.Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (!moves.contains(move))
            throw new InvalidMoveException();

        //copy the current board
        ChessBoard temp = board;

        //make the move
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), mypiece);

        //if the move causes the player to be in check, reverse the move (it's invalid)
        if (this.isInCheck(currentPlayer))
        {
            board = temp;
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        //finds the friendly king
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) // rows (white to black)
        {
            for (int j = 1; j <= 8; j++) //columns (queenside to kingside)
            {
                //if the chess piece on the square is the friendly king
                if (Objects.equals(board.getPiece(new ChessPosition(i, j)), new ChessPiece(teamColor, ChessPiece.PieceType.KING)))
                    kingPosition = new ChessPosition(i, j);
            }
        }
        if (kingPosition == null)
            throw new RuntimeException("there's no king here, what?");

        TeamColor attacker = null;
        if (teamColor == TeamColor.WHITE)
            attacker = TeamColor.BLACK;
        else if (teamColor == TeamColor.BLACK)
            attacker = TeamColor.WHITE;

        if (attacker == null)
            throw new RuntimeException("somehow the team color is neither white nor black");

        //returns whether the king's square is under attack
        if (Objects.equals(board.squareAttacked(attacker, kingPosition), ""))
            return true;
        else
            return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        //finds the friendly king
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) // rows (white to black)
        {
            for (int j = 1; j <= 8; j++) //columns (queenside to kingside)
            {
                //if the chess piece on the square is the friendly king
                if (Objects.equals(board.getPiece(new ChessPosition(i, j)), new ChessPiece(teamColor, ChessPiece.PieceType.KING)))
                    kingPosition = new ChessPosition(i, j);
            }
        }
        if (kingPosition == null)
            throw new RuntimeException("there's no king here, what?");

        TeamColor attacker = null;
        if (teamColor == TeamColor.WHITE)
            attacker = TeamColor.BLACK;
        else if (teamColor == TeamColor.BLACK)
            attacker = TeamColor.WHITE;

        if (attacker == null)
            throw new RuntimeException("somehow the team color is neither white nor black");

        String attackers = board.squareAttacked(attacker, kingPosition);

        //returns whether the king's square is under attack
        if (Objects.equals(attackers, ""))
            return false; //king is not under attack, not checkmate
        else
        {
            //king is in check, may be checkmate
            if (kingPosition.getPiece().pieceMoves(board, kingPosition) == null)
            {
                //king has no moves
                if (board.squareAttacked(attacker, kingPosition).length() > 2)
                    return true; //double attack + no king moves = checkmate
                else
                {
                    //check if there's any non-king piece that can stop checkmate
                    int row = attackers.charAt(0);
                    int col = attackers.charAt(1);//the row and column the attacking piece rests on

                    //make code to see if piece is pinned
                    //if piece is pinned, skip looking at their moves
                    //check if you have any pieces that can capture the enemy piece first


                    int row_diff = row-kingPosition.getRow();
                    int col_diff = col-kingPosition.getColumn();//determines the relative position of king and attacker

                    if(row_diff == 0) //rook or queen on same row
                    {

                    }
                    else if(col_diff == 0) //rook or queen on same column
                    {

                    }

                    else if (row_diff - col_diff == 0) //bishop or queen on up and right diagonal
                    {

                    }
                    else if (row_diff + col_diff == 0) //bishop or queen on down and right diagonal
                    {

                    }
                }
            }
            else
                return false; //king can move, not checkmate
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean wayOutOfCheckmate(TeamColor teamColor)
    {
        //finds the friendly king
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) // rows (white to black)
        {
            for (int j = 1; j <= 8; j++) //columns (queenside to kingside)
            {
                //if the chess piece on the square is the friendly king
                if (Objects.equals(board.getPiece(new ChessPosition(i, j)), new ChessPiece(teamColor, ChessPiece.PieceType.KING)))
                    kingPosition = new ChessPosition(i, j);
            }
        }
        if (kingPosition == null)
            throw new RuntimeException("there's no king here, what?");

        //checks if the king has any legal moves - if it does, not checkmate
        if (board.getPiece(kingPosition).pieceMoves(board, kingPosition).isEmpty())
        {

        }
        else
            return false;

        return false;
    }
}
