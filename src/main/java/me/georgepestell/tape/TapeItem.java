package tape;

import java.nio.BufferOverflowException;
import java.util.ArrayList;

public class TapeItem {
    static int MAX_VALUE = Integer.MAX_VALUE;
    public ArrayList<Character> cells;

    public TapeItem next;
    public TapeItem previous;

    public TapeItem() {
        this(null, null);
    }

    public TapeItem(TapeItem next, TapeItem previous) {
        this.next = next;
        this.previous = previous;
        cells = new ArrayList<Character>();
    }

    public void append(Character c) {
        if (cells.size() >= MAX_VALUE) {
            throw new BufferOverflowException();
        }

        cells.add(c);
    }

    public Character getCell(int offset) throws IndexOutOfBoundsException {
        if (offset > MAX_VALUE) {
            throw new IndexOutOfBoundsException();
        }

        while (cells.size() <= offset) {
            append('_');
        }

        return cells.get(offset);
    }

    public void overwrite(int offset, Character symbol) {
        if (offset >= MAX_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        while (offset >= cells.size()) {
            append('_');
        }

        cells.set(offset, symbol);
    }

    public int size() {
        return cells.size();
    }

    public ArrayList<Character> getCells() {
        return cells;
    }

}
