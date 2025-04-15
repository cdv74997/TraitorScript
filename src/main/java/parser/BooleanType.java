package parser;

public record BooleanType() implements Type {
    @Override
    public String toString() {
        return "Bool";
    }
}
