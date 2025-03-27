package tokenizer;

import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class TokenizerTest{
    @Test
    public void testEmpty() {
        final Tokenizer tokenizer = new Tokenizer("");
        tokenizer.skipWhitespace();
        assertEquals(0, tokenizer.getPosition());
    }

    @Test
    public void testSingleWhitespace() {
        final Tokenizer tokenizer = new Tokenizer(" ");
        tokenizer.skipWhitespace();
        assertEquals(1, tokenizer.getPosition());
    }

    @Test
    public void testSingleWhitespaceAndA() {
        final Tokenizer tokenizer = new Tokenizer(" a");
        tokenizer.skipWhitespace();
        assertEquals(1, tokenizer.getPosition());
    }

    @Test
    public void testReadCommaToken(){
        final Tokenizer tokenizer = new Tokenizer(",");
        assertEquals(Optional.of(new CommaToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadLParenToken(){
        final Tokenizer tokenizer = new Tokenizer("(");
        assertEquals(Optional.of(new LParenToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadRParenToken(){
        final Tokenizer tokenizer = new Tokenizer(")");
        assertEquals(Optional.of(new RParenToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadColonToken(){
        final Tokenizer tokenizer = new Tokenizer(":");
        assertEquals(Optional.of(new ColonToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadLCurlyToken(){
        final Tokenizer tokenizer = new Tokenizer("{");
        assertEquals(Optional.of(new LCurlyToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadRCurlyToken(){
        final Tokenizer tokenizer = new Tokenizer("}");
        assertEquals(Optional.of(new RCurlyToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadSemicolonToken(){
        final Tokenizer tokenizer = new Tokenizer(";");
        assertEquals(Optional.of(new SemicolonToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadDotToken(){
        final Tokenizer tokenizer = new Tokenizer(".");
        assertEquals(Optional.of(new DotToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadStarToken(){
        final Tokenizer tokenizer = new Tokenizer("*");
        assertEquals(Optional.of(new StarToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadSlashToken(){
        final Tokenizer tokenizer = new Tokenizer("/");
        assertEquals(Optional.of(new SlashToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadPlusToken(){
        final Tokenizer tokenizer = new Tokenizer("+");
        assertEquals(Optional.of(new PlusToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadMinusToken(){
        final Tokenizer tokenizer = new Tokenizer("-");
        assertEquals(Optional.of(new MinusToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadLessThanToken(){
        final Tokenizer tokenizer = new Tokenizer("<");
        assertEquals(Optional.of(new LessThanToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadRArrowToken(){
        final Tokenizer tokenizer = new Tokenizer("=>");
        assertEquals(Optional.of(new RArrowToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadDEqualsToken(){
        final Tokenizer tokenizer = new Tokenizer("==");
        assertEquals(Optional.of(new DEqualsToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadNEqualsToken(){
        final Tokenizer tokenizer = new Tokenizer("!=");
        assertEquals(Optional.of(new NEqualsToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadEqualsToken(){
        final Tokenizer tokenizer = new Tokenizer("=");
        assertEquals(Optional.of(new EqualsToken()), tokenizer.tryReadSymbol());
    }

    @Test
    public void testReadEmptySymbolToken(){
        final Tokenizer tokenizer = new Tokenizer("");
        assertEquals(Optional.empty(), tokenizer.tryReadSymbol());
    }

    @Test
    public void testSingleDigitIntegerLiteralToken(){
        final Tokenizer tokenizer = new Tokenizer("2");
        assertEquals(Optional.of(new IntegerLiteralToken(2)),
                     tokenizer.tryReadInteger());
    }

    @Test
    public void testDoubleDigitIntegerLiteralToken(){
        final Tokenizer tokenizer = new Tokenizer("24");
        assertEquals(Optional.of(new IntegerLiteralToken(24)),
                     tokenizer.tryReadInteger());
    }

    @Test
    public void testReadEmptyIntegerToken(){
        final Tokenizer tokenizer = new Tokenizer("");
        assertEquals(Optional.empty(), tokenizer.tryReadInteger());
    }

    @Test
    public void testPrintlnToken(){
        final Tokenizer tokenizer = new Tokenizer("println");
        assertEquals(Optional.of(new PrintlnToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testVoidToken(){
        final Tokenizer tokenizer = new Tokenizer("Void");
        assertEquals(Optional.of(new VoidToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testSelfToken(){
        final Tokenizer tokenizer = new Tokenizer("Self");
        assertEquals(Optional.of(new SelfToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testStructToken(){
        final Tokenizer tokenizer = new Tokenizer("struct");
        assertEquals(Optional.of(new StructToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testMethodToken(){
        final Tokenizer tokenizer = new Tokenizer("method");
        assertEquals(Optional.of(new MethodToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testTraitToken(){
        final Tokenizer tokenizer = new Tokenizer("trait");
        assertEquals(Optional.of(new TraitToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testImplToken(){
        final Tokenizer tokenizer = new Tokenizer("impl");
        assertEquals(Optional.of(new ImplToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testForToken(){
        final Tokenizer tokenizer = new Tokenizer("for");
        assertEquals(Optional.of(new ForToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testFuncToken(){
        final Tokenizer tokenizer = new Tokenizer("func");
        assertEquals(Optional.of(new FuncToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testLetToken(){
        final Tokenizer tokenizer = new Tokenizer("let");
        assertEquals(Optional.of(new LetToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testIfToken(){
        final Tokenizer tokenizer = new Tokenizer("if");
        assertEquals(Optional.of(new IfToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testElseToken(){
        final Tokenizer tokenizer = new Tokenizer("else");
        assertEquals(Optional.of(new ElseToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testWhileToken(){
        final Tokenizer tokenizer = new Tokenizer("while");
        assertEquals(Optional.of(new WhileToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testBreakToken(){
        final Tokenizer tokenizer = new Tokenizer("break");
        assertEquals(Optional.of(new BreakToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testReturnToken(){
        final Tokenizer tokenizer = new Tokenizer("return");
        assertEquals(Optional.of(new ReturnToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testNewToken(){
        final Tokenizer tokenizer = new Tokenizer("new");
        assertEquals(Optional.of(new NewToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testIntToken(){
        final Tokenizer tokenizer = new Tokenizer("Int");
        assertEquals(Optional.of(new IntToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testBooleanLiteralTrueToken(){
        final Tokenizer tokenizer = new Tokenizer("true");
        assertEquals(Optional.of(new BooleanLiteralToken(true)),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testBooleanLiteralFalseToken(){
        final Tokenizer tokenizer = new Tokenizer("false");
        assertEquals(Optional.of(new BooleanLiteralToken(false)),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testBooleanToken(){
        final Tokenizer tokenizer = new Tokenizer("Boolean");
        assertEquals(Optional.of(new BooleanToken()),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testIdentifierToken(){
        final Tokenizer tokenizer = new Tokenizer("name");
        assertEquals(Optional.of(new IdentifierToken("name")),
                     tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testReadEmptyStringToken(){
        final Tokenizer tokenizer = new Tokenizer("");
        assertEquals(Optional.empty(), tokenizer.tryReadReservedWordOrIdentifier());
    }

    @Test
    public void testMultiTokenize() throws TokenizerException {
        // foo bar
        final Tokenizer tokenizer = new Tokenizer("foo bar");
        final ArrayList<Token> tokens = tokenizer.tokenize();
        // assertEquals(new IdentifierToken("foo"),
        //              tokens.get(0));
        // assertEquals(new IdentifierToken("bar"),
        //              tokens.get(1));

        assertArrayEquals(new Token[]{
                new IdentifierToken("foo"),
                new IdentifierToken("bar")
            },
            tokens.toArray());
    }
}