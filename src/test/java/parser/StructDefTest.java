package parser;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class StructDefTest {

    @Test
    void testStructDefinition() {
        List<Param> fields = List.of(new Param("x", new IntType()), new Param("flag", new BooleanType()));
        StructDef structDef = new StructDef("Point", fields);

        assertEquals("Point", structDef.name());
        assertEquals(2, structDef.fields().size());
    }
}
