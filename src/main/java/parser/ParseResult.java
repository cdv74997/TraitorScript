package parser;

public class ParseResult<T> {
    public final T result;
    public final int nextPos;

    public ParseResult(T result, int nextPos) {
        this.result = result;
        this.nextPos = nextPos;
    }
}
