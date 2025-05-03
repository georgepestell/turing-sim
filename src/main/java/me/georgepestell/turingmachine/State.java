package turingmachine;

import java.security.InvalidParameterException;
import java.util.HashMap;

import turingmachine.Transition.DIRECTION;

public class State {
    private String name;
    private HashMap<Character, Transition> transitions;

    public State(String name) throws InvalidParameterException {
        if (name == null) {
            throw new InvalidParameterException();
        }
        
        this.name = name;
        this.transitions = new HashMap<Character, Transition>();
    }

    public Transition getTransition(Character input) {
        return this.transitions.get(input);
    }

    public String getName() {
        return this.name;
    }

    public HashMap<Character, Transition> getTransitions() {
        return transitions;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof State) {
            State otherState = (State) object;
            if (otherState.getName().equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    public void addTransition(Character input, State toState, Character output, DIRECTION direction) throws InvalidParameterException {
        if (transitions.containsKey(input)) {
            throw new InvalidParameterException("Transition for input (" + input + ") already exists (" + this.name + ")");
        }

        Transition transition = new Transition(this, toState, output, direction);
        transitions.put(input, transition);
    }
}
