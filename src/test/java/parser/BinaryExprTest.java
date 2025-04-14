package parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryExprTest {

    @Test
    void testAdditionExpression() {
        Expr left = new IntLiteral(3);
        Expr right = new IntLiteral(4);
        Op op = new AddOp();

        BinaryExpr expr = new BinaryExpr(left, op, right);

        assertEquals(3, ((IntLiteral) expr.left()).value());
        assertEquals(4, ((IntLiteral) expr.right()).value());
        assertTrue(expr.op() instanceof AddOp);
    }

    @Test
    void testEqualityExpression() {
        Expr left = new VarExpr("x");
        Expr right = new VarExpr("y");
        Op op = new EqOp();

        BinaryExpr expr = new BinaryExpr(left, op, right);

        assertEquals("x", ((VarExpr) expr.left()).name());
        assertEquals("y", ((VarExpr) expr.right()).name());
        assertTrue(expr.op() instanceof EqOp);
    }

    @Test
    void testLessThanWithLiterals() {
        BinaryExpr expr = new BinaryExpr(
                new IntLiteral(5),
                new LessThanOp(),
                new IntLiteral(10)
        );

        assertTrue(expr.op() instanceof LessThanOp);
    }
}
