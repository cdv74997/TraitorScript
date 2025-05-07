package tokenizer;


public record TrueToken() implements Token {
    @Override
    public String toString() {
        return "true";
    }
}

