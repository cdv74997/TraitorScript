package parser;

import tokenizer.*;

//import java.beans.Expression;
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
    public ParseResult<TraitDef> traitDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new TraitToken());
        Token nameTok = getToken(pos + 1);
    
        if (!(nameTok instanceof IdentifierToken id)) {
            throw new ParseException("Expected trait name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new LCurlyToken());
        List<AbsMethodDef> methods = new ArrayList<>();
        pos = pos + 3;
    
        while (true) {
            try {
                ParseResult<AbsMethodDef> methodResult = absMethodDef(pos);
                methods.add(methodResult.result());
                pos = methodResult.nextPos();
            } catch (ParseException e) {
                break;
            }
        }
    
        assertTokenIs(pos, new RCurlyToken());
        return new ParseResult<>(new TraitDef(id.name(), methods), pos + 1);
    }
    

    // Definition of an implementation of a typeclass
    // impldef ::= `impl` traitname `for` type `{` conc_methoddef* `}`
    public ParseResult<ImplDef> implDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new ImplToken());
    
        Token traitNameTok = getToken(pos + 1);
        if (!(traitNameTok instanceof IdentifierToken traitNameId)) {
            throw new ParseException("Expected trait name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new ForToken());
    
        ParseResult<Type> typeRes = type(pos + 3);
        pos = typeRes.nextPos();
    
        assertTokenIs(pos, new LCurlyToken());
        pos++;
    
        List<ConcMethodDef> methods = new ArrayList<>();
        while (true) {
            try {
                ParseResult<ConcMethodDef> methodRes = concMethodDef(pos);
                methods.add(methodRes.result());
                pos = methodRes.nextPos();
            } catch (ParseException e) {
                break;
            }
        }
    
        assertTokenIs(pos, new RCurlyToken());
        return new ParseResult<>(new ImplDef(traitNameId.name(), typeRes.result(), methods), pos + 1);
    }
    

    // Definition of a toplevel function
    // funcdef ::= `func` var `(` comma_param `)` `:` type
    //             `{` stmt* `}` 
    public ParseResult<FuncDef> funcDef(int startPos) throws ParseException {
        int pos = startPos;
    
        assertTokenIs(pos, new FuncToken());
    
        Token funcNameTok = getToken(pos + 1);
        if (!(funcNameTok instanceof IdentifierToken funcNameId)) {
            throw new ParseException("Expected function name at position " + (pos + 1));
        }
    
        assertTokenIs(pos + 2, new LParenToken());
        ParseResult<List<Param>> paramsRes = commaParam(pos + 3);
        int posAfterParams = paramsRes.nextPos();
        assertTokenIs(posAfterParams, new RParenToken());
    
        assertTokenIs(posAfterParams + 1, new ColonToken());
        ParseResult<Type> returnTypeRes = type(posAfterParams + 2);
    
        int posAfterReturnType = returnTypeRes.nextPos();
        assertTokenIs(posAfterReturnType, new LCurlyToken());
        pos = posAfterReturnType + 1;
    
        List<Stmt> body = new ArrayList<>();
        while (true) {
            try {
                ParseResult<Stmt> stmtRes = stmt(pos);
                body.add(stmtRes.result());
                pos = stmtRes.nextPos();
            } catch (ParseException e) {
                break;
            }
        }
    
        assertTokenIs(pos, new RCurlyToken());
        return new ParseResult<>(
            new FuncDef(funcNameId.name(), paramsRes.result(), returnTypeRes.result(), body),
            pos + 1
        );
    }
    

    // stmt ::= `let` param `=` exp `;` | Variable declaration
    //      var `=` exp `;` | Assignment
    //      `if` `(` exp `)` stmt [`else` stmt] | if
    //      `while` `(` exp `)` stmt | while
    //      `break` `;` | break
    //      `println` `(` exp `)` | Printing something
    //      `{` stmt* `}` | Block
    //      `return` [exp] `;` | Return
    //      exp `;` Expression statements

    public ParseResult<Stmt> stmt(final int startPos) throws ParseException {
        Token t = getToken(startPos);
    
        // let param = exp;
        if (t instanceof LetToken) {
            ParseResult<Param> paramRes = param(startPos + 1);
            assertTokenIs(paramRes.nextPos(), new EqualsToken());
            ParseResult<Exp> rhs = exp(paramRes.nextPos() + 1);
            assertTokenIs(rhs.nextPos(), new SemicolonToken());

            // Extract the name and type from the Param object
            Param p = paramRes.result();
            
            // Create the LetStmt using the extracted name and type
            return new ParseResult<>(
                new LetStmt(p.name(), p.type(), rhs.result()),  // Pass name, type, and rhs expression
                rhs.nextPos() + 1
            );
        }

        // if (t instanceof LetToken) {
        //     ParseResult<Param> paramRes = param(startPos + 1);
        //     assertTokenIs(paramRes.nextPos(), new EqualsToken());
        //     ParseResult<Exp> rhs = exp(paramRes.nextPos() + 1);
        //     assertTokenIs(rhs.nextPos(), new SemicolonToken());
        
        //     Param p = paramRes.result();
        //     return new ParseResult<>(
        //         new LetStmt(p.name(), p.type(), rhs.result()),
        //         rhs.nextPos() + 1
        //     );
        // }
        
        // var = exp;
        if (t instanceof IdentifierToken id1 &&
            getToken(startPos + 1) instanceof EqualsToken) {
            ParseResult<Exp> rhs = exp(startPos + 2);
            assertTokenIs(rhs.nextPos(), new SemicolonToken());
            return new ParseResult<>(new AssignStmt(id1.name(), rhs.result()), rhs.nextPos() + 1);
        }
    
        // if (exp) stmt [else stmt]
        if (t instanceof IfToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            ParseResult<Exp> cond = exp(startPos + 2);
            assertTokenIs(cond.nextPos(), new RParenToken());
            ParseResult<Stmt> thenBranch = stmt(cond.nextPos() + 1);
    
            if (getToken(thenBranch.nextPos()) instanceof ElseToken) {
                ParseResult<Stmt> elseBranch = stmt(thenBranch.nextPos() + 1);
                return new ParseResult<>(
                    new IfStmt(cond.result(), thenBranch.result(), Optional.of(elseBranch.result())),
                    elseBranch.nextPos()
                );
            } else {
                return new ParseResult<>(
                    new IfStmt(cond.result(), thenBranch.result(), Optional.empty()),
                    thenBranch.nextPos()
                );
            }
        }
    
        // while (exp) stmt
        if (t instanceof WhileToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            ParseResult<Exp> cond = exp(startPos + 2);
            assertTokenIs(cond.nextPos(), new RParenToken());
            ParseResult<Stmt> body = stmt(cond.nextPos() + 1);
            return new ParseResult<>(new WhileStmt(cond.result(), body.result()), body.nextPos());
        }
    
        // break;
        if (t instanceof BreakToken) {
            assertTokenIs(startPos + 1, new SemicolonToken());
            return new ParseResult<>(new BreakStmt(), startPos + 2);
        }
    
        // println(exp)
        if (t instanceof PrintlnToken) {
            assertTokenIs(startPos + 1, new LParenToken());
            ParseResult<Exp> inner = exp(startPos + 2);
            assertTokenIs(inner.nextPos(), new RParenToken());
            return new ParseResult<>(new PrintlnStmt(inner.result()), inner.nextPos() + 1);
        }
    
        // { stmt* }
        if (t instanceof LCurlyToken) {
            int pos = startPos + 1;
            List<Stmt> stmts = new ArrayList<>();
            while (!(getToken(pos) instanceof RCurlyToken)) {
                ParseResult<Stmt> stmtRes = stmt(pos);
                stmts.add(stmtRes.result());
                pos = stmtRes.nextPos();
            }
            return new ParseResult<>(new BlockStmt(stmts), pos + 1);
        }
    
        // return [exp];
        if (t instanceof ReturnToken) {
            Token next = getToken(startPos + 1);
            if (next instanceof SemicolonToken) {
                return new ParseResult<>(new ReturnStmt(Optional.empty()), startPos + 2);
            } else {
                ParseResult<Exp> e = exp(startPos + 1);
                assertTokenIs(e.nextPos(), new SemicolonToken());
                return new ParseResult<>(new ReturnStmt(Optional.of(e.result())), e.nextPos() + 1);
            }
        }
    
        // exp;
        ParseResult<Exp> e = exp(startPos);
        assertTokenIs(e.nextPos(), new SemicolonToken());
        return new ParseResult<>(new ExpStmt(e.result()), e.nextPos() + 1);
    }
    


    // struct_actual_param ::= var `:` exp
    public ParseResult<StructActualParam> structActualParam(final int startPos) throws ParseException {
        // Expect a variable (IdentifierToken)
        Token t = getToken(startPos);
        if (!(t instanceof IdentifierToken id)) {
            throw new ParseException("Expected variable name at position " + startPos);
        }
    
        // Expect a colon
        assertTokenIs(startPos + 1, new ColonToken());
    
        // Parse the expression
        ParseResult<Exp> e = exp(startPos + 2);
    
        return new ParseResult<>(new StructActualParam(id.name(), e.result()), e.nextPos());
    }
    



    // struct_actual_params ::=
    // [struct_actual_param (`,` struct_actual_param)*]
    public ParseResult<List<StructActualParam>> structActualParams(final int startPos) throws ParseException {
        List<StructActualParam> params = new ArrayList<>();
        int pos = startPos;
    
        try {
            // First parameter
            ParseResult<StructActualParam> first = structActualParam(pos);
            params.add(first.result());
            pos = first.nextPos();
    
            // Additional parameters (comma separated)
            while (true) {
                Token t = getToken(pos);
                if (t instanceof CommaToken) {
                    ParseResult<StructActualParam> next = structActualParam(pos + 1);
                    params.add(next.result());
                    pos = next.nextPos();
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            // It's okay if this fails — empty param list is valid
        }
    
        return new ParseResult<>(params, pos);
    }
    


    // primary_exp ::= i | var | Integers and variables
    //                 `true` | `false` | Booleans
    //                 `self` | Instance on which we call a method
    //                 `(` exp `)` | Parenthesized expression

    //                 Creates a new instance of a struct
    //                 `new` structname `{` struct_actual_params `}`
    public ParseResult<Exp> primaryExp(final int startPos) throws ParseException {
        final Token t = getToken(startPos);
    
        if (t instanceof IntegerLiteralToken i) {
            return new ParseResult<>(new IntLiteral(i.value()), startPos + 1);
    
        } else if (t instanceof IdentifierToken id) {
            return new ParseResult<>(new VarExp(id.name()), startPos + 1);
    
        } else if (t instanceof TrueToken) {
            return new ParseResult<>(new BoolLiteral(true), startPos + 1);
    
        } else if (t instanceof FalseToken) {
            return new ParseResult<>(new BoolLiteral(false), startPos + 1);
    
        } else if (t instanceof SelfToken) {
            return new ParseResult<>(new SelfExp(), startPos + 1);
    
        } else if (t instanceof LParenToken) {
            ParseResult<Exp> inner = exp(startPos + 1);
            assertTokenIs(inner.nextPos(), new RParenToken());
            return new ParseResult<>(new ParenExp(inner.result()), inner.nextPos() + 1);
    
        } else if (t instanceof NewToken) {
            Token nameTok = getToken(startPos + 1);
            if (!(nameTok instanceof IdentifierToken id)) {
                throw new ParseException("Expected struct name after 'new'");
            }
    
            assertTokenIs(startPos + 2, new LCurlyToken());
            ParseResult<List<StructActualParam>> params = structActualParams(startPos + 3);
            assertTokenIs(params.nextPos(), new RCurlyToken());
    
            return new ParseResult<>(new NewExp(id.name(), params.result()), params.nextPos() + 1);
        }
    
        throw new ParseException("Invalid primary expression at position " + startPos);
    }
    
    public ParseResult<Exp> var(int startPos) throws ParseException {
        Token t = getToken(startPos);
        
        if (t instanceof IdentifierToken idToken) {
            // Create a variable expression using the identifier
            Exp varExp = new VarExp(idToken.name());  
            return new ParseResult<>(varExp, startPos + 1);  // Move to the next token
        } else {
            throw new ParseException("Expected a variable at position " + startPos);
        }
    }
    


    // dot_exp ::= primary_exp (`.` var)*
    public ParseResult<Exp> dotExp(final int startPos) throws ParseException {
        ParseResult<Exp> primary = primaryExp(startPos);  // first parse primary expression
        Exp result = primary.result();
        int pos = primary.nextPos();

        while (true) {
            Token t = getToken(pos);
            
            if (t instanceof DotToken) {  // if it's a dot, expect a variable (field or method)
                ParseResult<Exp> fieldOrMethod = var(pos + 1); // handle the variable after the dot
                result = new DotExp(result, fieldOrMethod.result());  // create a dot expression
                pos = fieldOrMethod.nextPos();
            } else {
                break;  // stop if there's no dot
            }
        }

        return new ParseResult<>(result, pos);
    }



    // comma_exp ::= [exp (`,` exp)*]
    public ParseResult<List<Exp>> commaExp(int startPos) throws ParseException {
        List<Exp> expressions = new ArrayList<>();

        // First expression is required
        ParseResult<Exp> first = exp(startPos);
        expressions.add(first.result());
        int pos = first.nextPos();

        // Handle additional comma-separated expressions
        while (true) {
            Token t;
            try {
                t = getToken(pos);
            } catch (ParseException e) {
                break; // No more tokens
            }

            if (t instanceof CommaToken) {
                pos++; // skip comma
                ParseResult<Exp> next = exp(pos);
                expressions.add(next.result());
                pos = next.nextPos();
            } else {
                break; // No more commas
            }
    }

    return new ParseResult<>(expressions, pos);
}

    
    // call_exp ::= dot_exp (`(` comma_exp `)`)* 
    public ParseResult<Exp> callExp(int startPos) throws ParseException {
        ParseResult<Exp> dotResult = dotExp(startPos);
        Exp result = dotResult.result();
        int pos = dotResult.nextPos();
    
        while (true) {
            try {
                Token t = getToken(pos);
                if (t instanceof LParenToken) {
                    pos++; // skip '('
    
                    ParseResult<List<Exp>> argResult = commaExp(pos);
                    List<Exp> args = argResult.result();
                    pos = argResult.nextPos();
    
                    assertTokenIs(pos, new RParenToken());
                    pos++;
    
                    result = new CallExpr(result, args);  // wrap in call
                } else {
                    break;
                }
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



    //less_than_exp ::= add_exp [`<` add_exp]
    public ParseResult<Exp> lessThanExp(int startPos) throws ParseException {
        // Parse the left-hand side expression
        ParseResult<Exp> leftResult = addExp(startPos);
        Exp result = leftResult.result();
        int pos = leftResult.nextPos();
    
        try {
            Token t = getToken(pos);
            if (t instanceof LessThanToken) {
                // If we see a <, parse the right-hand side
                ParseResult<Exp> rightResult = addExp(pos + 1);
                result = new BinOpExp(result, new LessThanOp(), rightResult.result());
                pos = rightResult.nextPos();
            }
        } catch (ParseException e) {
            // no <, return left side only
        }
    
        return new ParseResult<>(result, pos);
    }
    
    //equals_exp ::= less_than_exp [(`==` | `!=`) less_than_exp]
    public ParseResult<Exp> equalsExp(int startPos) throws ParseException {
        ParseResult<Exp> leftResult = lessThanExp(startPos);
        Exp result = leftResult.result();
        int pos = leftResult.nextPos();
    
        try {
            Token t = getToken(pos);
            Op op;
    
            if (t instanceof EqualsToken) {
                op = new EqOp();
            } else if (t instanceof NEqualsToken) {
                op = new NeqOp();
            } else {
                return new ParseResult<>(result, pos); // no equality operator
            }
    
            ParseResult<Exp> rightResult = lessThanExp(pos + 1);
            result = new BinOpExp(result, op, rightResult.result());
            pos = rightResult.nextPos();
        } catch (ParseException e) {
            // no second operand, return what we had
        }
    
        return new ParseResult<>(result, pos);
    }
    
    //exp ::= equals_exp
    public ParseResult<Exp> exp(int startPos) throws ParseException {
        return equalsExp(startPos);
    }
    
    //program_item ::= structdef | traitdef | impldef | funcdef
    public ParseResult<ProgramItem> programItem(int startPos) throws ParseException {
        ParseException lastError = null;
    
        try {
            ParseResult<StructDef> struct = structDef(startPos);
            return new ParseResult<>(struct.result(), struct.nextPos());
        } catch (ParseException e) {
            lastError = e;
        }
    
        try {
            ParseResult<TraitDef> trait = traitDef(startPos);
            return new ParseResult<>(trait.result(), trait.nextPos());
        } catch (ParseException e) {
            lastError = e;
        }
    
        try {
            ParseResult<ImplDef> impl = implDef(startPos);
            return new ParseResult<>(impl.result(), impl.nextPos());
        } catch (ParseException e) {
            lastError = e;
        }
    
        try {
            ParseResult<FuncDef> func = funcDef(startPos);
            return new ParseResult<>(func.result(), func.nextPos());
        } catch (ParseException e) {
            lastError = e;
        }
    
        // If all parsing attempts failed
        throw lastError != null ? lastError : new ParseException(startPos, "Expected a program item");
    }
    
    //program ::= program_item* stmt* stmt* is the entry point
    public ParseResult<Program> program(int startPos) throws ParseException {
        List<ProgramItem> items = new ArrayList<>();
        List<Stmt> stmts = new ArrayList<>();
        int pos = startPos;
    
        // Parse zero or more program items
        while (true) {
            try {
                ParseResult<ProgramItem> item = programItem(pos);
                items.add(item.result());
                pos = item.nextPos();
            } catch (ParseException e) {
                break; // No more program items
            }
        }
    
        // Parse zero or more statements (entry point)
        while (true) {
            try {
                ParseResult<Stmt> stmt = stmt(pos);
                stmts.add(stmt.result());
                pos = stmt.nextPos();
            } catch (ParseException e) {
                break; // No more statements
            }
        }
    
        return new ParseResult<>(new Program(items, stmts), pos);
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



