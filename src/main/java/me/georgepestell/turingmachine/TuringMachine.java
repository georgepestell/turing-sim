package turingmachine;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.regex.Pattern;

import tape.Tape;
import turingmachine.Transition.DIRECTION;

public class TuringMachine {

    private enum MODE {
        DEBUG,
        MAIN
    }

    private HashMap<String, State> states;
    private ArrayList<Character> alphabet;
    private Tape tape;

    private State currentState;
    private String acceptState;
    private String rejectState;

    private int stepCount;

    private MODE mode;

    public TuringMachine() {
        this.states = new HashMap<String, State>();
        this.alphabet = new ArrayList<Character>();
        this.tape = new Tape();

        this.currentState = null;
        this.acceptState = null;
        this.rejectState = null;

        this.stepCount = 0;
        this.mode = MODE.MAIN;
    }


    public void addState(String name) throws InvalidParameterException {
        if (name == null) {
            throw new InvalidParameterException("Null state"); 
        }

        if (states.containsKey(name)) {
            throw new InvalidParameterException("Duplicate state " + name);
        }

        State state = new State(name);

        if (currentState == null) {
            currentState = state;
        }

        states.put(name, state);
    }

    public void setAcceptState(String state) {
        if (!states.containsKey(state)) {
            throw new InvalidParameterException("Accept state not in TM");
        }
        acceptState = state;
    }
    public void setRejectState(String state) {
        if (!states.containsKey(state)) {
            throw new InvalidParameterException("Reject state not in TM");
        }
        rejectState = state;
    } 

    public void addToAlphabet(Character character) throws InvalidParameterException {   
        if (character == null) {
            throw new InvalidParameterException("Null character");
        }

        if (character.equals('_')) {
            throw new InvalidParameterException("Cannot add \'_\' to alphabet");
        }

        if (alphabet.contains(character)) {
            throw new InvalidParameterException("Alphabet already contains character");
        }

        alphabet.add(character);
    }

    public void printTransitions() {
        System.out.println(" States:");
        for (State state : states.values()) {
            System.out.printf("  \'%s\':\n", state.getName());
            HashMap<Character, Transition> transitions = state.getTransitions();
            for (Character key : transitions.keySet()) {
                System.out.printf("   \'%s\' \'%s\' \'%s\' %s\n", 
                    key,    
                    transitions.get(key).getToState().getName(),
                    transitions.get(key).getOutput(),    
                    transitions.get(key).getDirection().name()
                );
            }
        }
    }

    public void printAlphabet() {
        System.out.printf(" Alphabet: ");
        for (Character character : alphabet) {
            System.out.printf("\'%s\' ", character);
        }
        System.out.println();
    }

    public void printStatus() {
        printTransitions();
        printAlphabet();
    }

    public void addTransition(String from, Character input, String to, Character output, DIRECTION direction) throws InvalidParameterException {     
        if (!(alphabet.contains(input) || input.equals('_'))) {
            throw new InvalidParameterException("Input \'" + input + "\' not in alphabet");
        }

        if (!(alphabet.contains(output) || output.equals('_'))) {
            throw new InvalidParameterException("Output \'" + output + "\' not in alphabet");
        }

        if (!states.containsKey(from)) {
            throw new InvalidParameterException("From state not in TM");
        }

        if (!states.containsKey(to)) {
            throw new InvalidParameterException("To state not in TM");
        }


        State fromState = getState(from);
        State toState = getState(to);
        
        fromState.addTransition(input, toState, output, direction);

        // By convention, the first state mentioned is the start state
        if (currentState == null) {
            currentState = fromState;
        }
    }

    public State getState(String name) throws InvalidParameterException {
        return states.get(name);
    }

    public void addTape(String content) throws InputMismatchException {
        content = content.replaceAll("\\s", "").trim();

        if (content.isBlank()) {
            return;
        }

        StringBuilder regex = new StringBuilder("[");
        for (char c : alphabet) {
            regex.append(c);
        }
        regex.append("_]+");

        if (Pattern.matches(regex.toString(), content)) {
            for (Character c : content.toCharArray()) {
                tape.append(c);
            }
        } else {
            throw new InputMismatchException("Invalid tape contents");
        }
    } 

    public void run() {
        if (mode == MODE.DEBUG) {
            printStatus();
        }
        while (true) {
            if (mode == MODE.DEBUG) {
                System.out.println(currentState.getName());
                System.out.println(tape.read());
            }
            doStep();
        }
    }

    void printState() {
        System.out.printf(" state: %s\n", currentState.getName());
    }

    public void doStep() {
        // System.out.printf("offset: %s\n", tape.offset);
        Character input = tape.read();

        // Find transition
        Transition transition = currentState.getTransition(input);
        doTransition(transition);
        checkState();

        this.stepCount++;
    }

    public void end() {
        tape.move(DIRECTION.L);
        reject();
    }

    public void doTransition(Transition transition) {
        if (transition == null) {
            end();
        }

        tape.write(transition.getOutput());
        currentState = transition.getToState();
        tape.move(transition.getDirection());

    }

    private boolean checkState() {
        if (this.currentState.getName().equals(this.acceptState)) {
            this.accept();
            return true;
        } else if (this.currentState.getName().equals(this.rejectState)) {
            this.reject();
            return true;
        }

        return false;
    }

    private void accept() {
        System.out.printf("accepted \n%d\n", stepCount);
        tape.printTape();
        System.exit(0);
    }

    private void reject() {

        System.out.printf("not accepted \n%d\n", stepCount);
        tape.printTape();        
        System.exit(1);

    }

    public void setDebug() {
        this.mode = MODE.DEBUG;
    }

}