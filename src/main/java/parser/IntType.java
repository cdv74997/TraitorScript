package parser;

public record IntType() implements Type {
    @Override
    public String toString() {
        return "Int";
    }
}
