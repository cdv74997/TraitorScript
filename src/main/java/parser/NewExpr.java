package parser;

import java.util.Map;

public record NewExpr(String structname, Map<String, Expr> fields) implements Expr{}
