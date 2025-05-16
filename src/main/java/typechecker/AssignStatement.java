package typechecker;
public class AssignStatement implements Statement {
    public String var;
    public Expression expr;

    AssignStatement(String var, Expression expr) {
        this.var = var;
        this.expr = expr;
    }
}