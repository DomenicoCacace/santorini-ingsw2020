package it.polimi.ingsw.client.view;


public final class Constants {

    private Constants(){}

    // NON-PRINTABLE CHARACTERS
    public static final int END_OF_TEXT = 0x03;
    public static final int BACKSPACE_0 = 0x08;
    public static final int BACKSPACE_1 = 0x7F;
    public static final int HORIZONTAL_TAB = 0x09;
    public static final int ESCAPE = 0x1B;
    public static final int CARRIAGE_RETURN = 0x0D;

    public static final int BRACKET = 0x5B;
    public static final int ARROW_UP = 0x41;
    public static final int ARROW_DOWN = 0x42;
    public static final int ARROW_RIGHT = 0x43;
    public static final int ARROW_LEFT = 0x44;

    public static final int EXIT = ESCAPE;


    // COLORS
    public static final String ANSI_RESET = "\033[0m";


    // DEFAULT INPUT STRINGS
    public static final String YES = "y";
    public static final String NO = "n";
    public static final String QUIT = "quit";
    public static final String CREATE_LOBBY = "1";
    public static final String JOIN_LOBBY = "2";


    // TABLE DRAWING UTILS
    public static final String LINE_VERTICAL = "┃";
    public static final String LINE_TOP_LEFT = "┏";
    public static final String LINE_TOP_RIGHT = "┓";
    public static final String LINE_BOTTOM_LEFT = "┗";
    public static final String LINE_BOTTOM_RIGHT = "┛";
    public static final String LINE_HORIZONTAL = "━";
    public static final String LINE_T_LEFT = "┣";
    public static final String LINE_T_RIGHT = "┫";
    public static final String LINE_SHADOW_RIGHT = "\u258c";
    public static final String LINE_SHADOW_BOTTOM = "\u2580";
    public static final String LINE_SHADOW_CORNER = "\u2598";

    // SPECIAL ANSI SEQUENCES
    public static final String CURSOR_BACK = "\033[1D";

    // MISC
    public static final String STOP_BLINK = "\033[25m";
    public static final String BLINKER = "\033[5m" + '\u2588' + CURSOR_BACK + STOP_BLINK;

    public static final int INPUT_TIMER = 60;

    public static final String GAME_RULES =
            "-Before the game starts, every player must place the workers on the Game Board.\n" +
                    "-On your turn, select one of your Workers. You must move and then build with the selected Worker.\n" +
                    "-Move your selected Worker into one of the (up to) eight neighboring spaces.\n" +
                    "-A Worker may move up a maximum of one level higher, move down any number of levels lower, or move along the same level.\n" +
                    "-A Worker may not move up more than one level.\n" +
                    "-The space your Worker moves into must be unoccupied (not containing a Worker or Dome).\n" +
                    "-Build a block or dome on an unoccupied space neighboring the moved Worker.\n" +
                    "-You can build a dome only upon a level 3 block.\n" +
                    "-If one of your Workers moves up on top of level 3 during your turn, you instantly win.\n" +
                    "-You must always perform a move then build on your turn. If you are unable to, you lose.\n\n" +
                    "Remember: to win the game by moving onto the third level, your Worker must move up during your turn.\n" +
                    "Therefore, if your Worker is Forced onto the third level, you do not win the game.\n" +
                    "Moving from one third level space to another also does not trigger a win.\n";

}