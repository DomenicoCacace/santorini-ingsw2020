package it.polimi.ingsw.view;

public class Constants {
    public static final String NO = "n";
    public static final String YES = "y";
    public static final String CREATE_LOBBY = "1";
    public static final String JOIN_LOBBY = "2";
    public static final String QUIT = "quit";
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
