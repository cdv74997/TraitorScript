package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.List;

class FunctionTypeTest {
    
    @Test
    void testFunctionTypeConstructor() {
        Type intType = new IntType();
        Type boolType = new BooleanType();
        Type returnType = new VoidType();
        
        FunctionType funcType = new FunctionType(List.of(intType, boolType), returnType);
        
        // Check param types and return type
        assertEquals(2, funcType.paramTypes().size());
        assertEquals(intType, funcType.paramTypes().get(0));
        assertEquals(boolType, funcType.paramTypes().get(1));
        assertEquals(returnType, funcType.returnType());
    }

    @Test
    void testToStringWithMultipleParams() {
        Type intType = new IntType();
        Type boolType = new BooleanType();
        Type returnType = new VoidType();
        
        FunctionType funcType = new FunctionType(List.of(intType, boolType), returnType);
        
        // Check the toString method formatting
        assertEquals("(Int, Bool) => Void", funcType.toString());
    }

    @Test
    void testToStringWithSingleParam() {
        Type intType = new IntType();
        Type returnType = new VoidType();
        
        FunctionType funcType = new FunctionType(List.of(intType), returnType);
        
        // Check the toString method formatting for a single parameter
        assertEquals("(Int) => Void", funcType.toString());
    }

    @Test
    void testToStringWithNoParams() {
        Type returnType = new VoidType();
        
        FunctionType funcType = new FunctionType(List.of(), returnType);
        
        // Check the toString method formatting for no parameters
        assertEquals("() => Void", funcType.toString());
    }
}
