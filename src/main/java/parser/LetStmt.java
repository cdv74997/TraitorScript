package parser;

public record LetStmt(String name, Type type, Expr value) implements Stmt {}
