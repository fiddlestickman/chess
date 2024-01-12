package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPosition[][] chessBoard = new ChessPosition[8][8]; //creates the chess board, separated into 8 rows and columns

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

}
