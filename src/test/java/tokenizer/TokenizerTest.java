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
    public void testSingleDigitIntegerToken(){
        final Tokenizer tokenizer = new Tokenizer("2");
        assertEquals(Optional.of(new IntegerToken(2)),
                     tokenizer.tryReadInteger());
    }

    @Test
    public void testDoubleDigitIntegerToken(){
        final Tokenizer tokenizer = new Tokenizer("24");
        assertEquals(Optional.of(new IntegerToken(24)),
                     tokenizer.tryReadInteger());
    }

    @Test
    public void testPrintlnToken(){
        final Tokenizer tokenizer = new Tokenizer("println");
        assertEquals(Optional.of(new PrintlnToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testVoidToken(){
        final Tokenizer tokenizer = new Tokenizer("void");
        assertEquals(Optional.of(new VoidToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testSelfToken(){
        final Tokenizer tokenizer = new Tokenizer("self");
        assertEquals(Optional.of(new SelfToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testStructToken(){
        final Tokenizer tokenizer = new Tokenizer("struct");
        assertEquals(Optional.of(new StructToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testMethodToken(){
        final Tokenizer tokenizer = new Tokenizer("method");
        assertEquals(Optional.of(new MethodToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testTraitToken(){
        final Tokenizer tokenizer = new Tokenizer("trait");
        assertEquals(Optional.of(new TraitToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testImplToken(){
        final Tokenizer tokenizer = new Tokenizer("impl");
        assertEquals(Optional.of(new ImplToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testForToken(){
        final Tokenizer tokenizer = new Tokenizer("for");
        assertEquals(Optional.of(new ForToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testFuncToken(){
        final Tokenizer tokenizer = new Tokenizer("func");
        assertEquals(Optional.of(new FuncToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testLetToken(){
        final Tokenizer tokenizer = new Tokenizer("let");
        assertEquals(Optional.of(new LetToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testIfToken(){
        final Tokenizer tokenizer = new Tokenizer("if");
        assertEquals(Optional.of(new IfToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testElseToken(){
        final Tokenizer tokenizer = new Tokenizer("else");
        assertEquals(Optional.of(new ElseToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testWhileToken(){
        final Tokenizer tokenizer = new Tokenizer("while");
        assertEquals(Optional.of(new WhileToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testBreakToken(){
        final Tokenizer tokenizer = new Tokenizer("break");
        assertEquals(Optional.of(new BreakToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testReturnToken(){
        final Tokenizer tokenizer = new Tokenizer("return");
        assertEquals(Optional.of(new ReturnToken()),
                     tokenizer.tryReadReservedWord());
    }

    @Test
    public void testNewToken(){
        final Tokenizer tokenizer = new Tokenizer("new");
        assertEquals(Optional.of(new NewToken()),
                     tokenizer.tryReadReservedWord());
    }
}