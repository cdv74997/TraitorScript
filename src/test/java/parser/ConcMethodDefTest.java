package parser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ConcMethodDefTest {

    @Test
    void testConstructorAndGetters() {
        String name = "myMethod";
        List<Param> params = List.of(
                new Param("x", new IntType()),
                new Param("y", new BooleanType())
        );
        Type returnType = new IntType();
        List<Stmt> body = List.of(
                new ReturnStmt(Optional.of(new IntLiteral(42)))
        );

        ConcMethodDef methodDef = new ConcMethodDef(name, params, returnType, body);

        assertEquals("myMethod", methodDef.name());
        assertEquals(2, methodDef.params().size());
        assertTrue(methodDef.returnType() instanceof IntType);
        assertEquals(1, methodDef.body().size());
        assertTrue(methodDef.body().get(0) instanceof ReturnStmt);
    }

    @Test
    void testEmptyParametersAndBody() {
        String name = "emptyMethod";
        List<Param> params = List.of();
        Type returnType = new VoidType();
        List<Stmt> body = List.of();

        ConcMethodDef methodDef = new ConcMethodDef(name, params, returnType, body);

        assertEquals("emptyMethod", methodDef.name());
        assertTrue(methodDef.params().isEmpty());
        assertTrue(methodDef.returnType() instanceof VoidType);
        assertTrue(methodDef.body().isEmpty());
    }
}
