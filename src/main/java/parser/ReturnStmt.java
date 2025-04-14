package parser;

import java.util.Optional;

public record ReturnStmt(Optional<Exp> expression) implements Stmt {}




// package parser;

// public record ReturnStmt(Exp expression) implements Stmt {}
