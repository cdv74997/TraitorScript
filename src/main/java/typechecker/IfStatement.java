package typechecker;
public class IfStatement implements Statement {
    public final Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch; // can be null

    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}