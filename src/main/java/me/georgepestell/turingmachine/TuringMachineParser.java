package turingmachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.InvalidParameterException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import turingmachine.Transition.DIRECTION;

public class TuringMachineParser {

    public static TuringMachine parseTuringMachine(String filepath_description) throws FileNotFoundException, InvalidParameterException, IOException, NoSuchElementException {
        return parseTuringMachine(filepath_description, null);
    }

    public static TuringMachine parseTuringMachine(String filepath_description, String filepath_tape) throws FileNotFoundException, InvalidParameterException, IOException, NoSuchElementException {
        TuringMachine tm = new TuringMachine();
        Scanner scanner = new Scanner(new File(filepath_description));

        //// Parse states
        scanner.next("states");

        int stateCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < stateCount; i++) {
            String[] input = scanner.nextLine().trim().split("\\s+");

            // State lines can only have 1 or 2 parameters
            if (input.length > 2) {
                throw new InputMismatchException("Too many state parameters");
            }

            String state = input[0];

            // Add new state to turing machine
            tm.addState(state);

            // Check for accept and reject states
            if (input.length == 2) {
                switch(input[1]) {
                    case "+": tm.setAcceptState(state); break;
                    case "-": tm.setRejectState(state); break;
                    default: throw new InputMismatchException("Invalid state parameter");
                }
            }
        }

        // Get alphabet
        scanner.next("alphabet");
        int alphabetCount = scanner.nextInt();

        if (alphabetCount <= 0) {
            throw new InputMismatchException("Alphabet must contain at least one character");
        }

        for (int i = 0; i < alphabetCount; i++) {
            String input = scanner.next();
            
            if (input.length() != 1) {
                throw new
                 InputMismatchException("Alphabet characters must be separated by spaces");
            }

            tm.addToAlphabet(input.charAt(0));
        }

        scanner.nextLine();

        //// Parse transition list
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isBlank()) {
                break;
            }

            String[] items = line.split("\\s+");

            
            if (items.length != 5) {
                System.err.println("ERROR (" + line + ")");
                throw new InputMismatchException("Transition has too many arguments");
            }

            String from = items[0];
            String input = items[1];
            String to = items[2];
            String output = items[3];
            DIRECTION direction;
            try {
                direction = Transition.DIRECTION.valueOf(items[4]);
            } catch (IllegalArgumentException e) {
                throw new InputMismatchException("Transition direction must be L,R, or N");
            }

            if (input.length() > 1) {
                throw new InputMismatchException("Input must be a single character");
            }

            if (output.length() > 1) {
                throw new InputMismatchException("Output must be a single character");
            }

            tm.addTransition(from, input.charAt(0), to, output.charAt(0), direction);
        }
        scanner.close();

        if (filepath_tape != null) {
            parseTape(tm, filepath_tape);
        }

        return tm;
    }

    public static void parseTape(TuringMachine tm, String filepath) throws FileNotFoundException, IOException, InputMismatchException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tm.addTape(line);
            }
        }
    }
}
