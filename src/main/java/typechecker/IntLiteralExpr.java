package typechecker;

public class IntLiteralExpr implements Expression {
    private int value;

    public IntLiteralExpr(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
