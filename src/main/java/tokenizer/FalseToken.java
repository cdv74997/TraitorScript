package tokenizer;

public record FalseToken() implements Token {
    @Override
    public String toString() {
        return "false";
    }
}
