package tape;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import turingmachine.Transition.DIRECTION;

public class Tape {

    public TapeItem head;
    private TapeItem tail;

    public TapeItem current;
    int offset;

    public Tape() {      
        head = new TapeItem();
        tail = head;

        current = head;
        offset = 0;
    }

    public void append(Character character) throws InvalidParameterException {
        if (tail.size() >= TapeItem.MAX_VALUE) {
            TapeItem newTail = new TapeItem(tail, null);
            tail.next = newTail;
            tail = newTail;
        }

        tail.append(character);
    }

    public void reset() {
        current = head;
        offset = 0;
    }

    public Character read() {
        return current.getCell(offset);
    }
    
    public void move(DIRECTION direction) throws InvalidParameterException {
        switch (direction) {
            case L: moveLeft(); break;
            case R: moveRight(); break;
            case N: break;
            default: throw new InvalidParameterException();
        }
    }

    public void moveRight() {
        if (offset >= TapeItem.MAX_VALUE) {
            if (current.equals(tail)){
                append('_');
            } else {
                current = tail;
                offset = 0;
            }
        } else {
            offset++;
        }
    }

    public void moveLeft() {
        if (offset <= 0) {
            if (current.equals(head)) {
                return;
            }
            current = current.previous;
            offset = current.size() - 1;
        } else {
            offset--;
        }
    }

    public void write(Character symbol) {
        current.overwrite(offset, symbol);
    }

    public void printTape() {

        if (isEmpty()) {
            System.out.println("_");
            return;
        }

        TapeItem item = head;
        while (item != null) {

            ArrayList<Character> itemCells = item.getCells();
            for (int i = 0; i < itemCells.size(); i++) {
                
                Character c = itemCells.get(i);
                
                if (c.equals('_') && isEmpty(item, i+1)) {
                    System.out.println();
                    return;
                }

                System.out.printf("%c", c);
            }
            
            item = item.next;
        }

        System.out.println();
    }

    public boolean isEmpty() {
        return isEmpty(head, 0);
    }

    public boolean isEmpty(TapeItem fromItem, int fromOffset) { 
        if (fromItem == null) {
            return true;
        }

        TapeItem item = fromItem;

        for (int i = fromOffset; i < item.size(); i++) {
            if (!item.getCell(i).equals('_')) {
                return false;
            }
        }

        item = item.next;

        while (item != null) {
            for (Character c : item.getCells()) {
                if (!c.equals('_')) {
                    return false;
                }
            }
        }

        return true;
    }

}
