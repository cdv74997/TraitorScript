package typechecker;

public class IntLiteralExpr implements Expression {
    public int value;

    public IntLiteralExpr(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
