package parser;

import java.util.List;

public record DotExpr(Expr target, List<String> path) implements Expr{
    
}
