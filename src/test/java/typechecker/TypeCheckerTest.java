package typechecker;  // This is your test package, should match where the test is located.

import typechecker.TypeChecker;  // Import the classes you're testing
import typechecker.Type;
import typechecker.Expression;
import typechecker.IntLiteralExpr;
import typechecker.IntType;

import java.util.Map;
import java.util.HashMap;


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
}

