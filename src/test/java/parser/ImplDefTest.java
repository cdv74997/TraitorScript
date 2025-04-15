package parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImplDefTest {

    @Test
    void testImplDefWithMethods() {
        Param param1 = new Param("x", new IntType());
        ConcMethodDef method1 = new ConcMethodDef("add", List.of(param1), new IntType(), List.of());

        Param param2 = new Param("flag", new BooleanType());
        ConcMethodDef method2 = new ConcMethodDef("check", List.of(param2), new BooleanType(), List.of());

        ImplDef impl = new ImplDef("TraitName", new StructType("MyStruct"), List.of(method1, method2));

        assertEquals("TraitName", impl.traitName());
        assertTrue(impl.forType() instanceof StructType);
        assertEquals("MyStruct", ((StructType) impl.forType()).name());
        assertEquals(2, impl.methods().size());
        assertEquals("add", impl.methods().get(0).name());
        assertEquals("check", impl.methods().get(1).name());
    }

    @Test
    void testImplDefEmptyMethods() {
        ImplDef impl = new ImplDef("EmptyTrait", new StructType("EmptyStruct"), List.of());

        assertEquals("EmptyTrait", impl.traitName());
        assertTrue(impl.forType() instanceof StructType);
        assertEquals("EmptyStruct", ((StructType) impl.forType()).name());
        assertTrue(impl.methods().isEmpty());
    }
}
