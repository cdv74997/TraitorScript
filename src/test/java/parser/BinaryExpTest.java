package parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryExpTest {

    @Test
    void testAdditionExpression() {
        Exp left = new IntLiteral(3);
        Exp right = new IntLiteral(4);
        Op op = new AddOp();

        BinOpExp exp = new BinOpExp(left, op, right);

        assertEquals(3, ((IntLiteral) exp.left()).value());
        assertEquals(4, ((IntLiteral) exp.right()).value());
        assertTrue(exp.op() instanceof AddOp);
    }

    @Test
    void testEqualityExpression() {
        Exp left = new VarExp("x");
        Exp right = new VarExp("y");
        Op op = new EqOp();

        BinOpExp exp = new BinOpExp(left, op, right);

        assertEquals("x", ((VarExp) exp.left()).name());
        assertEquals("y", ((VarExp) exp.right()).name());
        assertTrue(exp.op() instanceof EqOp);
    }

    @Test
    void testLessThanWithLiterals() {
        BinOpExp exp = new BinOpExp(
                new IntLiteral(5),
                new LessThanOp(),
                new IntLiteral(10)
        );

        assertTrue(exp.op() instanceof LessThanOp);
    }
}
