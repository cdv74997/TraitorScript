package parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tokenizer.*;

//import java.util.Optional;

public class ParserTest {

    @Test
    void testIntegerLiteralExpression() throws ParseException {
        Token[] tokens = { new IntegerLiteralToken(10) };
        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.exp(0);

        assertTrue(result.result() instanceof IntLiteral);
        assertEquals(10, ((IntLiteral) result.result()).value());
        assertEquals(1, result.nextPos());
    }

    @Test
    void testParenthesizedExpression() throws ParseException {
        Token[] tokens = {
            new LParenToken(),
            new IntegerLiteralToken(42),
            new RParenToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.exp(0);

        assertTrue(result.result() instanceof ParenExp);
        ParenExp paren = (ParenExp) result.result();
        assertTrue(paren.expression() instanceof IntLiteral);
        assertEquals(42, ((IntLiteral) paren.expression()).value());
    }

    @Test
    void testAdditionExpression() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(5),
            new PlusToken(),
            new IntegerLiteralToken(3)
        };
        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.exp(0);

        assertTrue(result.result() instanceof BinOpExp);
        BinOpExp bin = (BinOpExp) result.result();
        assertTrue(bin.left() instanceof IntLiteral);
        assertTrue(bin.right() instanceof IntLiteral);
        assertEquals(5, ((IntLiteral) bin.left()).value());
        assertEquals(3, ((IntLiteral) bin.right()).value());
    }

    @Test
    void testAssignmentStatement() throws ParseException {
        Token[] tokens = {
            new IdentifierToken("x"),
            new EqualsToken(),
            new IntegerLiteralToken(9),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Stmt> result = parser.stmt(0);

        assertTrue(result.result() instanceof AssignStmt);
        AssignStmt stmt = (AssignStmt) result.result();
        assertEquals("x", stmt.name());
        assertEquals(9, ((IntLiteral) stmt.value()).value());
    }

    @Test
    void testPrintStatement() throws ParseException {
        Token[] tokens = {
            new PrintToken(),
            new IntegerLiteralToken(1),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Stmt> result = parser.stmt(0);

        assertTrue(result.result() instanceof PrintlnStmt);
        PrintlnStmt stmt = (PrintlnStmt) result.result();
        assertEquals(1, ((IntLiteral) stmt.expression()).value());
    }

    @Test
    void testReturnStatementWithExpression() throws ParseException {
        Token[] tokens = {
            new ReturnToken(),
            new IntegerLiteralToken(7),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Stmt> result = parser.stmt(0);

        assertTrue(result.result() instanceof ReturnStmt);
        ReturnStmt stmt = (ReturnStmt) result.result();
        assertTrue(stmt.value().isPresent());
        assertEquals(7, ((IntLiteral) stmt.value().get()).value());
    }

    @Test
    void testReturnStatementWithoutExpression() throws ParseException {
        Token[] tokens = {
            new ReturnToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Stmt> result = parser.stmt(0);

        assertTrue(result.result() instanceof ReturnStmt);
        ReturnStmt stmt = (ReturnStmt) result.result();
        assertTrue(stmt.value().isEmpty());
    }

    @Test
    void testProgramParsing() throws ParseException {
        Token[] tokens = {
            new PrintToken(),
            new IntegerLiteralToken(99),
            new SemicolonToken(),
            new ReturnToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        Program prog = parser.parseWholeProgram();

        assertEquals(2, prog.statements().size());
        assertTrue(prog.statements().get(0) instanceof PrintlnStmt);
        assertTrue(prog.statements().get(1) instanceof ReturnStmt);
    }

    @Test
    void testAssertTokenIsThrowsParseException() {
        // Create tokens for testing
        Token[] tokens = new Token[]{
        new IntegerLiteralToken(42),  // First token
        };

        // Instantiate parser with test tokens
        Parser parser = new Parser(tokens);

        // The expected token is a different type, which should cause a ParseException
        Token expectedToken = new PrintToken();  // Expected token is not the same as the first one (IntegerLiteralToken)
    
        // Use a try-catch block to check if ParseException is thrown
        try {
            parser.assertTokenIs(0, expectedToken); // This should throw ParseException
            fail("Expected ParseException to be thrown"); // Fail the test if exception is not thrown
        } catch (ParseException e) {
            // This block is reached if the exception is thrown, meaning the test passes
            assertTrue(e.getMessage().contains("Expected: " + expectedToken + ", got: " + tokens[0]));
        }
    }

    




}
