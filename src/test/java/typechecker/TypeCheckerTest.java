package typechecker;  

import typechecker.TypeChecker; 
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;


import java.util.Arrays;



import static org.junit.jupiter.api.Assertions.assertTrue; 

import org.junit.jupiter.api.Test; 

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
    
        // Correct the method map for the trait "Printable"
        Map<String, FunctionType> printableMethods = new HashMap<>();
        printableMethods.put("doSomething", new FunctionType(List.of(), new VoidType())); // defining doSomething method
        
        // Create the trait definition with methods
        TraitDef printable = new TraitDef("Printable", printableMethods);
        checker.checkTrait(printable);
    
        // Define the struct "Box"
        Map<String, Type> fields = new HashMap<>();
        fields.put("value", new IntType()); // Adding field "value" of type IntType
        StructDef boxDef = new StructDef("Box", fields);
        checker.checkStruct(boxDef);
    
        // Define the implementation method map for the trait "Printable" for "Box"
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> implDoSomethingMethods = new ArrayList<>();
        implDoSomethingMethods.add(new FunctionType(List.of(), new VoidType())); // defining doSomething method
        implMethods.put("doSomething", implDoSomethingMethods); // List of methods for doSomething
        
        // Create implementation for the trait "Printable" for the struct "Box"
        ImplDef impl = new ImplDef("Printable", new StructType("Box"), implMethods);
        checker.checkImpl(impl);
    
        // Define the local environment with a variable "b" of type "Box"
        Map<String, Type> localEnv = new HashMap<>();
        localEnv.put("b", new StructType("Box"));
    
        // Create a variable expression for "b" and invoke the "doSomething" method
        VariableExpr bVar = new VariableExpr("b");
        MethodCallExpr call = new MethodCallExpr(bVar, "doSomething", List.of());
    
        // Check the expression and assert that the result type is VoidType
        Type result = checker.checkExpression(call, localEnv);
        assertTrue(result instanceof VoidType);
    }






    

    @Test
    public void testTraitImplementationForStruct() {
        TypeChecker typeChecker = new TypeChecker();
    
        // Define the Addable trait with the "add" method
        TraitDef addableTrait = new TraitDef("Addable", new HashMap<>());
        FunctionType addMethodType = new FunctionType(
                Arrays.asList(new IntType()), // Argument type
                new IntType() // Return type
        );
        addableTrait.methods.put("add", addMethodType); // Add the method to the trait
    
        // Define the IntWrapper struct with a field "value"
        StructDef intWrapperStruct = new StructDef("IntWrapper", new HashMap<>());
        intWrapperStruct.fields.put("value", new IntType());
    
        // Define the implementation of the Addable trait for the IntWrapper struct
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> implAddMethodList = new ArrayList<>();
        implAddMethodList.add(addMethodType); // Add the "add" method to the impl
        implMethods.put("add", implAddMethodList); // Map "add" method to the impl
        
        ImplDef addableForIntWrapper = new ImplDef("Addable", new StructType("IntWrapper"), implMethods);
    
        // Register the trait, struct, and implementation in the type checker
        typeChecker.checkTrait(addableTrait);
        typeChecker.checkStruct(intWrapperStruct);
        typeChecker.checkImpl(addableForIntWrapper);
    
        // Assert that the implementation is registered in the environment
        assertTrue(typeChecker.env.impls.containsKey("Addable"),
                   "ImplDef for Addable should be registered");
    
        // Retrieve the list of implementations for the "Addable" trait
        List<ImplDef> implList = typeChecker.env.impls.get("Addable");
        assertNotNull(implList, "Impl list for Addable should not be null");
        assertFalse(implList.isEmpty(), "Impl list for Addable should not be empty");
    
        // Retrieve the first implementation and verify its type
        ImplDef retrievedImpl = implList.get(0);
        assertEquals("IntWrapper", ((StructType) retrievedImpl.forType).name,
                     "Implementation type should be IntWrapper");
    
        // Verify that the "add" method is present in the implementation
        assertTrue(retrievedImpl.methods.containsKey("add"),
                   "Method add should exist in the implementation");
    }


    @Test
    public void testTraitMethodTypeResolution() {
        TypeChecker typeChecker = new TypeChecker();
        
        // Define the 'Addable' trait with a method 'add'
        TraitDef addableTrait = new TraitDef("Addable", new HashMap<>());
        FunctionType addMethodType = new FunctionType(
                Arrays.asList(new IntType()),  // Parameter type: Int
                new IntType()                  // Return type: Int
        );
        addableTrait.methods.put("add", addMethodType); // Add the method to the trait
        
        // Define the 'IntWrapper' struct with a field 'value' of type Int
        StructDef intWrapperStruct = new StructDef("IntWrapper", new HashMap<>());
        intWrapperStruct.fields.put("value", new IntType());
        
        // Implement 'Addable' for 'IntWrapper'
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> implAddMethodList = new ArrayList<>();
        implAddMethodList.add(addMethodType); // Add the "add" method to the impl
        implMethods.put("add", implAddMethodList); // Map "add" method to the impl
        
        ImplDef addableForIntWrapper = new ImplDef("Addable", new StructType("IntWrapper"), implMethods);
    
        // Register the trait, struct, and implementation
        typeChecker.checkTrait(addableTrait);
        typeChecker.checkStruct(intWrapperStruct);
        typeChecker.checkImpl(addableForIntWrapper);
        
        // Ensure the implementation is registered
        assertTrue(typeChecker.env.impls.containsKey("Addable"),
                   "ImplDef for Addable should be registered");
    
        // Retrieve the ImplDef and validate it
        List<ImplDef> implList = typeChecker.env.impls.get("Addable");
        ImplDef retrievedImpl = implList.get(0);
        assertNotNull(retrievedImpl,
                      "The implementation for Addable should not be null");
    
        // Validate that the implementation is for the correct struct type
        assertEquals("IntWrapper", ((StructType) retrievedImpl.forType).name,
                     "Implementation type should be IntWrapper");
    
        // Ensure the method 'add' exists in the implementation
        assertTrue(retrievedImpl.methods.containsKey("add"),
                   "Method add should exist in the implementation");
    
        // Validate the method's return type
        FunctionType returnedAddMethod = (FunctionType) retrievedImpl.methods.get("add").get(0);
        assertEquals(new IntType(), returnedAddMethod.returnType,
                     "The return type of add method should be Int");
    
        // Validate the method's parameter type
        assertEquals(Arrays.asList(new IntType()), returnedAddMethod.paramTypes,
                     "The parameter type of add method should be Int");
    }


    @Test
    public void testTraitMethodCallOnStructInstance() {
        TypeChecker typeChecker = new TypeChecker();
        
        // Define the 'Addable' trait with a method 'add'
        TraitDef addableTrait = new TraitDef("Addable", new HashMap<>());
        FunctionType addMethodType = new FunctionType(
                Arrays.asList(new IntType()),  // Parameter type: Int
                new IntType()                  // Return type: Int
        );
        addableTrait.methods.put("add", addMethodType);
        
        // Define the 'IntWrapper' struct with a field 'value' of type Int
        StructDef intWrapperStruct = new StructDef("IntWrapper", new HashMap<>());
        intWrapperStruct.fields.put("value", new IntType());
        
        // Implement 'Addable' for 'IntWrapper'
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> addMethodList = new ArrayList<>();
        addMethodList.add(addMethodType); // Add method to implementation
        implMethods.put("add", addMethodList); // Map the "add" method
        
        ImplDef addableForIntWrapper = new ImplDef("Addable", new StructType("IntWrapper"), implMethods);
        
        // Register the trait, struct, and implementation
        typeChecker.checkTrait(addableTrait);
        typeChecker.checkStruct(intWrapperStruct);
        typeChecker.checkImpl(addableForIntWrapper);
        
        // Create an instance of IntWrapper using StructInstantiationExpr
        Map<String, Expression> fields = new HashMap<>();
        fields.put("value", new IntLiteralExpr(5));  // Assuming IntLiteralExpr represents integer literals
        StructInstantiationExpr intWrapperInstance = new StructInstantiationExpr("IntWrapper", fields);
        
        // Ensure the instance is created
        assertNotNull(intWrapperInstance, "Instance of IntWrapper should not be null");
        
        // Create a MethodCallExpr for calling 'add' on the IntWrapper instance
        MethodCallExpr addMethodCall = new MethodCallExpr(
                intWrapperInstance,           // Receiver expression (the struct instance itself)
                "add",                        // Method name
                Arrays.asList(new IntLiteralExpr(3))  // Argument (integer literal)
        );
        
        // Perform type-checking and ensure no exceptions are thrown
        Type returnType = typeChecker.checkExpression(addMethodCall, new HashMap<>());
        
        // Assert that the method call resolved to the correct return type (IntType)
        assertTrue(returnType instanceof IntType, "The return type of the add method should be Int");
    }


    @Test
    public void testArithmeticExpressionType() {
        TypeChecker typeChecker = new TypeChecker();
        // Create a sample type environment for testing
        Map<String, Type> localEnv = new HashMap<>();
        
        // Add integer variables to the local environment
        localEnv.put("a", new IntType());
        localEnv.put("b", new IntType());
    
        // Test valid arithmetic expression (should return IntType)
        BinaryExpr validExpr = new BinaryExpr("+", new VariableExpr("a"), new VariableExpr("b"));
        Type result = typeChecker.checkExpression(validExpr, localEnv);
        assertTrue(result instanceof IntType, "Expected IntType for valid arithmetic expression");
        
        // Test invalid arithmetic expression (should throw exception)
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BinaryExpr invalidExpr = new BinaryExpr("+", new VariableExpr("a"), new BooleanLiteralExpr(true));
            typeChecker.checkExpression(invalidExpr, localEnv);
        });
        assertTrue(thrown.getMessage().contains("Operands must be Int"), "Error message should indicate operand type mismatch");
        
        // Test another valid arithmetic expression (multiplication)
        BinaryExpr validMulExpr = new BinaryExpr("*", new VariableExpr("a"), new VariableExpr("b"));
        Type mulResult = typeChecker.checkExpression(validMulExpr, localEnv);
        assertTrue(mulResult instanceof IntType, "Expected IntType for valid multiplication expression");
    
        // Test invalid multiplication (mixing IntType and BooleanType)
        thrown = assertThrows(RuntimeException.class, () -> {
            BinaryExpr invalidMulExpr = new BinaryExpr("*", new VariableExpr("a"), new BooleanLiteralExpr(true));
            typeChecker.checkExpression(invalidMulExpr, localEnv);
        });
        assertTrue(thrown.getMessage().contains("Operands must be Int"), "Error message should indicate operand type mismatch");
    }

    @Test
    public void testSelfReferenceInTrait() {
        TypeChecker checker = new TypeChecker();
        
        // trait Addable { method add(other: Self): Self }
        Map<String, FunctionType> traitMethods = new HashMap<>();
        traitMethods.put("add", new FunctionType(
            List.of(new StructType("MyInt")),  // Self resolved to MyInt
            new StructType("MyInt")            // Return type is MyInt
        ));
        TraitDef trait = new TraitDef("Addable", traitMethods);
        checker.checkTrait(trait);
        
        // struct MyInt { value: Int }
        Map<String, Type> fields = new HashMap<>();
        fields.put("value", new IntType());
        StructDef struct = new StructDef("MyInt", fields);
        checker.checkStruct(struct);
        
        // impl Addable for MyInt { method add(other: MyInt): MyInt { return new MyInt { value: self.value + other.value } } }
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> addMethodList = new ArrayList<>();
        addMethodList.add(new FunctionType(
            List.of(new StructType("MyInt")),  // Parameter type is MyInt
            new StructType("MyInt")            // Return type is MyInt
        ));
        implMethods.put("add", addMethodList); // Adding the method to the map
        
        ImplDef impl = new ImplDef("Addable", new StructType("MyInt"), implMethods);
        
        // This shouldn't throw
        assertDoesNotThrow(() -> checker.checkImpl(impl));
    }





    @Test
    public void testMethodCallWithStructArgument() {
        TypeChecker checker = new TypeChecker();
        
        // Define a trait with a method that takes a struct as an argument
        Map<String, FunctionType> methods = new HashMap<>();
        methods.put("process", new FunctionType(
            List.of(new StructType("Data")),  // Argument type: Data (struct)
            new IntType()                     // Return type: Int
        ));
        TraitDef trait = new TraitDef("Processor", methods);
        checker.checkTrait(trait);
        
        // Define the struct
        Map<String, Type> fields = new HashMap<>();
        fields.put("val", new IntType());
        StructDef struct = new StructDef("Data", fields);
        checker.checkStruct(struct);
        
        // Implement the trait for the struct
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        List<FunctionType> implProcessMethodList = new ArrayList<>();
        implProcessMethodList.add(new FunctionType(
            List.of(new StructType("Data")),  // Argument type: Data (struct)
            new IntType()                     // Return type: Int
        ));
        implMethods.put("process", implProcessMethodList);  // Add the method to the map
        ImplDef impl = new ImplDef("Processor", new StructType("Data"), implMethods);
        checker.checkImpl(impl);
        
        // Make a variable in the environment of type Processor
        Map<String, Type> env = new HashMap<>();
        env.put("p", new StructType("Data")); // p is the receiver of the method
        
        // Create an argument: new Data { val: 123 }
        Map<String, Expression> argFields = new HashMap<>();
        argFields.put("val", new IntLiteralExpr(123));
        StructInstantiationExpr arg = new StructInstantiationExpr("Data", argFields);
        
        // Method call: p.process(new Data { val: 123 })
        MethodCallExpr call = new MethodCallExpr(
            new VariableExpr("p"),
            "process",
            List.of(arg)
        );
        
        // Type check
        Type result = checker.checkExpression(call, env);
        
        assertTrue(result instanceof IntType, "Expected IntType as result of method call");
    }
















    








}

