package tokenizer;

public record PrintToken() implements Token {
    @Override
    public String toString() {
        return "print";
    }
}
