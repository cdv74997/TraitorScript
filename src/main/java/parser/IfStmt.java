package parser;

public record IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}
