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

    public ChessPosition(int row, int col)
    {
        if(0 >= row || row >= 9)
            throw new RuntimeException("Row out of bounds");
        if(0 >= col || col >= 9)
            throw new RuntimeException("Column out of bounds");

        this.row=row;
        this.column=col;
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
}
