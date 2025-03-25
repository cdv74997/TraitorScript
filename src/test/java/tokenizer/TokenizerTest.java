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
}