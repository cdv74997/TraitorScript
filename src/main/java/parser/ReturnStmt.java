package parser;

import java.util.Optional;

public record ReturnStmt(Optional<Exp> value) implements Stmt {
    public Optional<Exp> value() {
        return value;
    }
}




// package parser;

// public record ReturnStmt(Exp expression) implements Stmt {}
