package parser;

import tokenizer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {
    public final Token[] tokens;

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    public Token readToken(final int pos) throws ParseException {
        if (pos < 0 || pos >= tokens.length) {
            throw new ParseException("Ran out of tokens");
        } else {
            return tokens[pos];
        }
    }

    public Token getToken(final int pos) throws ParseException {
        return readToken(pos);
    }

    public void assertTokenIs(final int pos, final Token expected) throws ParseException {
        final Token received = getToken(pos);
        if (!expected.equals(received)) {
            throw new ParseException("Expected: " + expected + ", got: " + received);
        }
    }

    // exp ::= addExp
    public ParseResult<Exp> exp(final int startPos) throws ParseException {
        return addExp(startPos);
    }

    // addExp ::= multExp ((+ | -) multExp)*
    public ParseResult<Exp> addExp(final int startPos) throws ParseException {
        ParseResult<Exp> m = multExp(startPos);
        Exp result = m.result();
        int pos = m.nextPos();

        while (true) {
            try {
                Token t = getToken(pos);
                Op op;

                if (t instanceof PlusToken) {
                    op = new AddOp();
                } else if (t instanceof MinusToken) {
                    op = new SubOp();
                } else {
                    break;
                }

                ParseResult<Exp> m2 = multExp(pos + 1);
                result = new BinOpExp(result, op, m2.result());
                pos = m2.nextPos();
            } catch (ParseException e) {
                break;
            }
        }

        return new ParseResult<>(result, pos);
    }

    // multExp ::= primaryExp ((* | /) primaryExp)*
    public ParseResult<Exp> multExp(final int startPos) throws ParseException {
        ParseResult<Exp> m = primaryExp(startPos);
        Exp result = m.result();
        int pos = m.nextPos();

        while (true) {
            try {
                Token t = getToken(pos);
                Op op;

                if (t instanceof StarToken) {
                    op = new MulOp();
                } else if (t instanceof DivToken) {
                    op = new DivOp();
                } else {
                    break;
                }

                ParseResult<Exp> m2 = primaryExp(pos + 1);
                result = new BinOpExp(result, op, m2.result());
                pos = m2.nextPos();
            } catch (ParseException e) {
                break;
            }
        }

        return new ParseResult<>(result, pos);
    }

    // primaryExp ::= IDENTIFIER | INTEGER | `(` exp `)`
    public ParseResult<Exp> primaryExp(final int startPos) throws ParseException {
        Token t = getToken(startPos);

        if (t instanceof IdentifierToken id) {
            return new ParseResult<>(new VarExp(id.name()), startPos + 1);
        } else if (t instanceof IntegerLiteralToken i) {
            return new ParseResult<>(new IntLiteral(i.value()), startPos + 1);
        } else if (t instanceof LParenToken) {
            ParseResult<Exp> inner = exp(startPos + 1);
            assertTokenIs(inner.nextPos(), new RParenToken());
            return new ParseResult<>(new ParenExp(inner.result()), inner.nextPos() + 1);
        } else {
            throw new ParseException("Expected primary expression at " + startPos);
        }
    }

    // stmt ::= IDENTIFIER = exp ; | print exp ; | return [exp] ;
    public ParseResult<Stmt> stmt(final int startPos) throws ParseException {
        Token token = getToken(startPos);

        if (token instanceof IdentifierToken id) {
            assertTokenIs(startPos + 1, new EqualsToken());
            ParseResult<Exp> expr = exp(startPos + 2);
            assertTokenIs(expr.nextPos(), new SemicolonToken());
            return new ParseResult<>(new AssignStmt(id.name(), expr.result()), expr.nextPos() + 1);
        } else if (token instanceof PrintToken) {
            ParseResult<Exp> expr = exp(startPos + 1);
            assertTokenIs(expr.nextPos(), new SemicolonToken());
            return new ParseResult<>(new PrintlnStmt(expr.result()), expr.nextPos() + 1);
        } else if (token instanceof ReturnToken) {
            try {
                ParseResult<Exp> expr = exp(startPos + 1);
                assertTokenIs(expr.nextPos(), new SemicolonToken());
                return new ParseResult<Stmt>(new ReturnStmt(Optional.of(expr.result())), expr.nextPos() + 1);
            } catch (ParseException e) {
                assertTokenIs(startPos + 1, new SemicolonToken());
                return new ParseResult<Stmt>(new ReturnStmt(Optional.empty()), startPos + 2);
            }
        } else {
            throw new ParseException("Expected statement at " + startPos);
        }
    }

    // program ::= stmt*
    public ParseResult<Program> program(final int startPos) {
        List<Stmt> stmts = new ArrayList<>();
        int pos = startPos;

        while (pos < tokens.length) {
            try {
                ParseResult<Stmt> res = stmt(pos);
                stmts.add(res.result());
                pos = res.nextPos();
            } catch (ParseException e) {
                break;
            }
        }

        return new ParseResult<Program>(new Program(stmts), pos);
    }

    // Top-level entry point
    public Program parseWholeProgram() throws ParseException {
        ParseResult<Program> result = program(0);

        if (result.nextPos() == tokens.length) {
            return result.result();
        } else {
            throw new ParseException("Extra tokens after program at position " + result.nextPos());
        }
    }
}
