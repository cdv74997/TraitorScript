package parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TraitDefTest {

    @Test
    void testTraitDefWithMethods() {
        Param param1 = new Param("x", new IntType());
        AbsMethodDef method1 = new AbsMethodDef("foo", List.of(param1), new IntType());

        Param param2 = new Param("flag", new BooleanType());
        AbsMethodDef method2 = new AbsMethodDef("bar", List.of(param2), new BooleanType());

        TraitDef trait = new TraitDef("MyTrait", List.of(method1, method2));

        assertEquals("MyTrait", trait.name());
        assertEquals(2, trait.methods().size());
        assertEquals("foo", trait.methods().get(0).name());
        assertTrue(trait.methods().get(1).returnType() instanceof BooleanType);
    }

    @Test
    void testTraitDefEmptyMethods() {
        TraitDef trait = new TraitDef("EmptyTrait", List.of());

        assertEquals("EmptyTrait", trait.name());
        assertTrue(trait.methods().isEmpty());
    }
}
