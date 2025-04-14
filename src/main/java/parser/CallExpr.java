package parser;

import java.util.List;

public record CallExpr(Exp function, List<List<Exp>> argumentGroups) implements Exp {}
