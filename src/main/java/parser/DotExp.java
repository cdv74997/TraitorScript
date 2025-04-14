package parser;

import java.util.List;

public record DotExp(Exp target, List<String> path) implements Exp{}
