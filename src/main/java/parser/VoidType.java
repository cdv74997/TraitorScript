package parser;

public record VoidType() implements Type {
    @Override
    public String toString() {
        return "Void";
    }
}
