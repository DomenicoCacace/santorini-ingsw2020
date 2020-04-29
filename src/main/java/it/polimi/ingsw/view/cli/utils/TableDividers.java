package it.polimi.ingsw.view.cli.utils;

/**
 * Unicode characters to draw tables
 * <p>
 *     To make square cells, for each vertical line, 4 horizontal lines are needed
 * </p>
 */
public enum TableDividers {
        TOP_LEFT_CORNER('\u2554'),
        TOP_RIGHT_CORNER('\u2557'),
        BOTTOM_LEFT_CORNER('\u255A'),
        BOTTOM_RIGHT_CORNER('\u255D'),
        HORIZONTAL_LINE('\u2550'),
        VERTICAL_LINE('\u2551'),
        VERTICAL_T_RIGHT('\u2560'),
        VERTICAL_T_LEFT('\u2563'),
        HORIZONTAL_T_DOWN('\u2566'),
        HORIZONTAL_T_UP('\u2569'),
        CROSS('\u256C');
        /*
         * Example: 2x2 grid
         *
         * ╔════╦════╗
         * ║    ║    ║
         * ╠════╬════╣
         * ║    ║    ║
         * ╚════╩════╝
         *
         */

        private final char code;

        TableDividers(char code) {
            this.code = code;
        }


        public String toString() {
            return String.valueOf(code);
        }

        public char getAsChar() {
            return code;
        }

}
