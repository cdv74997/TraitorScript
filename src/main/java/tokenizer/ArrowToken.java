package tokenizer;

public record ArrowToken() implements Token {
    @Override
    public String toString() {
        return "=>";
    }
}
