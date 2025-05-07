package parser;

import java.util.Optional;

public record IfStmt(Exp condition, Stmt thenBranch, Optional<Stmt> elseBranch) implements Stmt {}

//public record IfStmt(Exp condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}
