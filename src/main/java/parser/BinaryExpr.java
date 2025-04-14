package parser;

public record BinaryExpr(Expr left, Op op, Expr right) implements Expr {}
