package turingmachine;


public class Transition {
    public static enum DIRECTION {
        L, N, R
    }

    private DIRECTION direction;
    private State fromState;
    private State toState;
    private Character output;

    public Transition(State fromState, State toState, Character output, DIRECTION direction) {
        this.fromState = fromState;
        this.toState = toState;
        this.output = output;
        this.direction = direction;
    }

    public DIRECTION getDirection() {
        return direction;        
    }

    public State getFromState() {
        return fromState;
    }

    public State getToState() {
        return toState;
    }

    public Character getOutput() {
        return output;
    }
}
