package parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tokenizer.EqualsToken;
import tokenizer.IdentifierToken;
import tokenizer.ReturnToken;
import tokenizer.SemicolonToken;
import tokenizer.Token;

public class Parser {
    private final Token[] tokens;

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    // stmt ::= IDENTIFIER `=` exp `;` |
    //          `print` exp `;` |
    //          `return` [exp] `;`
    public ParseResult<Stmt> stmt(final int startPos) throws ParseException {
        final Token token = readToken(startPos);
        if (token instanceof IdentifierToken id) {
            String name = id.name;
            assertTokenIs(startPos + 1, new EqualsToken());
            ParseResult<Exp> expression = exp(startPos + 2);
            assertTokenIs(expression.nextPos, new SemicolonToken());
            AssignStmt assign = new AssignStmt(name, expression.result);
            return new ParseResult<>(assign, expression.nextPos + 1);
        } else if (token instanceof PrintToken) {
            ParseResult<Exp> expression = exp(startPos + 1);
            assertTokenIs(expression.nextPos, new SemicolonToken());
            PrintStmt print = new PrintStmt(expression.result);
            return new ParseResult<>(print, expression.nextPos + 1);
        } else if (token instanceof ReturnToken) {
            ParseResult<Optional<Exp>> opExpression;
            try {
                ParseResult<Exp> expression = exp(startPos + 1);
                opExpression = new ParseResult<>(Optional.of(expression.result), expression.nextPos);
            } catch (ParseException e) {
                opExpression = new ParseResult<>(Optional.empty(), startPos + 1);
            }
            assertTokenIs(opExpression.nextPos, new SemicolonToken());
            return new ParseResult<>(new ReturnStmt(opExpression.result), opExpression.nextPos + 1);
        } else {
            throw new ParseException("Expected statement; got: " + token);
        }
    }

    // Other methods like exp(), assertTokenIs(), etc.

    private Token readToken(final int pos) throws ParseException {
        if (pos < 0 || pos >= tokens.length) {
            throw new ParseException("Ran out of tokens", pos);
        } else {
            return tokens[pos];
        }
    }

    private void assertTokenIs(final int pos, final Token expected) throws ParseException {
        final Token received = readToken(pos);
        if (!expected.equals(received)) {
            throw new ParseException("Expected: " + expected + "; received: " + received, pos);
        }
    }

    public ParseResult<Program> program(int startPos) throws ParseException {
        List<ProgramItem> items = new ArrayList<>();
        int pos = startPos;

        // Parse zero or more top-level items
        while (true) {
            try {
                ParseResult<ProgramItem> itemRes = programItem(pos);
                items.add(itemRes.result);
                pos = itemRes.nextPos;
            } catch (ParseException e) {
                break; // Done with program items
            }
        }

        // Then expect zero or more statements
        List<Stmt> stmts = new ArrayList<>();
        while (true) {
            try {
                ParseResult<Stmt> stmtRes = stmt(pos);
                stmts.add(stmtRes.result);
                pos = stmtRes.nextPos;
            } catch (ParseException e) {
                break;
            }
        }

        return new ParseResult<>(new Program(items, stmts), pos);
    }

    public Program parseWholeProgram() throws ParseException {
        ParseResult<Program> result = program(0);
        if (result.nextPos == tokens.length) {
            return result.result;
        } else {
            throw new ParseException("Unexpected token at position " + result.nextPos, 0);
        }
    }

    // Next up: programItem()...
}
