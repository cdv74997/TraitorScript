package parser;

public record AssignStmt(String name, Exp value) implements Stmt {}
