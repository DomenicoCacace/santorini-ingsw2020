package it.polimi.ingsw.model;

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
    PASSTURN {
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
