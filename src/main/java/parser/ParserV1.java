// package parser;

// import java.text.ParseException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;


// import tokenizer.DotToken;
// import tokenizer.EqualsToken;
// import tokenizer.IdentifierToken;
// import tokenizer.LParenToken;
// import tokenizer.LessThanToken;
// import tokenizer.PrintlnToken;
// import tokenizer.RParenToken;
// import tokenizer.ReturnToken;
// import tokenizer.SemicolonToken;
// import tokenizer.StarToken;
// import tokenizer.Token;

// public class ParserV1 {
//     private final Token[] tokens;

//     public ParserV1(final Token[] tokens) {
//         this.tokens = tokens;
//     }

//     // stmt ::= IDENTIFIER `=` exp `;` |
//     //          `print` exp `;` |
//     //          `return` [exp] `;`
//     public ParseResult<Stmt> stmt(final int startPos) throws ParseException {
//         final Token token = readToken(startPos);
//         if (token instanceof IdentifierToken id) {
//             String name = id.name();
//             assertTokenIs(startPos + 1, new EqualsToken());
//             ParseResult<Exp> expression = exp(startPos + 2);
//             assertTokenIs(expression.nextPos, new SemicolonToken());
//             AssignStmt assign = new AssignStmt(name, expression.result);
//             return new ParseResult<>(assign, expression.nextPos + 1);
//         } else if (token instanceof PrintlnToken) {
//             ParseResult<Exp> expression = exp(startPos + 1);
//             assertTokenIs(expression.nextPos, new SemicolonToken());
//             PrintlnStmt print = new PrintlnStmt(expression.result);
//             return new ParseResult<>(print, expression.nextPos + 1);
//         } else if (token instanceof ReturnToken) {
//             ParseResult<Optional<Exp>> opExpression;
//             try {
//                 ParseResult<Exp> expression = exp(startPos + 1);
//                 opExpression = new ParseResult<>(Optional.of(expression.result), expression.nextPos);
//             } catch (ParseException e) {
//                 opExpression = new ParseResult<>(Optional.empty(), startPos + 1);
//             }
//             assertTokenIs(opExpression.nextPos, new SemicolonToken());
//             return new ParseResult<>(new ReturnStmt(opExpression.result), opExpression.nextPos + 1);
//         } else {
//             throw new ParseException("Expected statement; got: " + token);
//         }
//     }

//     //1. exp ::= equalsExp
//     public ParseResult<Exp> exp(int startPos) throws ParseException {
//         return equalsExp(startPos);
//     }

//     //2. equalsExp ::= lessThanExp (("==" | "!=") lessThanExp)?
//     public ParseResult<Exp> equalsExp(int startPos) throws ParseException {
//         ParseResult<Exp> left = lessThanExp(startPos);
//         int pos = left.nextPos;
    
//         while (true) {
//             Token t;
//             try {
//                 t = readToken(pos);
//             } catch (ParseException e) {
//                 break;
//             }
    
//             Op op;
//             if (t instanceof EqualsEqualsToken) {
//                 op = new EqualsOp();
//             } else if (t instanceof NotEqualsToken) {
//                 op = new NotEqualsOp();
//             } else {
//                 break;
//             }
    
//             ParseResult<Exp> right = lessThanExp(pos + 1);
//             left = new ParseResult<>(new BinOpExp(left.result(), op, right.result()), right.nextPos());
//             pos = right.nextPos();
//         }
    
//         return left;
//     }

//     //3. lessThanExp ::= addExp ("<" addExp)?
//     public ParseResult<Exp> lessThanExp(int startPos) throws ParseException {
//     ParseResult<Exp> left = addExp(startPos);
//     int pos = left.nextPos;

//     try {
//         Token t = readToken(pos);
//         if (t instanceof LessThanToken) {
//             ParseResult<Exp> right = addExp(pos + 1);
//             return new ParseResult<>(new BinOpExp(left.result(), new LessThanOp(), right.result()), right.nextPos());
//         }
//     } catch (ParseException e) {
//         // do nothing; return left as is
//     }

//     return left;
//     }

//     //4. addExp ::= multExp (("+" | "-") multExp)*
//     public ParseResult<Exp> multExp(int startPos) throws ParseException {
//     ParseResult<Exp> left = callExp(startPos);
//     int pos = left.nextPos;

//     while (true) {
//         Token t;
//         try {
//             t = readToken(pos);
//         } catch (ParseException e) {
//             break;
//         }

//         Op op;
//         if (t instanceof StarToken) {
//             op = new MultOp();
//         } else if (t instanceof DivToken) {
//             op = new DivOp();
//         } else {
//             break;
//         }

//         ParseResult<Exp> right = callExp(pos + 1);
//         left = new ParseResult<>(new BinOpExp(left.result(), op, right.result()), right.nextPos());
//         pos = right.nextPos();
//     }

//     return left;
//     }

//     //5. multExp ::= callExp (("*" | "/") callExp)*
//     public ParseResult<Exp> multExp(int startPos) throws ParseException {
//         ParseResult<Exp> left = callExp(startPos);
//         int pos = left.nextPos;
    
//         while (true) {
//             Token t;
//             try {
//                 t = readToken(pos);
//             } catch (ParseException e) {
//                 break;
//             }
    
//             Op op;
//             if (t instanceof StarToken) {
//                 op = new MultOp();
//             } else if (t instanceof DivToken) {
//                 op = new DivOp();
//             } else {
//                 break;
//             }
    
//             ParseResult<Exp> right = callExp(pos + 1);
//             left = new ParseResult<>(new BinOpExp(left.result(), op, right.result()), right.nextPos());
//             pos = right.nextPos();
//         }
    
//         return left;
//     }
    
//     //6. callExp ::= dotExp ( "(" commaExp ")" )*
//     public ParseResult<Exp> callExp(int startPos) throws ParseException {
//     ParseResult<Exp> func = dotExp(startPos);
//     int pos = func.nextPos;

//     while (true) {
//         try {
//             Token t = readToken(pos);
//             if (!(t instanceof LParenToken)) break;

//             ParseResult<Exp> arg = exp(pos + 1); // basic single arg support
//             int next = arg.nextPos;
//             assertTokenIs(next, new RParenToken());

//             func = new ParseResult<>(new CallExp(func.result(), List.of(arg.result())), next + 1);
//             pos = func.nextPos;
//         } catch (ParseException e) {
//             break;
//         }
//     }

//     return func;
//     }

//     // dotExp ::= primaryExp ("." IDENTIFIER)*
//     public ParseResult<Exp> dotExp(int startPos) throws ParseException {
//     ParseResult<Exp> base = primaryExp(startPos);
//     int pos = base.nextPos;

//     while (true) {
//         try {
//             Token dot = readToken(pos);
//             if (!(dot instanceof DotToken)) break;

//             Token next = readToken(pos + 1);
//             if (!(next instanceof IdentifierToken id)) {
//                 throw new ParseException("Expected identifier after '.'");
//             }

//             base = new ParseResult<>(new FieldAccessExp(base.result(), id.name()), pos + 2);
//             pos = base.nextPos;
//         } catch (ParseException e) {
//             break;
//         }
//     }

//     return base;
//     }

//     //8. primaryExp ::= INT | ID | true | false | (exp)
//     public ParseResult<Exp> primaryExp(int startPos) throws ParseException {
//         Token t = readToken(startPos);
    
//         if (t instanceof IntegerToken i) {
//             return new ParseResult<>(new IntExp(i.value()), startPos + 1);
//         } else if (t instanceof IdentifierToken id) {
//             return new ParseResult<>(new IdExp(id.name()), startPos + 1);
//         } else if (t instanceof TrueToken) {
//             return new ParseResult<>(new BoolExp(true), startPos + 1);
//         } else if (t instanceof FalseToken) {
//             return new ParseResult<>(new BoolExp(false), startPos + 1);
//         } else if (t instanceof LParenToken) {
//             ParseResult<Exp> inner = exp(startPos + 1);
//             assertTokenIs(inner.nextPos, new RParenToken());
//             return new ParseResult<>(inner.result(), inner.nextPos() + 1);
//         } else {
//             throw new ParseException("Unexpected token in primaryExp: " + t);
//         }
//     }
    



    

//     // Other methods like exp(), assertTokenIs(), etc.

//     private Token readToken(final int pos) throws ParseException {
//         if (pos < 0 || pos >= tokens.length) {
//             throw new ParseException("Ran out of tokens", pos);
//         } else {
//             return tokens[pos];
//         }
//     }

//     private void assertTokenIs(final int pos, final Token expected) throws ParseException {
//         final Token received = readToken(pos);
//         if (!expected.equals(received)) {
//             throw new ParseException("Expected: " + expected + "; received: " + received, pos);
//         }
//     }

//     public ParseResult<Program> program(int startPos) throws ParseException {
//         List<ProgramItem> items = new ArrayList<>();
//         int pos = startPos;

//         // Parse zero or more top-level items
//         while (true) {
//             try {
//                 ParseResult<ProgramItem> itemRes = programItem(pos);
//                 items.add(itemRes.result);
//                 pos = itemRes.nextPos;
//             } catch (ParseException e) {
//                 break; // Done with program items
//             }
//         }

//         // Then expect zero or more statements
//         List<Stmt> stmts = new ArrayList<>();
//         while (true) {
//             try {
//                 ParseResult<Stmt> stmtRes = stmt(pos);
//                 stmts.add(stmtRes.result);
//                 pos = stmtRes.nextPos;
//             } catch (ParseException e) {
//                 break;
//             }
//         }

//         return new ParseResult<>(new Program(items, stmts), pos);
//     }

//     public Program parseWholeProgram() throws ParseException {
//         ParseResult<Program> result = program(0);
//         if (result.nextPos == tokens.length) {
//             return result.result;
//         } else {
//             throw new ParseException("Unexpected token at position " + result.nextPos, 0);
//         }
//     }

//     // Next up: programItem()...
// }

