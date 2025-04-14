package parser;

public record LetStmt(String name, Type type, Exp value) implements Stmt {}
