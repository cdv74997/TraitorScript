package typechecker;
public class BinaryExpr implements Expression {
    public String op;
    public Expression left, right;

    BinaryExpr(String op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}