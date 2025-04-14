package tokenizer;

public record DivToken() implements Token {
    @Override
    public String toString() {
        return "/";
    }
}
