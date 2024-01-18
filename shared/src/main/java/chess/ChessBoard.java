package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPosition[][] chessBoard = new ChessPosition[9][9]; //creates the chess board, separated into 8 rows and columns (with a ghostly zeroeth rank and file)
    ChessMove lastMove = null;

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
    public ChessMove getLastMove()
    {
        return lastMove;
    }

    //checks if a square is under attack (useful for legal king moves)
    public String squareAttacked (ChessGame.TeamColor attacker, ChessPosition attackedSquare)
    {

        int row = attackedSquare.getRow();
        int col = attackedSquare.getColumn();
        StringBuilder attackerSquare = new StringBuilder();
        ChessPiece piece = chessBoard[row][col].getPiece();

        if (attacker == ChessGame.TeamColor.WHITE)
        {
            //checks if there's a white pawn attacking the square
            ChessPiece pieceTested = new ChessPiece(attacker, chess.ChessPiece.PieceType.PAWN);
            if (row > 0 && col > 0) { //makes sure the pawn checked for is within bounds
                if (Objects.equals(chessBoard[row - 1][col - 1].getPiece(), pieceTested)) {
                    attackerSquare.append(row-1);
                    attackerSquare.append(col-1);
                }
            }
            if (row > 0 && col < 7) { // makes sure the pawn checked for is within bounds
                if (Objects.equals(chessBoard[row - 1][col + 1].getPiece(), pieceTested)) {

                    attackerSquare.append(row-1);
                    attackerSquare.append(col+1);
                }
            }
        }

        else if (attacker == ChessGame.TeamColor.BLACK)
        {
            //checks if there's a black pawn attacking the square
            ChessPiece pieceTested = new ChessPiece(attacker, chess.ChessPiece.PieceType.PAWN);
            if (row < 8 && col > 1) { //makes sure the pawn checked for is within bounds
                if (Objects.equals(chessBoard[row + 1][col - 1].getPiece(), pieceTested)) {
                    attackerSquare.append(row+1);
                    attackerSquare.append(col-1);
                }
            }
            if (row < 8 && col < 8) { // makes sure the pawn checked for is within bounds
                if (Objects.equals(chessBoard[row + 1][col + 1].getPiece(), pieceTested)) {
                    attackerSquare.append(row+1);
                    attackerSquare.append(col+1);
                }
            }
        }
        attackerSquare.append(dAttacker(attacker, attackedSquare)); //checks if there's a bishop or queen attacking from a diagonal
        attackerSquare.append(cAttacker(attacker, attackedSquare)); //checks if there's a rook or queen attacking from a rank or file

        //checks if there's a knight that can attack from any l-shape spot
        attackerSquare.append(kAttacker(attacker, row+2, col+1));
        attackerSquare.append(kAttacker(attacker, row+1, col+2));
        attackerSquare.append(kAttacker(attacker, row-1, col+2));
        attackerSquare.append(kAttacker(attacker, row-2, col+1));
        attackerSquare.append(kAttacker(attacker, row-2, col-1));
        attackerSquare.append(kAttacker(attacker, row-1, col-2));
        attackerSquare.append(kAttacker(attacker, row+1, col-2));
        attackerSquare.append(kAttacker(attacker, row+2, col-1));

        attackerSquare.append(kingAttacker(attacker, attackedSquare));

        return attackerSquare.toString();

    }

    private String dAttacker (ChessGame.TeamColor attacker, ChessPosition attackedSquare)
    {
        int row = attackedSquare.getRow();
        int col = attackedSquare.getColumn();
        StringBuilder attackerSquare = new StringBuilder();

        //checks if there's a bishop or queen attacking the square
        chess.ChessPiece pieceTested = new ChessPiece(attacker, chess.ChessPiece.PieceType.BISHOP);
        chess.ChessPiece pieceTested2 = new ChessPiece(attacker, chess.ChessPiece.PieceType.QUEEN);
        boolean legal = true;
        int distance = 0; //distance diagonally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row+distance >= 9 || col+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row+distance][col+distance].getPiece(); //gets the piece on the square targeted
                //if the piece on the square is an enemy bishop or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row+distance);
                    attackerSquare.append(col+distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }

        legal = true;
        distance = 0; //distance diagonally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row-distance <= 0 || col+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row-distance][col+distance].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy bishop or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row-distance);
                    attackerSquare.append(col+distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }

        legal = true;
        distance = 0; //distance diagonally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row-distance <= 0 || col-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row-distance][col-distance].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy bishop or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row-distance);
                    attackerSquare.append(col-distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }

        legal = true;
        distance = 0; //distance diagonally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row+distance >= 9 || col-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row+distance][col-distance].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy bishop or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row+distance);
                    attackerSquare.append(col-distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }
        return attackerSquare.toString();
    }

    private String cAttacker (ChessGame.TeamColor attacker, ChessPosition attackedSquare) {
        int row = attackedSquare.getRow();
        int col = attackedSquare.getColumn();
        StringBuilder attackerSquare = new StringBuilder();

        //checks if there's a rook or queen attacking the square
        chess.ChessPiece pieceTested = new ChessPiece(attacker, chess.ChessPiece.PieceType.ROOK);
        chess.ChessPiece pieceTested2 = new ChessPiece(attacker, chess.ChessPiece.PieceType.QUEEN);
        boolean legal = true;
        int distance = 0; //distance cardinally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row + distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            } else {
                ChessPiece test = chessBoard[row + distance][col].getPiece(); //gets the piece on the square targeted
                //if the piece on the square is an enemy rook or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row+distance);
                    attackerSquare.append(col);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }

        legal = true;
        distance = 0; //distance cardinally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (col+distance >= 9) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row][col+distance].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy rook or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row);
                    attackerSquare.append(col+distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }

        legal = true;
        distance = 0; //distance cardinally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (row-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row-distance][col].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy rook or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row-distance);
                    attackerSquare.append(col);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }
        legal = true;
        distance = 0; //distance cardinally from the square
        while (legal) //iterates through squares away from checked square
        {
            distance++;//increments the distance from the square
            if (col-distance <= 0) //makes sure the move isn't out of bounds
            {
                legal = false;
            }
            else {
                ChessPiece test = chessBoard[row][col-distance].getPiece(); //gets the piece on the next square
                //if the piece on the square is an enemy rook or queen
                if (Objects.equals(test, pieceTested) || Objects.equals(test, pieceTested2)) {
                    attackerSquare.append(row);
                    attackerSquare.append(col-distance);
                    legal = false;
                } else if (test.getPieceType() != ChessPiece.PieceType.NOTHING)
                    legal = false; //the piece on that square can't attack the square, and blocks more distant attackers
            }
        }
        return attackerSquare.toString();
    }

    private String kAttacker (ChessGame.TeamColor attacker, int finalRow, int finalCol) //works for knights
    {
        if (finalRow < 9 && finalCol < 9 && finalRow > 0 && finalCol > 0) //makes sure the move is not out of bounds
        {
            ChessPiece test = chessBoard[finalRow][finalCol].getPiece(); //gets the piece on the square targeted
            if (test.getPieceType() == ChessPiece.PieceType.KNIGHT && test.getTeamColor() == attacker)
            {
                //checks if there's a knight on the attacking square
                return String.valueOf(finalRow) + finalCol;

            }
            else
                return "";
        }
        else
            return ""; //no knight can attack from out of bounds
    }

    private String kingAttacker (ChessGame.TeamColor attacker, ChessPosition attackedSquare) //works for kings
    {
        int row = attackedSquare.getRow();
        int col = attackedSquare.getColumn();
        chess.ChessPiece testPiece = new chess.ChessPiece(attacker, ChessPiece.PieceType.KING);

        StringBuilder attackerSquare = new StringBuilder();

        boolean northFine = true;
        boolean eastFine = true;
        boolean southFine = true;
        boolean westFine = true;
        if (attackedSquare.getRow() == 8) //checks if the square attacked is at the top of the board or not
            northFine = false;
        if (attackedSquare.getColumn() == 8) //checks if the square attacked is on the right side of the board or not
            eastFine = false;
        if (attackedSquare.getRow() == 1) //checks if the square attacked is at the bottom of the board or not
            southFine = false;
        if (attackedSquare.getColumn() == 1) //checks if the square attacked is on the left side of the board or not
            westFine = false;

        if (northFine && Objects.equals(chessBoard[row+1][col].getPiece(), testPiece))
        {
            //checks top square for king
            attackerSquare.append(row+1);
            attackerSquare.append(col);
            return attackerSquare.toString();
        }
        if (northFine && eastFine && Objects.equals(chessBoard[row+1][col+1].getPiece(), testPiece)) //checks top right for king
        {
            attackerSquare.append(row+1);
            attackerSquare.append(col+1);
            return attackerSquare.toString();
        }
        if (eastFine && Objects.equals(chessBoard[row][col+1].getPiece(), testPiece)) //checks right square for king
        {
            attackerSquare.append(row);
            attackerSquare.append(col+1);
            return attackerSquare.toString();
        }
        if (eastFine && southFine && Objects.equals(chessBoard[row-1][col+1].getPiece(), testPiece)) //checks bottom right for king
        {
            attackerSquare.append(row-1);
            attackerSquare.append(col+1);
            return attackerSquare.toString();
        }
        if (southFine && Objects.equals(chessBoard[row-1][col].getPiece(), testPiece)) //checks bottom square for king
        {
            attackerSquare.append(row-1);
            attackerSquare.append(col);
            return attackerSquare.toString();
        }
        if (southFine && westFine && Objects.equals(chessBoard[row-1][col-1].getPiece(), testPiece)) //checks bottom left for king
        {
            attackerSquare.append(row-1);
            attackerSquare.append(col-1);
            return attackerSquare.toString();
        }
        if (westFine && Objects.equals(chessBoard[row][col-1].getPiece(), testPiece)) //checks left square for king
        {
            attackerSquare.append(row);
            attackerSquare.append(col-1);
            return attackerSquare.toString();
        }
        if (westFine && northFine && Objects.equals(chessBoard[row+1][col-1].getPiece(), testPiece)) //checks top left for king
        {
            attackerSquare.append(row+1);
            attackerSquare.append(col-1);
            return attackerSquare.toString();
        }
        else
            return "";

    }

    public void setLastMove(ChessMove move)
    {
        lastMove = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessBoard, that.chessBoard) && Objects.equals(lastMove, that.lastMove);


    }



    @Override
    public int hashCode() {
        int result = Objects.hash(lastMove);
        result = 31 * result + Arrays.hashCode(chessBoard);
        return result;
    }
}
