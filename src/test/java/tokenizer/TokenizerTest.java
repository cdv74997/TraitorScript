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
}