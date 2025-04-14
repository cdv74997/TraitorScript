package parser;

import java.util.List;

public record CallExpr(Expr function, List<List<Expr>> argumentGroups) implements Expr {}
