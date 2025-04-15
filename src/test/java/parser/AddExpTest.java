package parser;

import org.junit.jupiter.api.Test;
import tokenizer.*;

import static org.junit.jupiter.api.Assertions.*;

public class AddExpTest {

    // Helper method to parse using Parser.addExp()
    private Exp parseExp(Token[] tokens) throws ParseException {
        Parser parser = new Parser(tokens);
        return parser.addExp(0).result();
    }

    @Test
    void testSingleIntLiteral() throws ParseException {
        Token[] tokens = { new IntegerLiteralToken(10) };
        Exp exp = parseExp(tokens);
        assertTrue(exp instanceof IntLiteral);
        assertEquals(10, ((IntLiteral) exp).value());
    }

    @Test
    void testAddition() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(1),
            new PlusToken(),
            new IntegerLiteralToken(2)
        };
        Exp exp = parseExp(tokens);
        assertTrue(exp instanceof BinOpExp);
        BinOpExp bin = (BinOpExp) exp;
        assertTrue(bin.op() instanceof AddOp);
        assertEquals(1, ((IntLiteral) bin.left()).value());
        assertEquals(2, ((IntLiteral) bin.right()).value());
    }

    @Test
    void testSubtraction() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(5),
            new MinusToken(),
            new IntegerLiteralToken(3)
        };
        Exp exp = parseExp(tokens);
        assertTrue(exp instanceof BinOpExp);
        BinOpExp bin = (BinOpExp) exp;
        assertTrue(bin.op() instanceof SubOp);
        assertEquals(5, ((IntLiteral) bin.left()).value());
        assertEquals(3, ((IntLiteral) bin.right()).value());
    }

    @Test
    void testAddThenSub() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(4),
            new PlusToken(),
            new IntegerLiteralToken(1),
            new MinusToken(),
            new IntegerLiteralToken(2)
        };
        Exp exp = parseExp(tokens);

        // Should parse as (4 + 1) - 2
        assertTrue(exp instanceof BinOpExp);
        BinOpExp outer = (BinOpExp) exp;
        assertTrue(outer.op() instanceof SubOp);

        BinOpExp inner = (BinOpExp) outer.left();
        assertTrue(inner.op() instanceof AddOp);
        assertEquals(4, ((IntLiteral) inner.left()).value());
        assertEquals(1, ((IntLiteral) inner.right()).value());
        assertEquals(2, ((IntLiteral) outer.right()).value());
    }

    @Test
    void testStopsOnNonAddOp() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(1),
            new StarToken(),
            new IntegerLiteralToken(2)
        };

        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.addExp(0);

        // Should parse 1 * 2 as a BinOpExp with MulOp
        assertTrue(result.result() instanceof BinOpExp);
        BinOpExp exp = (BinOpExp) result.result();
        assertTrue(exp.op() instanceof MulOp);
        assertEquals(1, ((IntLiteral) exp.left()).value());
        assertEquals(2, ((IntLiteral) exp.right()).value());
    }

    @Test
    void testIncompleteAdditionReturnsLeftOperand() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(1),
            new PlusToken() // No second operand
        };
        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.addExp(0);
        assertTrue(result.result() instanceof IntLiteral);
        assertEquals(1, ((IntLiteral) result.result()).value());
        // This confirms it just returns the left operand and stops
    }

    @Test
    void testBadTokenAtStartThrows() {
        Token[] tokens = {
            new PrintToken() // Invalid token for exp
        };

        Parser parser = new Parser(tokens);
        assertThrows(ParseException.class, () -> {
            parser.addExp(0);
        });
    }
}
