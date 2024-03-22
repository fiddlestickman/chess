package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;


public class ChessboardUI {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();
    private static String[] whitecolumns = {EMPTY,  " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};
    private static String[] whiterows = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
    private static String[] blackcolumns = {EMPTY,  " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY};
    private static String[] blackrows = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);



        drawHeaders(out, false);
        drawTicTacToeBoard(out, false);
        drawHeaders(out, false);

        drawHeaders(out, true);
        drawTicTacToeBoard(out, true);
        drawHeaders(out, true);

        out.print(SET_BG_COLOR_BLACK);
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

    private static void drawTicTacToeBoard(PrintStream out, boolean white) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);
            if (white) {
                out.print(whiterows[boardRow]);
            } else {
                out.print(blackrows[boardRow]);
            }
            setBlack(out);

            drawRowOfSquares(out, boardRow);

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

    private static void drawRowOfSquares(PrintStream out, int row) {

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
            printPlayer(out, rand.nextBoolean() ? X : O, color);

            setBlack(out);
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void setLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
    private static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void printPlayer(PrintStream out, String player, boolean color) {
        if (color) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_WHITE);
        }
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(player);
        setWhite(out);
    }
}
