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
    boolean whiteqsc;
    boolean whiteksc;
    boolean blackqsc;
    boolean blackksc;

    public ChessGame() {
        //make the board
        board = new ChessBoard();

        //set up the board
        board.resetBoard();

        //set start player to white
        currentPlayer = TeamColor.WHITE;

        //lets the players castle from the start
        whiteqsc = true;
        whiteksc = true;
        blackqsc = true;
        blackksc = true;
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

        legalMoves.addAll(castleMoves(startPosition));

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

        //castling move
        if (mypiece.getPieceType() == ChessPiece.PieceType.KING)
        { //castle moves are the only king moves where the king moves two squares
            if (move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == 2
            || move.getStartPosition().getColumn() - move.getEndPosition().getColumn() == -2)
            {
                //it's a castling move
                ChessPiece rook = new ChessPiece(mypiece.getTeamColor(), ChessPiece.PieceType.ROOK);
                if (move.getEndPosition().getRow() == 1) {
                    if (move.getEndPosition().getColumn() == 7){
                        board.removePiece(new ChessPosition(1,8));
                        board.addPiece(new ChessPosition(1,6), rook); //rook needs to move too
                    }
                    else if (move.getEndPosition().getColumn() == 3){
                        board.removePiece(new ChessPosition(1,1));
                        board.addPiece(new ChessPosition(1,4), rook); //rook needs to move too
                    }
                }
                else if (move.getEndPosition().getRow() == 8) {
                    if (move.getEndPosition().getColumn() == 7){
                        board.removePiece(new ChessPosition(8,8));
                        board.addPiece(new ChessPosition(8,6), rook); //rook needs to move too
                    }
                    else if (move.getEndPosition().getColumn() == 3){
                        board.removePiece(new ChessPosition(8,1));
                        board.addPiece(new ChessPosition(8,4), rook); //rook needs to move too
                    }
                }
            }
        }

        //if the move causes the player to be in check, reverse the move (it's invalid)
        if (this.isInCheck(currentPlayer))
        {
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), mypiece);
            board.addPiece(move.getEndPosition(), slainpiece);
            throw new InvalidMoveException("is still in check");
        }

        //if the piece moved could have been the king or rooks, it removes the possibility of certain kinds of castling
        if(move.getStartPosition().getRow() == 1) {
            if (move.getStartPosition().getColumn() == 1)
                whiteqsc = false;
            else if (move.getStartPosition().getColumn() == 8)
                whiteksc = false;
            else if (move.getStartPosition().getColumn() == 5) {
                whiteqsc = false;
                whiteksc = false;
            }
        }
        else if(move.getStartPosition().getRow() == 8) {
            if (move.getStartPosition().getColumn() == 1)
                blackqsc = false;
            else if (move.getStartPosition().getColumn() == 8)
                blackksc = false;
            else if (move.getStartPosition().getColumn() == 5) {
                blackqsc = false;
                blackksc = false;
            }
        }

        //if the move could have captured a rook, then the king can't castle that way
        if (move.getEndPosition().getRow() == 1) {
            if (move.getEndPosition().getColumn() == 1)
                whiteqsc = false;
            else if (move.getEndPosition().getColumn() == 8)
                whiteksc = false;
        }
        else if(move.getStartPosition().getRow() == 8) {
            if (move.getEndPosition().getColumn() == 1)
                blackqsc = false;
            else if (move.getEndPosition().getColumn() == 8)
                blackksc = false;
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
        whiteqsc = true;
        whiteksc = true;
        blackqsc = true;
        blackksc = true;

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

    //returns a collection of legal castling moves
    public Collection<ChessMove> castleMoves(ChessPosition startPosition)
    {
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(startPosition);
        if (!board.squareAttacked(piece.getTeamColor(), startPosition).isEmpty()) {//if the king is in check
            return legalMoves; //cannot castle while in check
        }


        // castling rules
        //white castling
        if(startPosition.getRow() == 1 && startPosition.getColumn() == 5
                && piece.getTeamColor() == TeamColor.WHITE
                && piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (whiteksc) { //make sure castling kingside is legal (king/rook hasn't moved)
                ChessPosition bishop = new ChessPosition (1,6);
                ChessPosition knight = new ChessPosition (1,7);
                if (board.getPiece(bishop) == null && board.getPiece(knight) == null) //first make sure no pieces are in the way and there's a rook still
                    if (board.squareAttacked(TeamColor.BLACK, bishop).isEmpty()
                    && board.squareAttacked(TeamColor.BLACK, knight).isEmpty()) //then make sure the king isn't attacked on his way
                        {
                            ChessMove move = new ChessMove(startPosition, knight, null);
                            legalMoves.add(move); //if there are no problems, the king can castle kingside
                        }
            }
            if (whiteqsc){
                ChessPosition queen = new ChessPosition (1,4);
                ChessPosition bishop = new ChessPosition (1,3);
                ChessPosition knight = new ChessPosition (1,2);
                if (board.getPiece(queen) == null && board.getPiece(bishop) == null && board.getPiece(knight) == null) //first make sure no pieces are in the way
                    if (board.squareAttacked(TeamColor.BLACK, queen).isEmpty()
                    && board.squareAttacked(TeamColor.BLACK, bishop).isEmpty()) //then make sure the king isn't attacked on his way
                    {
                        ChessMove move = new ChessMove(startPosition, bishop, null);
                        legalMoves.add(move); //if there are no problems, the king can castle queenside
                    }
            }
        }
        //black castling
        else if(startPosition.getRow() == 8 && startPosition.getColumn() == 5
                && piece.getTeamColor() == TeamColor.BLACK
                && piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (blackksc) { //make sure castling kingside is legal (king/rook hasn't moved)
                ChessPosition bishop = new ChessPosition (8,6);
                ChessPosition knight = new ChessPosition (8,7);
                if (board.getPiece(bishop) == null && board.getPiece(knight) == null) //first make sure no pieces are in the way
                    if (board.squareAttacked(TeamColor.WHITE, bishop).isEmpty()
                            && board.squareAttacked(TeamColor.WHITE, knight).isEmpty()) //then make sure the king isn't attacked on his way
                    {
                        ChessMove move = new ChessMove(startPosition, knight, null);
                        legalMoves.add(move); //if there's no problems, the king can castle kingside
                    }
            }
            if (blackqsc){
                ChessPosition queen = new ChessPosition (8,4);
                ChessPosition bishop = new ChessPosition (8,3);
                ChessPosition knight = new ChessPosition (8,2);
                if (board.getPiece(queen) == null && board.getPiece(bishop) == null && board.getPiece(knight) == null) //first make sure no pieces are in the way
                    if (board.squareAttacked(TeamColor.WHITE, queen).isEmpty()
                            && board.squareAttacked(TeamColor.WHITE, bishop).isEmpty()) //then make sure the king isn't attacked on his way
                    {
                        ChessMove move = new ChessMove(startPosition, bishop, null);
                        legalMoves.add(move); //if there's no problems, the king can castle queenside
                    }
            }
        }
        return legalMoves; //contains all the castling moves available
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
