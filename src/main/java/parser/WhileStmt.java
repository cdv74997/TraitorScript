package parser;

public record WhileStmt(Expr condition, Stmt body) implements Stmt{}
