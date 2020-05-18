package it.polimi.ingsw.model;

/**
 * The possible actions a player can perform
 */
public enum PossibleActions {
    MOVE {
        public String toString() {
            return "Move";
        }
    },
    BUILD {
        public String toString() {
            return "Build";
        }
    },
    PASS_TURN {
        public String toString() {
            return "Pass turn";
        }
    },
    SELECT_OTHER_WORKER {
        public String toString() {
            return "Select other worker";
        }
    }
}
