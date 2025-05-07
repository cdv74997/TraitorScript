package parser;

import parser.ParseException;

public class ParseException extends Exception {
    private final int position;
    
    public int getPosition() {
        return position;
    }
    
    public ParseException(String message) {
        super(message);
        this.position = -1; // or 0, or any other placeholder
    }

    

    public ParseException(int position, String message) {
        super("Parse error at position " + position + ": " + message);
        this.position = position;
    }

    
}
