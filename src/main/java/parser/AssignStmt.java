package parser;

public record AssignStmt(String name, Expr value) implements Stmt {}
