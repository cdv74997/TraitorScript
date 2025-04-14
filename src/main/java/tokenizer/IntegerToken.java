package tokenizer;

public record IntegerToken(int value) implements Token {
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
