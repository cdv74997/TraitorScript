package parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ASTTests {

    @Test
    void testParam() {
        Type type = new IntType();
        Param param = new Param("x", type);

        assertEquals("x", param.name());
        assertEquals(type, param.type());
    }

    @Test
    void testStructDef() {
        List<Param> fields = List.of(new Param("x", new IntType()));
        StructDef structDef = new StructDef("Point", fields);

        assertEquals("Point", structDef.name());
        assertEquals(1, structDef.fields().size());
    }

    @Test
    void testAbsMethodDef() {
        AbsMethodDef m = new AbsMethodDef("foo", List.of(), new IntType());
        assertEquals("foo", m.name());
        assertTrue(m.returnType() instanceof IntType);
    }

    @Test
    void testConcMethodDef() {
        ConcMethodDef m = new ConcMethodDef("bar", List.of(), new VoidType(), List.of());
        assertEquals("bar", m.name());
        assertEquals(0, m.body().size());
    }

    @Test
    void testTraitDef() {
        TraitDef t = new TraitDef("Trait", List.of(new AbsMethodDef("f", List.of(), new VoidType())));
        assertEquals("Trait", t.name());
        assertEquals(1, t.methods().size());
    }

    @Test
    void testImplDef() {
        ImplDef impl = new ImplDef("Trait", new StructType("Foo"), List.of());
        assertEquals("Trait", impl.traitName());
        assertTrue(impl.forType() instanceof StructType);
    }

    @Test
    void testFuncDef() {
        FuncDef f = new FuncDef("main", List.of(), new IntType(), List.of());
        assertEquals("main", f.name());
    }

    @Test
    void testProgram() {
        Program prog = new Program(List.of(), List.of());
        assertTrue(prog.items().isEmpty());
        assertTrue(prog.entryPoint().isEmpty());
    }
}
