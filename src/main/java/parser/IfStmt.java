package parser;

public record IfStmt(Exp condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}
