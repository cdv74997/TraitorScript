package parser;

import java.util.List;


//public record CallExpr(Exp function, List<List<Exp>> argumentGroups) implements Exp {}
public record CallExpr(Exp callee, List<Exp> arguments) implements Exp {}