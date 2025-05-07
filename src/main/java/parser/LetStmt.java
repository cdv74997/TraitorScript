package parser;

public record LetStmt(String name, Type type, Exp rhs) implements Stmt {}
//public record LetStmt(String name, Type type, Exp value) implements Stmt {}
