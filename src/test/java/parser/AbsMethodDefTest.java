package parser;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AbsMethodDefTest {

    @Test
    void testAbstractMethodDefinition() {
        List<Param> params = List.of(new Param("x", new IntType()));
        AbsMethodDef method = new AbsMethodDef("foo", params, new BooleanType());

        assertEquals("foo", method.name());
        assertEquals(1, method.params().size());
        assertTrue(method.returnType() instanceof BooleanType);
    }
}
