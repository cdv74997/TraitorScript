package typechecker;
public class FieldAccessExpr implements Expression {
    public final Expression receiver;
    public final String field;

    public FieldAccessExpr(Expression receiver, String field) {
        this.receiver = receiver;
        this.field = field;
    }
}