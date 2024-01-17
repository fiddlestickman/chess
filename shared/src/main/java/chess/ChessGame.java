package chess;

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
        //create a board
        //set up the board
        //set start player to white
        //put in a loop of asking turns from the current player until stalemate or checkmate happens
        //end the game
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
        BLACK
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
        throw new RuntimeException("Not implemented");
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
        return board.squareAttacked(attacker, kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        if (isInCheck(teamColor))
        {

        }
        else
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

    }
}
