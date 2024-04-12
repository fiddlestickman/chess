package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static ui.EscapeSequences.*;


public class ChessboardUI {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static String[] whitecolumns = {EMPTY,  " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};
    private static String[] whiterows = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
    private static String[] blackcolumns = {EMPTY,  " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY};
    private static String[] blackrows = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};


    public static void PrintWhite() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        drawHeaders(out, true);
        drawChessboard(out, true, board, null);
        drawHeaders(out, true);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void PrintBlack() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        drawHeaders(out, false);
        drawChessboard(out, false, board, null);
        drawHeaders(out, false);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void PrintWhiteHighlight(Collection<ChessMove> moves) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ArrayList<ChessPosition> positions = new ArrayList<>();
        Iterator<ChessMove> iter = moves.iterator();
        while(iter.hasNext()) {
            ChessMove next = iter.next();
            positions.add(next.getEndPosition());
        }
        out.print(ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        drawHeaders(out, true);
        drawChessboard(out, true, board, positions);
        drawHeaders(out, true);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void PrintBlackHighlight(Collection<ChessMove> moves) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ArrayList<ChessPosition> positions = new ArrayList<>();
        Iterator<ChessMove> iter = moves.iterator();
        while(iter.hasNext()) {
            ChessMove next = iter.next();
            positions.add(next.getEndPosition());
        }
        out.print(ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        drawHeaders(out, false);
        drawChessboard(out, false, board, positions);
        drawHeaders(out, false);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, boolean white) {
        setBlack(out);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES + 2; ++boardCol) {
            int prefixLength = SQUARE_SIZE_IN_CHARS / 2;

            out.print(EMPTY.repeat(prefixLength));
            if (white) {
                printHeaderText(out, whitecolumns[boardCol]);
            } else {
                printHeaderText(out, blackcolumns[boardCol]);
            }
        }
        out.println();
    }


    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(player);
        setBlack(out);
    }

    private static void drawChessboard(PrintStream out, boolean white, ChessBoard board, ArrayList<ChessPosition> positions) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);
            if (white) {
                out.print(whiterows[boardRow]);
            } else {
                out.print(blackrows[boardRow]);
            }
            setBlack(out);

            drawRowOfSquares(out, boardRow, white, board, positions);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);
            if (white) {
                out.print(whiterows[boardRow]);
            } else {
                out.print(blackrows[boardRow]);
            }
            setBlack(out);

            out.println();
        }
    }

    private static void drawRowOfSquares(PrintStream out, int row, boolean white, ChessBoard board, ArrayList<ChessPosition> positions) {

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            int sum = boardCol + row;
            boolean color;
            if((sum & 1) == 0 ) {
                color = false;
            }
            else {
                color = true;
            }
            int prefixLength = SQUARE_SIZE_IN_CHARS / 2;

            out.print(EMPTY.repeat(prefixLength));
            String piece;
            if (white) {
                piece = getPiece(out, board, 8-row, 8-boardCol);
            } else {
                piece = getPiece(out, board, row+1, boardCol+1);
            }

            boolean highlight = false;
            if (positions != null && positions.contains(new ChessPosition(row, boardCol)))
                highlight = true;

            printPlayer(out, piece, color, highlight);
            setBlack(out);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player, boolean bgcolor, boolean highlight) {
        if (bgcolor) {
            out.print(SET_BG_COLOR_DARK_GREY);
        } else {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } if (highlight) {
            out.print(SET_BG_COLOR_YELLOW);
        }
        out.print(player);
        setWhite(out);
    }

    private static String getPiece(PrintStream out, ChessBoard board, int row, int col) {
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(pos);
        String output = null;
        if (piece == null) {
            return EMPTY;
        } if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
        }

        String piecestring = out.toString();

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return WHITE_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return WHITE_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return WHITE_KNIGHT;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return WHITE_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return WHITE_QUEEN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return WHITE_KING;
        }

        return EMPTY;
    }

    private class PieceAndColor {
        private boolean white;
        private String piece;
        public PieceAndColor (String piece, boolean white) {
            this.piece = piece;
            this.white = white;
        }

        public String getPiece() {
            return piece;
        }

        public Boolean getWhite() {
            return white;
        }
    }

}

