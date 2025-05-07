package parser;

//import java.util.List;

//public record DotExp(Exp target, List<String> path) implements Exp{}
public record DotExp(Exp base, Exp field) implements Exp {
    @Override
    public String toString() {
        return base + "." + field;  // Represents the dot expression as string
    }
}