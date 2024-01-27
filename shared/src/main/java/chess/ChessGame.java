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
    ChessMove lastMove;
    ChessPiece lastMoveCaptured;

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
        //finds the piece on the board being tested
        ChessPiece piece = board.getPiece(startPosition);
        //get the possible moves that piece can make and set up possible legal moves
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();

        java.util.Iterator<ChessMove> pieceiterator = pieceMoves.iterator();
        //check their moves to see if they are free to move to whichever square
        while (pieceiterator.hasNext())
        {
            ChessMove next = pieceiterator.next();

            //make the move
            ChessPiece slainpiece = board.getPiece(next.getEndPosition());
            board.removePiece(next.getStartPosition());
            board.addPiece(next.getEndPosition(), piece);

            //if the king is not in check, it's a legal move
            if (!isInCheck(piece.getTeamColor()))
                legalMoves.add(next);

            //reverse the move
            board.removePiece(next.getEndPosition());
            board.addPiece(next.getStartPosition(), piece);
            board.addPiece(next.getEndPosition(), slainpiece);
        }
        return legalMoves;
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
        if (mypiece == null)
            throw new InvalidMoveException();

        if (mypiece.getTeamColor() != currentPlayer)
            throw new InvalidMoveException();


        //make sure that the move being made can be made by that piece
        java.util.Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (!moves.contains(move))
            throw new InvalidMoveException();
        //copy the piece potentially captured
        ChessPiece slainpiece = board.getPiece(move.getEndPosition());

        //make the move
        board.removePiece(move.getStartPosition());
        //pawn promotion move
        if (move.getPromotionPiece() != null) {
            ChessPiece promo = new ChessPiece(mypiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promo);
        }
        //normal move
        else
            board.addPiece(move.getEndPosition(), mypiece);

        //if the move causes the player to be in check, reverse the move (it's invalid)
        if (this.isInCheck(currentPlayer))
        {
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), mypiece);
            board.addPiece(move.getEndPosition(), slainpiece);
            throw new InvalidMoveException("is still in check");
        }


        //switch teams
        if (currentPlayer == TeamColor.WHITE)
            currentPlayer = TeamColor.BLACK;
        else if (currentPlayer == TeamColor.BLACK)
            currentPlayer = TeamColor.WHITE;

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
        //only works if there's one king
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
            return false;

        TeamColor attacker = null;
        if (teamColor == TeamColor.WHITE)
            attacker = TeamColor.BLACK;
        else if (teamColor == TeamColor.BLACK)
            attacker = TeamColor.WHITE;

        if (attacker == null)
            throw new RuntimeException("somehow the team color is neither white nor black");

        //returns whether the king's square is under attack
        if (board.squareAttacked(attacker, kingPosition).isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        //can't be checkmate if there's no check
        if (!this.isInCheck(teamColor))
            return false;
        else return noLegalMoves(teamColor);
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
        //can't be stalemate if king is in check
        if (this.isInCheck(teamColor))
            return false;
        else return noLegalMoves(teamColor);
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

    private boolean noLegalMoves(TeamColor teamColor)
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

        //there is a king and it's under attack, may be checkmate

        //check if the king can move
        ChessPiece temp = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        Collection<ChessMove> kingMoves = temp.pieceMoves(board, kingPosition);
        java.util.Iterator<ChessMove> kingiterator = kingMoves.iterator();
        boolean escapesquare = false;
        while (kingiterator.hasNext())
        {
            ChessMove next = kingiterator.next();

            //make the move
            ChessPiece mypiece = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
            ChessPiece slainpiece = board.getPiece(next.getEndPosition());
            board.removePiece(next.getStartPosition());
            board.addPiece(next.getEndPosition(), mypiece);

            //if it's not check anymore, it's an escape square
            if (!isInCheck(teamColor))
                escapesquare = true;

            //reverse the move
            board.removePiece(next.getEndPosition());
            board.addPiece(next.getStartPosition(), mypiece);
            board.addPiece(next.getEndPosition(), slainpiece);
        }
        //there's a way out, not checkmate
        if (escapesquare)
            return false;

        //there's no move for the white king, check if there's any other piece that can move
        for (int i = 1; i <=8; i++)
        {
            for (int j = 1; j <=8; j++)
            {
                ChessPiece test = board.getPiece(new ChessPosition(i,j));
                //look for pieces that are white but aren't the king
                if (test != null && test.getTeamColor() == teamColor && test.getPieceType() != ChessPiece.PieceType.KING)
                {
                    Collection<ChessMove> pieceMoves = test.pieceMoves(board, new ChessPosition(i,j));
                    java.util.Iterator<ChessMove> pieceiterator = pieceMoves.iterator();
                    boolean blocksquare = false;
                    //check their moves to see if they can block checkmate
                    while (pieceiterator.hasNext())
                    {
                        ChessMove next = pieceiterator.next();

                        //make the move
                        ChessPiece slainpiece = board.getPiece(next.getEndPosition());
                        board.removePiece(next.getStartPosition());
                        board.addPiece(next.getEndPosition(), test);

                        //if it's not check anymore, there's a square that can block
                        if (!isInCheck(teamColor))
                            blocksquare = true;

                        //reverse the move
                        board.removePiece(next.getEndPosition());
                        board.addPiece(next.getStartPosition(), test);
                        board.addPiece(next.getEndPosition(), slainpiece);
                    }
                    if (blocksquare)
                        return false;
                }
            }
        }
        //there's no escape move for the king, and no piece can legally move
        //it's checkmate or stalemate
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return currentPlayer == chessGame.currentPlayer && Objects.equals(board, chessGame.board) && Objects.equals(lastMove, chessGame.lastMove) && Objects.equals(lastMoveCaptured, chessGame.lastMoveCaptured);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPlayer, board, lastMove, lastMoveCaptured);
    }
}
