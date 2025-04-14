package parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParamTest {

    @Test
    void testParamConstruction() {
        Type type = new IntType();
        Param param = new Param("x", type);

        assertEquals("x", param.name());
        assertEquals(type, param.type());
    }
}
