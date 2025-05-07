package typechecker;  // This is your test package, should match where the test is located.

import typechecker.TypeChecker;  // Import the classes you're testing
import typechecker.Type;
import typechecker.Expression;
import typechecker.IntLiteralExpr;
import typechecker.IntType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;



import static org.junit.jupiter.api.Assertions.assertTrue; // Import JUnit assertions

import org.junit.jupiter.api.Test; // Import JUnit annotations

public class TypeCheckerTest {
    @Test 
    public void testIntegerLiteralType() {
        TypeChecker checker = new TypeChecker();
        Map<String, Type> localEnv = new HashMap<>();

        Expression expr = new IntLiteralExpr(42);
        Type result = checker.checkExpression(expr, localEnv);

        assertTrue(result instanceof IntType, "Expected IntType, but got: " + result.getClass().getSimpleName());
    }

    @Test
    public void testBooleanLiteralType() {
        TypeChecker tc = new TypeChecker();
        Map<String, Type> env = new HashMap<>();

        Expression trueExpr = new BooleanLiteralExpr(true);
        Expression falseExpr = new BooleanLiteralExpr(false);

        Type type1 = tc.checkExpression(trueExpr, env);
        Type type2 = tc.checkExpression(falseExpr, env);

        assertTrue(type1 instanceof BooleanType);
        assertTrue(type2 instanceof BooleanType);
    }

    @Test
    public void testStructInstantiationWithCorrectFields() {
        TypeChecker checker = new TypeChecker();

        // Create a type for Point
        Map<String, Type> fieldTypes = new HashMap<>();
        fieldTypes.put("x", new IntType());
        fieldTypes.put("y", new IntType());

        StructDef pointDef = new StructDef("Point", fieldTypes);

        // Register the struct in the typechecker
        checker.checkStruct(pointDef);

        // Create an expression representing: new Point { x: 1, y: 2 }
        Map<String, Expression> fieldExprs = new HashMap<>();
        fieldExprs.put("x", new IntLiteralExpr(1));
        fieldExprs.put("y", new IntLiteralExpr(2));
        StructInstantiationExpr expr = new StructInstantiationExpr("Point", fieldExprs);

        Type result = checker.checkExpression(expr, new HashMap<>());

        assertTrue(result instanceof StructType);

        StructType resultType = (StructType) result;

        assertTrue(resultType.name.equals("Point"));
    }

    @Test
    public void testStructMissingFieldsFails() {
        TypeChecker checker = new TypeChecker();

        // Create a type for Point
        Map<String, Type> fieldTypes = new HashMap<>();
        fieldTypes.put("x", new IntType());
        fieldTypes.put("y", new IntType());

        StructDef pointDef = new StructDef("Point", fieldTypes);

        // Register the struct in the typechecker
        checker.checkStruct(pointDef);

        // Create an expression representing: new Point { x: 1 }
        // y field is missing on purpose
        Map<String, Expression> fieldExprs = new HashMap<>();
        fieldExprs.put("x", new IntLiteralExpr(1));
        StructInstantiationExpr expr = new StructInstantiationExpr("Point", fieldExprs);

        assertThrows(IllegalArgumentException.class, () -> {
            checker.checkExpression(expr, new HashMap<>());
        });
    }

    @Test 
    public void testTraitMethodReference() {
        TypeChecker checker = new TypeChecker();
    
        Map<String, FunctionType> printableMethods = new HashMap<>();
        printableMethods.put("doSomething", new FunctionType(List.of(), new VoidType())); // define it here
        TraitDef printable = new TraitDef("Printable", printableMethods);
        checker.checkTrait(printable);
    
        Map<String, Type> fields = new HashMap<>();
        fields.put("value", new IntType());
        StructDef boxDef = new StructDef("Box", fields);
        checker.checkStruct(boxDef);
    
        Map<String, FunctionType> implMethods = new HashMap<>();
        implMethods.put("doSomething", new FunctionType(List.of(), new VoidType())); // and here
        ImplDef impl = new ImplDef("Printable", new StructType("Box"), implMethods);
        checker.checkImpl(impl);
    
        Map<String, Type> localEnv = new HashMap<>();
        localEnv.put("b", new StructType("Box"));
    
        VariableExpr bVar = new VariableExpr("b");
        MethodCallExpr call = new MethodCallExpr(bVar, "doSomething", List.of());
    
        Type result = checker.checkExpression(call, localEnv);
        assertTrue(result instanceof VoidType);
    }



    








}

