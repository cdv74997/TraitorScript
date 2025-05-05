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


    // Parses: comma_type ::= [type (`,` type)*]
    public ParseResult<List<Type>> commaType(final int startPos) throws ParseException {
        List<Type> types = new ArrayList<>();
        int pos = startPos;

        try {
            ParseResult<Type> first = type(pos);
            types.add(first.result());
            pos = first.nextPos();

            while (true) {
                Token t = getToken(pos);
                if (t instanceof CommaToken) {
                    ParseResult<Type> next = type(pos + 1);
                    types.add(next.result());
                    pos = next.nextPos();
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            // empty list is valid
        }

        return new ParseResult<>(types, pos);
    }

    // type ::=
    // `Int` | `Void` | `Boolean` | Built-in types
    // `Self` | Refers to our own type in a trait
    // structname | Structs are a valid kind of type
    // `(` type `)` | Parenthesized type
    // `(` comma_type `)` `=>` type Higher-order function

    public ParseResult<Type> type(int startPos) throws ParseException {
        Token t = getToken(startPos);
    
        if (t instanceof IntToken) {
            return new ParseResult<>(new IntType(), startPos + 1);
        } else if (t instanceof VoidToken) {
            return new ParseResult<>(new VoidType(), startPos + 1);
        } else if (t instanceof BooleanToken) {
            return new ParseResult<>(new BooleanType(), startPos + 1);
        } else if (t instanceof SelfToken) {
            return new ParseResult<>(new SelfType(), startPos + 1);
        } else if (t instanceof IdentifierToken id) {
            return new ParseResult<>(new StructType(id.name()), startPos + 1);
        } else if (t instanceof LParenToken) {
            // Could be parenthesized type or function type
            ParseResult<List<Type>> paramTypes = commaType(startPos + 1);
            int nextPos = paramTypes.nextPos();
            assertTokenIs(nextPos, new RParenToken());
    
            // Check for function arrow
            Token afterParen = getToken(nextPos + 1);
            if (afterParen instanceof ArrowToken) {
                // Function type: (comma_type) => type
                ParseResult<Type> returnType = type(nextPos + 2);
                return new ParseResult<>(
                    new FunctionType(paramTypes.result(), returnType.result()),
                    returnType.nextPos()
                );
            } else {
                // Just parenthesized type
                List<Type> types = paramTypes.result();
                if (types.size() != 1) {
                    throw new ParseException("Expected exactly one type in parentheses at " + startPos);
                }
                return new ParseResult<>(types.get(0), nextPos + 1);
            }
        }
    
        throw new ParseException("Expected type at position " + startPos + ", got: " + t);
    }
    
    
    //param ::= var `:` type
    public ParseResult<Param> param(int startPos) throws ParseException {
        Token t = getToken(startPos);
    
        if (!(t instanceof IdentifierToken id)) {
            throw new ParseException("Expected variable name at position " + startPos + ", got: " + t);
        }
    
        Token colon = getToken(startPos + 1);
        if (!(colon instanceof ColonToken)) {
            throw new ParseException("Expected ':' after variable at position " + (startPos + 1) + ", got: " + colon);
        }
    
        ParseResult<Type> parsedType = type(startPos + 2);
        return new ParseResult<>(new Param(id.name(), parsedType.result()), parsedType.nextPos());
    }

    //comma_param ::= [param (`,` param)*]
    public ParseResult<List<Param>> commaParam(int startPos) throws ParseException {
        List<Param> params = new ArrayList<>();
        int pos = startPos;
    
        try {
            ParseResult<Param> first = param(pos);
            params.add(first.result());
            pos = first.nextPos();
    
            while (true) {
                Token t = getToken(pos);
                if (!(t instanceof CommaToken)) {
                    break;
                }
    
                ParseResult<Param> next = param(pos + 1);
                params.add(next.result());
                pos = next.nextPos();
            }
        } catch (ParseException e) {
            // It's okay to have no parameters — empty list
        }
    
        return new ParseResult<>(params, pos);
    }
    
    //structdef ::= `struct` structname `{` comma_param `}`
    public ParseResult<StructDef> structDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new StructToken());
        Token nameTok = getToken(pos + 1);
    
        if (!(nameTok instanceof IdentifierToken id)) {
            throw new ParseException("Expected struct name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new LCurlyToken());
        ParseResult<List<Param>> fields = commaParam(pos + 3);
        assertTokenIs(fields.nextPos(), new RCurlyToken());
    
        return new ParseResult<>(
            new StructDef(id.name(), fields.result()),
            fields.nextPos() + 1
        );
    }

    // Definition of an abstract method
    // abs_methoddef ::= `method` var (` comma_param `)` `:` type `;`
    public ParseResult<AbsMethodDef> absMethodDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new MethodToken());
        Token nameTok = getToken(pos + 1);
    
        if (!(nameTok instanceof IdentifierToken id)) {
            throw new ParseException("Expected method name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new LParenToken());
        ParseResult<List<Param>> params = commaParam(pos + 3);
        assertTokenIs(params.nextPos(), new RParenToken());
        assertTokenIs(params.nextPos() + 1, new ColonToken());
    
        ParseResult<Type> returnType = type(params.nextPos() + 2);
        assertTokenIs(returnType.nextPos(), new SemicolonToken());
    
        return new ParseResult<>(
            new AbsMethodDef(id.name(), params.result(), returnType.result()),
            returnType.nextPos() + 1
        );
    }
    

    //conc_methoddef ::= `method` var `(` comma_param `)` `:` type `{` stmt* `}`
    public ParseResult<ConcMethodDef> concMethodDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new MethodToken());
        Token nameTok = getToken(pos + 1);
    
        if (!(nameTok instanceof IdentifierToken id)) {
            throw new ParseException("Expected method name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new LParenToken());
        ParseResult<List<Param>> params = commaParam(pos + 3);
        assertTokenIs(params.nextPos(), new RParenToken());
        assertTokenIs(params.nextPos() + 1, new ColonToken());
    
        ParseResult<Type> returnType = type(params.nextPos() + 2);
        assertTokenIs(returnType.nextPos(), new LCurlyToken());
    
        List<Stmt> body = new ArrayList<>();
        int bodyPos = returnType.nextPos() + 1;
        while (true) {
            try {
                ParseResult<Stmt> stmtRes = stmt(bodyPos);
                body.add(stmtRes.result());
                bodyPos = stmtRes.nextPos();
            } catch (ParseException e) {
                break;
            }
        }
    
        assertTokenIs(bodyPos, new RCurlyToken());
    
        return new ParseResult<>(
            new ConcMethodDef(id.name(), params.result(), returnType.result(), body),
            bodyPos + 1
        );
    }
    
    // Definition of a trait (typeclass)
    // traitdef ::= `trait` traitname `{` abs_methoddef* `}`
    



    // Definition of an implementation of a typeclass
    // impldef ::= `impl` traitname `for` type `{` conc_methoddef* `}`

    // Definition of a toplevel function
    // funcdef ::= `func` var `(` comma_param `)` `:` type
    //             `{` stmt* `}` 
















































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
