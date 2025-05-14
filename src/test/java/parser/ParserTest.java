package parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import tokenizer.*;

import java.util.Optional;

public class ParserTest {

    @Test
    void testCommaTypeWithMultipleTypes() throws ParseException {
        Token[] tokens = {
            new IntToken(),
            new CommaToken(),
            new BooleanToken(),
            new CommaToken(),
            new VoidToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<List<Type>> result = parser.commaType(0);

        assertEquals(3, result.result().size());
        assertTrue(result.result().get(0) instanceof IntType);
        assertTrue(result.result().get(1) instanceof BooleanType);
        assertTrue(result.result().get(2) instanceof VoidType);
        assertEquals(5, result.nextPos()); // All tokens consumed
    }


    @Test
    void testCommaTypeWithSingleType() throws ParseException {
        Token[] tokens = { new IntToken() };
        Parser parser = new Parser(tokens);
        ParseResult<List<Type>> result = parser.commaType(0);

        assertEquals(1, result.result().size());
        assertTrue(result.result().get(0) instanceof IntType);
        assertEquals(1, result.nextPos());
    }
    
    @Test
    void testCommaTypeWithNoType() throws ParseException {
        Token[] tokens = {};
        Parser parser = new Parser(tokens);
        ParseResult<List<Type>> result = parser.commaType(0);

        assertEquals(0, result.result().size());
        assertEquals(0, result.nextPos());
    }

    ///

    @Test
    void testIntType() throws ParseException {
        Token[] tokens = { new IntToken() };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof IntType);
        assertEquals(1, result.nextPos());
    }


    @Test
    void testVoidType() throws ParseException {
        Token[] tokens = { new VoidToken() };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof VoidType);
        assertEquals(1, result.nextPos());
    }

    @Test
    void testBooleanType() throws ParseException {
        Token[] tokens = { new BooleanToken() };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof BooleanType);
        assertEquals(1, result.nextPos());
    }

    @Test
    void testSelfType() throws ParseException {
        Token[] tokens = { new SelfToken() };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof SelfType);
        assertEquals(1, result.nextPos());
    }

    @Test
    void testStructType() throws ParseException {
        Token[] tokens = { new IdentifierToken("MyStruct") };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof StructType);
        assertEquals("MyStruct", ((StructType) result.result()).name());
        assertEquals(1, result.nextPos());
    }

    
    // @Test
    // void testParenthesizedSingleType() throws ParseException {
    //     Token[] tokens = {
    //         new LParenToken(),
    //         new IntToken(),
    //         new RParenToken()
    //     };
    //     Parser parser = new Parser(tokens);
    //     ParseResult<Type> result = parser.type(0);

    //     assertTrue(result.result() instanceof IntType);
    //     assertEquals(3, result.nextPos());
    // }

    @Test
    void testFunctionType() throws ParseException {
        Token[] tokens = {
            new LParenToken(),
            new IntToken(),
            new CommaToken(),
            new BooleanToken(),
            new RParenToken(),
            new ArrowToken(),
            new VoidToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Type> result = parser.type(0);

        assertTrue(result.result() instanceof FunctionType);
        FunctionType funcType = (FunctionType) result.result();
        assertEquals(2, funcType.paramTypes().size());
        assertTrue(funcType.paramTypes().get(0) instanceof IntType);
        assertTrue(funcType.paramTypes().get(1) instanceof BooleanType);
        assertTrue(funcType.returnType() instanceof VoidType);
        assertEquals(7, result.nextPos());
    }


    ////param ::= var `:` type
    @Test
    void testValidParamWithIntType() throws ParseException {
        Token[] tokens = {
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<Param> result = parser.param(0);

        assertEquals("x", result.result().name());
        assertTrue(result.result().type() instanceof IntType);
        assertEquals(3, result.nextPos());
    }

    @Test
    void testMissingColonThrows() {
        Token[] tokens = {
            new IdentifierToken("x"),
            new IntToken() // should be ColonToken
        };
        Parser parser = new Parser(tokens);

        Exception exception = assertThrows(ParseException.class, () -> parser.param(0));
        assertTrue(exception.getMessage().contains("Expected ':' after variable"));
    }

    // @Test
    // void testMissingTypeThrows() {
    //     Token[] tokens = {
    //         new IdentifierToken("x"),
    //         new ColonToken()
    //         // Missing type
    //     };
    //     Parser parser = new Parser(tokens);

    //     Exception exception = assertThrows(ParseException.class, () -> parser.param(0));
    //     assertTrue(exception.getMessage().contains("Expected type at position"));
    // }

    @Test
    void testFirstTokenNotIdentifierThrows() {
        Token[] tokens = {
            new IntToken(), // Invalid start token
            new ColonToken(),
            new IntToken()
        };
        Parser parser = new Parser(tokens);

        Exception exception = assertThrows(ParseException.class, () -> parser.param(0));
        assertTrue(exception.getMessage().contains("Expected variable name at position"));
    }


    //comma_param ::= [param (`,` param)*]

    //structdef ::= `struct` structname `{` comma_param `}`
    @Test
    void testValidStructDefSingleField() throws ParseException {
        Token[] tokens = {
            new StructToken(),
            new IdentifierToken("Point"),
            new LCurlyToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RCurlyToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<StructDef> result = parser.structDef(0);

        assertEquals("Point", result.result().name());
        List<Param> fields = result.result().fields();
        assertEquals(1, fields.size());
        assertEquals("x", fields.get(0).name());
        assertTrue(fields.get(0).type() instanceof IntType);
        assertEquals(7, result.nextPos());
    }

    @Test
    void testValidStructDefMultipleFields() throws ParseException {
        Token[] tokens = {
            new StructToken(),
            new IdentifierToken("Pair"),
            new LCurlyToken(),
            new IdentifierToken("first"),
            new ColonToken(),
            new IntToken(),
            new CommaToken(),
            new IdentifierToken("second"),
            new ColonToken(),
            new BooleanToken(),
            new RCurlyToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<StructDef> result = parser.structDef(0);

        assertEquals("Pair", result.result().name());
        List<Param> fields = result.result().fields();
        assertEquals(2, fields.size());
        assertEquals("first", fields.get(0).name());
        assertTrue(fields.get(0).type() instanceof IntType);
        assertEquals("second", fields.get(1).name());
        assertTrue(fields.get(1).type() instanceof BooleanType);
        assertEquals(11, result.nextPos());
    }

    @Test
    void testStructDefMissingNameThrows() {
        Token[] tokens = {
            new StructToken(),
            new IntToken(), // invalid name
            new LCurlyToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RCurlyToken()
        };
        Parser parser = new Parser(tokens);

        Exception ex = assertThrows(ParseException.class, () -> parser.structDef(0));
        assertTrue(ex.getMessage().contains("Expected struct name"));
    }


    // abs_methoddef ::= `method` var (` comma_param `)` `:` type `;`

    @Test
    void testValidAbsMethodDefSingleParam() throws ParseException {
        Token[] tokens = {
            new MethodToken(),
            new IdentifierToken("getX"),
            new LParenToken(),
            new IdentifierToken("a"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new IntToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<AbsMethodDef> result = parser.absMethodDef(0);

        AbsMethodDef method = result.result();
        assertEquals("getX", method.name());
        assertEquals(1, method.params().size());
        assertEquals("a", method.params().get(0).name());
        assertTrue(method.params().get(0).type() instanceof IntType);
        assertTrue(method.returnType() instanceof IntType);
        assertEquals(10, result.nextPos());
    }

    @Test
    void testValidAbsMethodDefMultipleParams() throws ParseException {
        Token[] tokens = {
            new MethodToken(),
            new IdentifierToken("max"),
            new LParenToken(),
            new IdentifierToken("a"),
            new ColonToken(),
            new IntToken(),
            new CommaToken(),
            new IdentifierToken("b"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new IntToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
        ParseResult<AbsMethodDef> result = parser.absMethodDef(0);

        AbsMethodDef method = result.result();
        assertEquals("max", method.name());
        assertEquals(2, method.params().size());
        assertEquals("a", method.params().get(0).name());
        assertEquals("b", method.params().get(1).name());
        assertTrue(method.returnType() instanceof IntType);
        assertEquals(14, result.nextPos());
    }

    @Test
    void testMissingMethodNameThrows() {
        Token[] tokens = {
            new MethodToken(),
            new IntToken(), // Invalid method name
            new LParenToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new IntToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        Exception ex = assertThrows(ParseException.class, () -> parser.absMethodDef(0));
        assertTrue(ex.getMessage().contains("Expected method name"));
    }

    @Test
    void testMissingColonBeforeReturnTypeThrows() {
        Token[] tokens = {
            new MethodToken(),
            new IdentifierToken("foo"),
            new LParenToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new IntToken(), // Missing colon
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        Exception ex = assertThrows(ParseException.class, () -> parser.absMethodDef(0));
        assertTrue(ex.getMessage().startsWith("Expected:"));
    }

    //conc_methoddef ::= `method` var `(` comma_param `)` `:` type `{` stmt* `}`
    @Test
    void testConcMethodDefParsesCorrectly() throws ParseException {
        Token[] tokens = {
            new MethodToken(),                    // method
            new IdentifierToken("foo"),           // method name
            new LParenToken(),                    // (
            new IdentifierToken("x"),             // parameter name
            new ColonToken(),                     // :
            new IntToken(),                       // parameter type
            new RParenToken(),                    // )
            new ColonToken(),                     // :
            new IntToken(),                       // return type
            new LCurlyToken(),                    // {
            new ReturnToken(),                    // return
            new IdentifierToken("x"),             // x
            new SemicolonToken(),                 // ;
            new RCurlyToken()                     // }
        };

        Parser parser = new Parser(tokens);
    ParseResult<ConcMethodDef> result = parser.concMethodDef(0);

    ConcMethodDef def = result.result();
    assertEquals("foo", def.name());

    // Check parameter
    assertEquals(1, def.params().size());
    Param param = def.params().get(0);
    assertEquals("x", param.name());
    assertTrue(param.type() instanceof IntType);

    // Check return type
    assertTrue(def.returnType() instanceof IntType);

    // Check body statements
    assertEquals(1, def.body().size());
    Stmt stmt = def.body().get(0);
    assertTrue(stmt instanceof ReturnStmt);

    ReturnStmt returnStmt = (ReturnStmt) stmt;

    assertTrue(returnStmt.value().isPresent(), "Return expression should be present");

    Exp returnExp = returnStmt.value().get();
    assertTrue(returnExp instanceof VarExp);
    assertEquals("x", ((VarExp) returnExp).name());
    }
        


    // traitdef ::= `trait` traitname `{` abs_methoddef* `}`
    @Test
    void testTraitDefParsesMultipleAbsMethods() throws ParseException {
        Token[] tokens = {
            new TraitToken(),
            new IdentifierToken("Eq"),
            new LCurlyToken(),

            // method eq(x: Int): Boolean;
            new MethodToken(),
            new IdentifierToken("eq"),
            new LParenToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new BooleanToken(),
            new SemicolonToken(),

            // method neq(x: Int): Boolean;
            new MethodToken(),
            new IdentifierToken("neq"),
            new LParenToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new BooleanToken(),
            new SemicolonToken(),

            new RCurlyToken()
        };

        Parser parser = new Parser(tokens);
        ParseResult<TraitDef> result = parser.traitDef(0);
        TraitDef trait = result.result();

        assertEquals("Eq", trait.name());
        assertEquals(2, trait.methods().size());

        AbsMethodDef method1 = trait.methods().get(0);
        assertEquals("eq", method1.name());
        assertEquals(1, method1.params().size());
        assertEquals("x", method1.params().get(0).name());
        assertTrue(method1.params().get(0).type() instanceof IntType);
        assertTrue(method1.returnType() instanceof BooleanType);

        AbsMethodDef method2 = trait.methods().get(1);
        assertEquals("neq", method2.name());
        assertEquals(1, method2.params().size());
        assertEquals("x", method2.params().get(0).name());
        assertTrue(method2.params().get(0).type() instanceof IntType);
        assertTrue(method2.returnType() instanceof BooleanType);
    }


    // impldef ::= `impl` traitname `for` type `{` conc_methoddef* `}`
    @Test
    void testImplDefParsesConcMethod() throws ParseException {
        Token[] tokens = {
            new ImplToken(),
            new IdentifierToken("Eq"),
            new ForToken(),
            new IdentifierToken("Point"),   // Struct name as type
            new LCurlyToken(),

            // method eq(x: Int): Boolean { }
            new MethodToken(),
            new IdentifierToken("eq"),
            new LParenToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new RParenToken(),
            new ColonToken(),
            new BooleanToken(),
            new LCurlyToken(), // empty body
            new RCurlyToken(),

            new RCurlyToken()
        };

        Parser parser = new Parser(tokens);
        ParseResult<ImplDef> result = parser.implDef(0);
        ImplDef impl = result.result();

        assertEquals("Eq", impl.traitName());
        assertTrue(impl.forType() instanceof StructType);
        assertEquals("Point", ((StructType) impl.forType()).name());
        assertEquals(1, impl.methods().size());

        ConcMethodDef method = impl.methods().get(0);
        assertEquals("eq", method.name());
        assertEquals(1, method.params().size());
        assertEquals("x", method.params().get(0).name());
        assertTrue(method.params().get(0).type() instanceof IntType);
        assertTrue(method.returnType() instanceof BooleanType);
        assertEquals(0, method.body().size()); // Empty b
    }
    
    // funcdef ::= `func` var `(` comma_param `)` `:` type
    //             `{` stmt* `}`

    @Test
    void testFuncDef() throws ParseException {
        Token[] tokens = {
            new FuncToken(),                        // func
            new IdentifierToken("add"),             // add
            new LParenToken(),                      // (
            new IdentifierToken("x"),               // x
            new ColonToken(),                       // :
            new IntToken(),                         // Int
            new CommaToken(),                       // ,
            new IdentifierToken("y"),               // y
            new ColonToken(),                       // :
            new IntToken(),                         // Int
            new RParenToken(),                      // )
            new ColonToken(),                       // :
            new IntToken(),                         // Int
            new LCurlyToken(),                      // {
            new ReturnToken(),                      // return
            new IdentifierToken("x"),               // x
            new SemicolonToken(),                   // ;
            new RCurlyToken()                       // }
        };
    
        Parser parser = new Parser(tokens);
        ParseResult<FuncDef> result = parser.funcDef(0);
        FuncDef func = result.result();
    
        assertEquals("add", func.name());
        assertEquals(2, func.params().size());
    
        Param p1 = func.params().get(0);
        assertEquals("x", p1.name());
        assertTrue(p1.type() instanceof IntType);
    
        Param p2 = func.params().get(1);
        assertEquals("y", p2.name());
        assertTrue(p2.type() instanceof IntType);
    
        assertTrue(func.returnType() instanceof IntType);
    
        assertEquals(1, func.body().size());
        Stmt stmt = func.body().get(0);
        assertTrue(stmt instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) stmt;
        assertTrue(returnStmt.value().isPresent());
        assertTrue(returnStmt.value().get() instanceof VarExp);
        assertEquals("x", ((VarExp) returnStmt.value().get()).name());
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
    @Test
    void testLetStatement() throws ParseException {
        Token[] tokens = {
            new LetToken(),
            new IdentifierToken("x"),
            new ColonToken(),
            new IntToken(),
            new EqualsToken(),
            new IntegerLiteralToken(42),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        LetStmt stmt = (LetStmt) result.result();
        assertEquals("x", stmt.name());
        assertTrue(stmt.type() instanceof IntType);
        assertEquals(42, ((IntLiteral) stmt.rhs()).value());
        assertEquals(result.nextPos(), tokens.length);  // Ensure we have parsed all tokens
    }

    @Test
    void testVarAssignment() throws ParseException {
        Token[] tokens = {
            new IdentifierToken("y"),
            new EqualsToken(),
            new IntegerLiteralToken(10),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        AssignStmt stmt = (AssignStmt) result.result();
        assertEquals("y", stmt.name());
        assertEquals(10, ((IntLiteral) stmt.value()).value());
        assertEquals(result.nextPos(), tokens.length);  // Ensure we have parsed all tokens
    }

    
    @Test
    void testIfStatementWithElse() throws ParseException {
        Token[] tokens = {
            new IfToken(),
            new LParenToken(),
            new IntegerLiteralToken(1),
            new RParenToken(),
            new IdentifierToken("stmt1"),
            new SemicolonToken(), // Fix: close the then-branch stmt
            new ElseToken(),
            new IdentifierToken("stmt2"),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        IfStmt stmt = (IfStmt) result.result();

        assertTrue(stmt.condition() instanceof IntLiteral);
        assertEquals(1, ((IntLiteral) stmt.condition()).value());

        assertTrue(stmt.thenBranch() instanceof ExpStmt);
        assertTrue(stmt.elseBranch().isPresent());
        assertTrue(stmt.elseBranch().get() instanceof ExpStmt);
    }

    @Test
    void testWhileStatement() throws ParseException {
        Token[] tokens = {
            new WhileToken(),
            new LParenToken(),
            new IntegerLiteralToken(5),
            new RParenToken(),
            new IdentifierToken("stmt"),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        WhileStmt stmt = (WhileStmt) result.result();
        assertEquals(5, ((IntLiteral) stmt.condition()).value());
        assertNotNull(stmt.body());
    }

    @Test
    void testBreakStatement() throws ParseException {
        Token[] tokens = {
            new BreakToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        BreakStmt stmt = (BreakStmt) result.result();
        assertNotNull(stmt);  // Ensure it's correctly parsed
    }

    @Test
    void testPrintlnStatement() throws ParseException {
        Token[] tokens = {
            new PrintlnToken(),
            new LParenToken(),
            new IntegerLiteralToken(100),
            new RParenToken(),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);

        ParseResult<Stmt> result = parser.stmt(0);
        PrintlnStmt stmt = (PrintlnStmt) result.result();
        assertEquals(100, ((IntLiteral) stmt.expression()).value());
    }

    @Test
    void testReturnStatement() throws ParseException {
        Token[] tokens = {
            new ReturnToken(),
            new IntegerLiteralToken(42),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
    
        ParseResult<Stmt> result = parser.stmt(0);
        ReturnStmt stmt = (ReturnStmt) result.result();
        assertTrue(stmt.value().isPresent());
        assertEquals(42, ((IntLiteral) stmt.value().get()).value());
    }


    @Test
    void testBlockStatement() throws ParseException {
        Token[] tokens = {
            new LCurlyToken(),
            new IdentifierToken("stmt1"),
            new SemicolonToken(),
            new IdentifierToken("stmt2"),
            new SemicolonToken(),
            new RCurlyToken()
        };
        Parser parser = new Parser(tokens);
    
        ParseResult<Stmt> result = parser.stmt(0);
        BlockStmt stmt = (BlockStmt) result.result();
        assertEquals(2, stmt.statements().size());  // There should be two statements in the block
    }


    @Test
    void testExpressionStatement() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(99),
            new SemicolonToken()
        };
        Parser parser = new Parser(tokens);
    
        ParseResult<Stmt> result = parser.stmt(0);
        ExpStmt stmt = (ExpStmt) result.result();
        assertEquals(99, ((IntLiteral) stmt.expression()).value());
    }

    // struct_actual_param ::= var `:` exp
    @Test
    void testStructActualParam() throws ParseException {
        Token[] tokens = {
            new IdentifierToken("x"),    // x
            new ColonToken(),            // :
            new IntegerLiteralToken(42)      // 42
        };
    
        Parser parser = new Parser(tokens);
        ParseResult<StructActualParam> result = parser.structActualParam(0);
        StructActualParam param = result.result();
    
        assertEquals("x", param.var());
        assertTrue(param.value() instanceof IntLiteral);
        assertEquals(42, ((IntLiteral) param.value()).value());
    
        assertEquals(3, result.nextPos());  // Should consume all 3 tokens
    }

    // struct_actual_params ::=
    // [struct_actual_param (`,` struct_actual_param)*]
    @Test
    void testStructActualParams_MultipleParams() throws ParseException {
        Token[] tokens = {
            new IdentifierToken("x"), new ColonToken(), new IntegerLiteralToken(1),
            new CommaToken(),
            new IdentifierToken("y"), new ColonToken(), new IntegerLiteralToken(2)
        };
    
        Parser parser = new Parser(tokens);
        ParseResult<List<StructActualParam>> result = parser.structActualParams(0);
        List<StructActualParam> params = result.result();
    
        assertEquals(2, params.size());
    
        // First param: x: 1
        assertEquals("x", params.get(0).var());
        assertTrue(params.get(0).value() instanceof IntLiteral);
        assertEquals(1, ((IntLiteral) params.get(0).value()).value());
    
        // Second param: y: 2
        assertEquals("y", params.get(1).var());
        assertTrue(params.get(1).value() instanceof IntLiteral);
        assertEquals(2, ((IntLiteral) params.get(1).value()).value());
    
        assertEquals(7, result.nextPos()); // All tokens consumed
    }
    
    @Test
    void testStructActualParams_EmptyList() throws ParseException {
        Token[] tokens = {}; // Empty input
        Parser parser = new Parser(tokens);
    
        ParseResult<List<StructActualParam>> result = parser.structActualParams(0);
        List<StructActualParam> params = result.result();
    
        assertTrue(params.isEmpty());
        assertEquals(0, result.nextPos());
    }



    @Test
    public void testPrimaryExpIntegerLiteral() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new IntegerLiteralToken(42)
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new IntLiteral(42), result.result());
    }
    
    @Test
    void testPrimaryExpVariable() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new IdentifierToken("x")
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new VarExp("x"), result.result());
    }
    
    @Test
    void testPrimaryExpTrueLiteral() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new TrueToken()
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new BoolLiteral(true), result.result());
    }
    
    @Test
    void testPrimaryExpFalseLiteral() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new FalseToken()
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new BoolLiteral(false), result.result());
    }
    
    @Test
    void testPrimaryExpSelf() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new SelfToken()
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new SelfExp(), result.result());
    }
    
    @Test
    void testPrimaryExpParenExp() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new LParenToken(),
            new IntegerLiteralToken(1),
            new RParenToken()
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        assertEquals(new ParenExp(new IntLiteral(1)), result.result());
    }
    
    @Test
    void testPrimaryExpNewStruct() throws ParseException {
        Parser parser = new Parser(new Token[] {
            new NewToken(),
            new IdentifierToken("Point"),
            new LCurlyToken(),
            new IdentifierToken("x"), new ColonToken(), new IntegerLiteralToken(1),
            new CommaToken(),
            new IdentifierToken("y"), new ColonToken(), new IntegerLiteralToken(2),
            new RCurlyToken()
        });
        ParseResult<Exp> result = parser.primaryExp(0);
        List<StructActualParam> expectedParams = List.of(
            new StructActualParam("x", new IntLiteral(1)),
            new StructActualParam("y", new IntLiteral(2))
        );
        assertEquals(new NewExp("Point", expectedParams), result.result());
    }
    
    // dot_exp ::= primary_exp (`.` var)*

    // @Test
    // public void testDotExp_singlePrimaryExp() throws ParseException {
    //     // We are creating an array of tokens for: "obj.field"
    //     Token[] tokens = new Token[]{
    //         new IdentifierToken("obj"),  // Primary expression (base)
    //         new DotToken(),              // Dot to access field
    //         new IdentifierToken("field") // Field after the dot
    //     };

    //     // Create the parser with the token array
    //     Parser parser = new Parser(tokens);
        
    //     // Parse starting from position 0
    //     ParseResult<Exp> result = parser.dotExp(0); 

    //     // Validate the result
    //     assertNotNull(result);
    //     assertTrue(result.result() instanceof DotExp);

    //     // Cast the result and validate the base and field
    //     DotExp dotExp = (DotExp) result.result();
    //     assertTrue(dotExp.base() instanceof VarExp);
    //     assertEquals("obj", ((VarExp) dotExp.base()).name());
    //     assertTrue(dotExp.field() instanceof VarExp);
    //     assertEquals("field", ((VarExp) dotExp.field()).name());
    // }
    

    // @Test
    // public void testDotExp_multipleFields() throws ParseException {
    //     Token[] tokens = new Token[]{
    //         new IdentifierToken("obj"),     // Primary expression (base)
    //         new DotToken(),                 // Dot to access first field
    //         new IdentifierToken("field1"),  // First field
    //         new DotToken(),                 // Dot to access second field
    //         new IdentifierToken("field2")   // Second field
    //     };
    
    //     Parser parser = new Parser(tokens);
    //     ParseResult<Exp> result = parser.dotExp(0); // Start at the first token
    
    //     assertNotNull(result);
    //     assertTrue(result.result() instanceof DotExp);
        
    //     DotExp firstDotExp = (DotExp) result.result();
    //     assertTrue(firstDotExp.base() instanceof VarExp);  // The base should be a VarExp
    //     assertEquals("obj", ((VarExp) firstDotExp.base()).name());
    
    //     // Now check the second dot expression
    //     assertTrue(firstDotExp.field() instanceof DotExp); // The field should be a DotExp for chaining
    //     DotExp secondDotExp = (DotExp) firstDotExp.field();
    //     assertTrue(secondDotExp.base() instanceof VarExp);
    //     assertEquals("field1", ((VarExp) secondDotExp.base()).name());
    //     assertEquals("field2", ((VarExp) secondDotExp.field()).name());
    // }


    @Test
    public void testDotExp_invalidDot() {
        // Test case where the dot operator appears in an invalid place (e.g., no primary expression before it).
        Parser parser = new Parser(new Token[] {
            new DotToken(), 
            new IdentifierToken("x")
        });
    
        assertThrows(ParseException.class, () -> {
            parser.dotExp(0);
        });
    }
    
    @Test
    public void testDotExp_dotAfterNonIdentifier() throws ParseException {
        // Test case where the first token isn't a valid primary expression (e.g., integer literal), so no dot expression is formed.
        Parser parser = new Parser(new Token[] {
            new IntegerLiteralToken(42),
            new DotToken(),
            new IdentifierToken("x")
        });
    
        assertThrows(ParseException.class, () -> {
            parser.dotExp(0);
        });
    }

    // comma_exp ::= [exp (`,` exp)*]
    @Test
    void testCommaExp_singleExpression() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(42) // Single expression without commas
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<List<Exp>> result = parser.commaExp(0);
        
        assertEquals(1, result.result().size()); // We expect one expression
        assertTrue(result.result().get(0) instanceof IntLiteral); // The first expression should be an integer literal
        assertEquals(1, result.nextPos()); // The next position should be after the single token
    }

    @Test
    void testCommaExp_multipleExpressions() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(42), // First expression
            new CommaToken(),            // Comma separating the expressions
            new IntegerLiteralToken(17)  // Second expression
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<List<Exp>> result = parser.commaExp(0);
        
        assertEquals(2, result.result().size()); // We expect two expressions
        assertTrue(result.result().get(0) instanceof IntLiteral); // First expression is an integer
        assertTrue(result.result().get(1) instanceof IntLiteral); // Second expression is also an integer
        assertEquals(3, result.nextPos()); // Position should be after the last token (comma + two expressions)
    }


    // multExp ::= primaryExp ((* | /) primaryExp)*
    @Test
    void testMultExp_singleExpression() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(42) // Single expression without any operators
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.multExp(0);
        
        assertTrue(result.result() instanceof IntLiteral); // The result should be an integer literal
        assertEquals(1, result.nextPos()); // Position should be after the single token
    }

    @Test
    void testMultExp_multiplication() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(42),  // First expression
            new StarToken(),              // Multiplication operator
            new IntegerLiteralToken(17)   // Second expression
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.multExp(0);
        
        assertTrue(result.result() instanceof BinOpExp); // The result should be a binary operation expression
        assertTrue(((BinOpExp) result.result()).op() instanceof MulOp); // The operator should be multiplication
        assertEquals(3, result.nextPos()); // Position should be after the two expressions and the operator
    }

    @Test
    void testMultExp_division() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(42),  // First expression
            new DivToken(),               // Division operator
            new IntegerLiteralToken(17)   // Second expression
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.multExp(0);
        
        assertTrue(result.result() instanceof BinOpExp); // The result should be a binary operation expression
        assertTrue(((BinOpExp) result.result()).op() instanceof DivOp); // The operator should be division
        assertEquals(3, result.nextPos()); // Position should be after the two expressions and the operator
    }



    // addExp ::= multExp ((+ | -) multExp)*
    @Test
    void testAddExp_singleAddition() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(2),    // First expression (multExp)
            new StarToken(),               // Multiplication operator
            new IntegerLiteralToken(3),    // Second expression (multExp)
            new PlusToken(),               // Addition operator
            new IntegerLiteralToken(4)     // Third expression (multExp)
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.addExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
        BinOpExp firstOp = (BinOpExp) result.result();
        assertTrue(firstOp.op() instanceof AddOp);  // The operator should be addition
        assertTrue(firstOp.left() instanceof BinOpExp);   // The left operand should be a BinOpExp
        
        // Check if the left operand of the first BinOpExp is a multiplication
        BinOpExp leftOp = (BinOpExp) firstOp.left();
        assertTrue(leftOp.op() instanceof MulOp);   // The operator of the left operand should be multiplication
    }

    // @Test
    // void testAddExp_additionAndSubtraction() throws ParseException {
    //     Token[] tokens = new Token[] {
    //         new IntegerLiteralToken(2),    // First expression (multExp)
    //         new StarToken(),               // Multiplication operator
    //         new IntegerLiteralToken(3),    // Second expression (multExp)
    //         new PlusToken(),               // Addition operator
    //         new IntegerLiteralToken(4),    // Third expression (multExp)
    //         new MinusToken(),              // Subtraction operator
    //         new IntegerLiteralToken(5)     // Fourth expression (multExp)
    //     };
    //     Parser parser = new Parser(tokens);
        
    //     ParseResult<Exp> result = parser.addExp(0);
        
    //     assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
    //     BinOpExp firstOp = (BinOpExp) result.result();
    //     assertTrue(firstOp.op() instanceof AddOp);  // The operator should be addition
        
    //     // Check the left operand of the first BinOpExp, it should be a BinOpExp for multiplication
    //     BinOpExp leftOp = (BinOpExp) firstOp.left();
    //     assertTrue(leftOp.op() instanceof MulOp);   // The operator of the left operand should be multiplication
        
    //     // Check the right operand of the first BinOpExp, it should be another BinOpExp (for subtraction)
    //     assertTrue(firstOp.right() instanceof BinOpExp);
    //     BinOpExp secondOp = (BinOpExp) firstOp.right();
    //     assertTrue(secondOp.op() instanceof SubOp); // The operator of the second BinOpExp should be subtraction
    // }


    @Test
    void testAddExp_noAdditionsOrSubtractions() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(2),    // First expression (multExp)
            new StarToken(),               // Multiplication operator
            new IntegerLiteralToken(3)     // Second expression (multExp)
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.addExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
        BinOpExp firstOp = (BinOpExp) result.result();
        assertTrue(firstOp.op() instanceof MulOp);  // The operator should be multiplication
    }

    // @Test
    // void testAddExp_multipleAdditions() throws ParseException {
    //     Token[] tokens = new Token[] {
    //         new IntegerLiteralToken(2),    // First expression (multExp)
    //         new StarToken(),               // Multiplication operator
    //         new IntegerLiteralToken(3),    // Second expression (multExp)
    //         new PlusToken(),               // Addition operator
    //         new IntegerLiteralToken(4),    // Third expression (multExp)
    //         new PlusToken(),               // Another addition operator
    //         new IntegerLiteralToken(5)     // Fourth expression (multExp)
    //     };
    //     Parser parser = new Parser(tokens);
        
    //     ParseResult<Exp> result = parser.addExp(0);
        
    //     assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
    //     BinOpExp firstOp = (BinOpExp) result.result();
    //     assertTrue(firstOp.op() instanceof AddOp);  // The operator should be addition
        
    //     // Check the right operand of the first BinOpExp, it should be another BinOpExp
    //     assertTrue(firstOp.right() instanceof BinOpExp);
    //     BinOpExp secondOp = (BinOpExp) firstOp.right();
    //     assertTrue(secondOp.op() instanceof AddOp); // The operator of the second BinOpExp should be addition
    // }


    //less_than_exp ::= add_exp [`<` add_exp]
    @Test
    void testLessThanExp_singleLessThan() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(2),    // First expression (addExp)
            new PlusToken(),               // Addition operator
            new IntegerLiteralToken(3),    // Second expression (addExp)
            new LessThanToken(),           // Less-than operator
            new IntegerLiteralToken(5)     // Third expression (addExp)
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.lessThanExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
        BinOpExp firstOp = (BinOpExp) result.result();
        assertTrue(firstOp.op() instanceof LessThanOp);  // The operator should be less-than
        assertTrue(firstOp.left() instanceof BinOpExp);         // The left operand should be a BinOpExp for addition
    }


    @Test
    void testLessThanExp_noLessThan() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(2),    // First expression (addExp)
            new PlusToken(),               // Addition operator
            new IntegerLiteralToken(3)     // Second expression (addExp)
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.lessThanExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
        BinOpExp firstOp = (BinOpExp) result.result();
        assertTrue(firstOp.op() instanceof AddOp);  // The operator should be addition, as no less-than was found
    }

    // @Test
    // void testLessThanExp_multipleLessThan() throws ParseException {
    //     Token[] tokens = new Token[] {
    //         new IntegerLiteralToken(2),    // First expression (addExp)
    //         new PlusToken(),               // Addition operator
    //         new IntegerLiteralToken(3),    // Second expression (addExp)
    //         new LessThanToken(),           // Less-than operator
    //         new IntegerLiteralToken(5),    // Third expression (addExp)
    //         new LessThanToken(),           // Another less-than operator
    //         new IntegerLiteralToken(6)     // Fourth expression (addExp)
    //     };
    //     Parser parser = new Parser(tokens);
        
    //     ParseResult<Exp> result = parser.lessThanExp(0);
        
    //     assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
    //     BinOpExp firstOp = (BinOpExp) result.result();
    //     assertTrue(firstOp.op() instanceof LessThanOp);  // The operator of the first BinOpExp should be less-than
        
    //     // Check the right operand of the first BinOpExp, it should be another BinOpExp
    //     assertTrue(firstOp.right() instanceof BinOpExp);
    //     BinOpExp secondOp = (BinOpExp) firstOp.right();
    //     assertTrue(secondOp.op() instanceof LessThanOp); // The operator of the second BinOpExp should be less-than
    // }


    @Test
    void testLessThanExp_emptyExpression() throws ParseException {
        Token[] tokens = new Token[] {
            new IntegerLiteralToken(2),    // First expression (addExp)
            new LessThanToken(),           // Less-than operator
            new IntegerLiteralToken(3)     // Second expression (addExp)
        };
        Parser parser = new Parser(tokens);
        
        ParseResult<Exp> result = parser.lessThanExp(0);
        
        assertTrue(result.result() instanceof BinOpExp);  // The result should be a BinOpExp (binary operation)
        
        BinOpExp firstOp = (BinOpExp) result.result();
        assertTrue(firstOp.op() instanceof LessThanOp);  // The operator should be less-than
    }
    

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///
    @Test
    void testEqualsExpWithEqualsToken() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(3),
            new EqualsToken(),
            new IntegerLiteralToken(5)
        };

        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.equalsExp(0);

        assertEquals(3, result.nextPos());
        assertTrue(result.result() instanceof BinOpExp);

        BinOpExp binExp = (BinOpExp) result.result();

        assertTrue(binExp.left() instanceof IntLiteral);
        assertTrue(binExp.right() instanceof IntLiteral);
        assertTrue(binExp.op() instanceof EqOp);

        IntLiteral left = (IntLiteral) binExp.left();
        IntLiteral right = (IntLiteral) binExp.right();

        assertEquals(3, left.value());
        assertEquals(5, right.value());
    }

    @Test
    void testEqualsExpWithNotEqualsToken() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(4),
            new NEqualsToken(),
            new IntegerLiteralToken(2)
        };

        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.equalsExp(0);

        assertEquals(3, result.nextPos());
        assertTrue(result.result() instanceof BinOpExp);

        BinOpExp binExp = (BinOpExp) result.result();

        assertTrue(binExp.op() instanceof NeqOp);

        IntLiteral left = (IntLiteral) binExp.left();
        IntLiteral right = (IntLiteral) binExp.right();

        assertEquals(4, left.value());
        assertEquals(2, right.value());
    }

    @Test
    public void testThrowsParseExceptionOnInvalidMethodName() {
       // Arrange
       Token[] tokens = {
           new MethodToken(),
           new IntegerToken(42)    
       };
       Parser parser = new Parser(tokens);

       ParseException thrown = assertThrows(ParseException.class, () -> {
           parser.concMethodDef(0);
       });


       assertTrue(thrown.getMessage().contains("Expected method name at position 1"));
    }

    @Test
    public void testThrowsParseExceptionOnInvalidTraitName() {
        Token[] tokens = {
            new TraitToken(),    
            new IntegerToken(42) 
        };
        Parser parser = new Parser(tokens);

        ParseException thrown = assertThrows(ParseException.class, () -> {
            parser.traitDef(0);
        });

        assertTrue(thrown.getMessage().contains("Expected trait name at position 1"));
    }

    @Test
    public void testThrowsParseExceptionOnInvalidTraitNameInImplDef() {
        // Arrange
        Token[] tokens = {
            new ImplToken(),        // pos 0
            new IntegerToken(42)    // pos 1 - invalid trait name (should be IdentifierToken)
        };
        Parser parser = new Parser(tokens);

        // Act & Assert
        ParseException thrown = assertThrows(ParseException.class, () -> {
            parser.implDef(0);
        });

        assertTrue(thrown.getMessage().contains("Expected trait name at position 1"));
    }

    @Test
    public void testThrowsParseExceptionOnInvalidFunctionNameInFuncDef() {
        Token[] tokens = {
            new FuncToken(),         
            new IntegerToken(42)   
        };
        Parser parser = new Parser(tokens);

        ParseException thrown = assertThrows(ParseException.class, () -> {
            parser.funcDef(0);
        });

        assertTrue(thrown.getMessage().contains("Expected function name at position 1"));
    }

    @Test
    void testMultExpWithDivToken() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(8),
            new DivToken(),
            new IntegerLiteralToken(2)
        };

        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.multExp(0);

        assertNotNull(result);
        assertEquals(3, result.nextPos()); // All tokens should be consumed

        assertTrue(result.result() instanceof BinOpExp);

        BinOpExp binExp = (BinOpExp) result.result();
        assertTrue(binExp.left() instanceof IntLiteral);
        assertTrue(binExp.right() instanceof IntLiteral);
        assertTrue(binExp.op() instanceof DivOp);

        IntLiteral left = (IntLiteral) binExp.left();
        IntLiteral right = (IntLiteral) binExp.right();

        assertEquals(8, left.value());
        assertEquals(2, right.value());
    }  

    @Test
    void testLessThanExp() throws ParseException {
        Token[] tokens = {
            new IntegerLiteralToken(3),
            new LessThanToken(),
            new IntegerLiteralToken(5)
        };

        Parser parser = new Parser(tokens);
        ParseResult<Exp> result = parser.lessThanExp(0);

        // Ensure the whole input was consumed
        assertEquals(3, result.nextPos());

        // Ensure we parsed a binary expression
        assertTrue(result.result() instanceof BinOpExp);

        BinOpExp binExp = (BinOpExp) result.result();

        // Check structure
        assertTrue(binExp.left() instanceof IntLiteral);
        assertTrue(binExp.right() instanceof IntLiteral);
        assertTrue(binExp.op() instanceof LessThanOp);

        IntLiteral left = (IntLiteral) binExp.left();
        IntLiteral right = (IntLiteral) binExp.right();

        assertEquals(3, left.value());
        assertEquals(5, right.value());
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    // @Test
    

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
