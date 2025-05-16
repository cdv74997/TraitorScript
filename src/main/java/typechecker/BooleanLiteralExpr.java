package typechecker;
public class BooleanLiteralExpr implements Expression {
    public boolean value;

    BooleanLiteralExpr(boolean value) {
        this.value = value;
    }
}