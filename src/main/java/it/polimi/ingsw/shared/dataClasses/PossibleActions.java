package it.polimi.ingsw.shared.dataClasses;

/**
 * The possible actions a player can perform
 */
public enum PossibleActions {
    MOVE {
        @Override
        public String toString() {
            return "Move";
        }
    },
    BUILD {
        @Override
        public String toString() {
            return "Build";
        }
    },
    PASS_TURN {
        @Override
        public String toString() {
            return "Pass turn";
        }
    },
    SELECT_OTHER_WORKER {
        @Override
        public String toString() {
            return "Select other worker";
        }
    }
}
