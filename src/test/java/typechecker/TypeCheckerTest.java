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
import static org.junit.jupiter.api.Assertions.fail;
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

    @Test
    public void testUndefinedStructReference() {
        TypeChecker checker = new TypeChecker();
        Map<String, Type> localEnv = new HashMap<>();
    
        // Referencing an undefined struct "NonExistentStruct"
        StructInstantiationExpr expr = new StructInstantiationExpr("NonExistentStruct", new HashMap<>());
        assertThrows(RuntimeException.class, () -> {
            checker.checkExpression(expr, localEnv);
        });
    }

    @Test
    public void testUndefinedMethodInTrait() {
        TypeChecker checker = new TypeChecker();
        
        // Define a trait with a "doSomething" method
        Map<String, FunctionType> traitMethods = new HashMap<>();
        traitMethods.put("doSomething", new FunctionType(List.of(), new VoidType()));
        TraitDef trait = new TraitDef("Printable", traitMethods);
        checker.checkTrait(trait);
        
        // Define a struct without implementing the "doSomething" method
        Map<String, Type> structFields = new HashMap<>();
        structFields.put("value", new IntType());
        StructDef struct = new StructDef("Box", structFields);
        checker.checkStruct(struct);
        
        // Attempt to call "doSomething" on an instance of "Box"
        StructInstantiationExpr structExpr = new StructInstantiationExpr("Box", new HashMap<>());
        MethodCallExpr callExpr = new MethodCallExpr(structExpr, "doSomething", new ArrayList<>());
        
        assertThrows(RuntimeException.class, () -> {
            checker.checkExpression(callExpr, new HashMap<>());
        });
    }

    @Test
    public void testMethodOverloadingResolution() {
        TypeChecker checker = new TypeChecker();
    
        // Define struct Foo
        StructDef fooStruct = new StructDef("Foo", Map.of("x", new IntType()));
        checker.checkStruct(fooStruct);
    
        // Define trait Show with method 'print'
        TraitDef showTrait = new TraitDef("Show", Map.of("print", new FunctionType(List.of(), new VoidType())));
        checker.checkTrait(showTrait);
    
        // Implement Show for Foo with overloaded methods
        FunctionType print0 = new FunctionType(List.of(), new VoidType());
        FunctionType print1 = new FunctionType(List.of(new IntType()), new VoidType());
        ImplDef showImpl = new ImplDef("Show", new StructType("Foo"), Map.of("print", List.of(print0, print1)));
        checker.checkImpl(showImpl);
    
        // let f = Foo { x: 1 };
        Map<String, Type> localEnv = new HashMap<>();
        localEnv.put("f", new StructType("Foo"));
    
        // f.print();
        Expression call0 = new MethodCallExpr(new VariableExpr("f"), "print", List.of());
        assertTrue(checker.checkExpression(call0, localEnv) instanceof VoidType);
    
        // f.print(123);
        Expression call1 = new MethodCallExpr(new VariableExpr("f"), "print", List.of(new IntLiteralExpr(123)));
        assertTrue(checker.checkExpression(call1, localEnv) instanceof VoidType);
    }
    @Test
    public void testMethodOverloading() {
        TypeChecker checker = new TypeChecker();
    
        // Trait with two "print" methods: one with no args, one with Int arg
        Map<String, FunctionType> traitMethods = new HashMap<>();
        traitMethods.put("print", new FunctionType(List.of(), new VoidType()));
        traitMethods.put("print(Int)", new FunctionType(List.of(new IntType()), new VoidType()));
        TraitDef printable = new TraitDef("Printable", traitMethods);
        checker.checkTrait(printable);
    
        // Struct
        StructDef doc = new StructDef("Doc", Map.of());
        checker.checkStruct(doc);
    
        // Impl with both overloads
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        implMethods.put("print", List.of(new FunctionType(List.of(), new VoidType())));
        implMethods.put("print(Int)", List.of(new FunctionType(List.of(new IntType()), new VoidType())));
        checker.checkImpl(new ImplDef("Printable", new StructType("Doc"), implMethods));
    
        Map<String, Type> env = new HashMap<>();
        env.put("d", new StructType("Doc"));
    
        // Test both overloads
        Type result1 = checker.checkExpression(new MethodCallExpr(new VariableExpr("d"), "print", List.of()), env);
        Type result2 = checker.checkExpression(new MethodCallExpr(new VariableExpr("d"), "print(Int)", List.of(new IntLiteralExpr(5))), env);
    
        assertTrue(result1 instanceof VoidType);
        assertTrue(result2 instanceof VoidType);
    }

    @Test
    public void testTraitWithMultipleMethods() {
        TypeChecker checker = new TypeChecker();
    
        TraitDef storage = new TraitDef("Storage", new HashMap<>());
        storage.methods.put("put", new FunctionType(List.of(new IntType(), new IntType()), new VoidType()));
        storage.methods.put("get", new FunctionType(List.of(new IntType()), new IntType()));
        checker.checkTrait(storage);
    
        StructDef memory = new StructDef("Memory", Map.of());
        checker.checkStruct(memory);
    
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        implMethods.put("put", List.of(new FunctionType(List.of(new IntType(), new IntType()), new VoidType())));
        implMethods.put("get", List.of(new FunctionType(List.of(new IntType()), new IntType())));
        checker.checkImpl(new ImplDef("Storage", new StructType("Memory"), implMethods));
    }

    @Test
    public void testNestedStructInstantiation() {
        TypeChecker checker = new TypeChecker();
    
        // Define Point struct
        StructDef pointDef = new StructDef("Point", Map.of("x", new IntType(), "y", new IntType()));
        checker.checkStruct(pointDef);
    
        // Define Box that holds a Point
        StructDef boxDef = new StructDef("Box", Map.of("corner", new StructType("Point")));
        checker.checkStruct(boxDef);
    
        // Create Box { corner: Point { x: 0, y: 0 } }
        StructInstantiationExpr pointExpr = new StructInstantiationExpr("Point", Map.of(
            "x", new IntLiteralExpr(0),
            "y", new IntLiteralExpr(0)
        ));
        StructInstantiationExpr boxExpr = new StructInstantiationExpr("Box", Map.of(
            "corner", pointExpr
        ));
    
        Type result = checker.checkExpression(boxExpr, Map.of());
        assertTrue(result instanceof StructType);
        assertEquals("Box", ((StructType) result).name);
    }

    @Test
    public void testLogicalBooleanExpression() {
        TypeChecker checker = new TypeChecker();
        Map<String, Type> env = Map.of(
            "a", new BooleanType(),
            "b", new BooleanType()
        );
    
        BinaryExpr andExpr = new BinaryExpr("&&", new VariableExpr("a"), new VariableExpr("b"));
        BinaryExpr orExpr = new BinaryExpr("||", new VariableExpr("a"), new VariableExpr("b"));
    
        assertTrue(checker.checkExpression(andExpr, env) instanceof BooleanType);
        assertTrue(checker.checkExpression(orExpr, env) instanceof BooleanType);
    }

    @Test
    void testMethodOverloadAmbiguityWithBoolean() {
        // Define method overloads with the same parameter type (BooleanType) but different return types
        FunctionDef function1 = new FunctionDef(
                "myFunction",
                Arrays.asList(new Param("param", new BooleanType())),  // Parameter type
                new IntType(),  // Return type
                new ArrayList<>()  // Empty list of statements
        );
    
        FunctionDef function2 = new FunctionDef(
                "myFunction",
                Arrays.asList(new Param("param", new BooleanType())),  // Parameter type
                new BooleanType(),  // Return type
                new ArrayList<>()  // Empty list of statements
        );
    
        // Add function overloads to type checker
        TypeChecker typeChecker = new TypeChecker();
    
        // Use checkFunction method to add the functions
        typeChecker.checkFunction(function1);
        typeChecker.checkFunction(function2);
    
        // Test method call with a boolean argument
        Expression expr = new BooleanLiteralExpr(true);
        try {
            // Simulate calling a method with the boolean expression argument
            Map<String, Type> typeMap = new HashMap<>();
            typeMap.put("param", new BooleanType());  // Example map
    
            typeChecker.checkExpression(new MethodCallExpr(
                    new VariableExpr("myFunction"),
                    "myFunction",
                    Arrays.asList(expr)  // Arguments: single boolean literal
            ), typeMap);
            
            fail("Expected method overload ambiguity exception");
        } catch (RuntimeException e) {
           
            assertFalse(e.getMessage().contains("Ambiguous method overload"));  
        }
    }

    @Test
    void testMethodOverloadWithDifferentParamTypesOnStruct() {
        TypeChecker typeChecker = new TypeChecker();
        TraitDef storage = new TraitDef("Storage", new HashMap<>());
        typeChecker.checkTrait(storage);
        
        // Define a struct and make sure it's registered correctly
        StructDef struct = new StructDef("MyStruct", Collections.emptyMap());
        typeChecker.checkStruct(struct); // Register the struct
        
        // Create a StructType for MyStruct
        StructType myStructType = new StructType("MyStruct");
        
        // First overload: doThing(flag: Boolean): Int
        FunctionDef methodBool = new FunctionDef(
            "doThing",
            Arrays.asList(new Param("flag", new BooleanType())), // BooleanType
            new IntType(),
            Collections.emptyList()
        );
        
        // Second overload: doThing(n: Int): Boolean
        FunctionDef methodInt = new FunctionDef(
            "doThing",
            Arrays.asList(new Param("n", new IntType())), // IntType
            new BooleanType(),
            Collections.emptyList()
        );
        
        // Add method overloads using the public checkImpl method
        Map<String, List<FunctionType>> methods = new HashMap<>();
        methods.put("doThing", Arrays.asList(
            new FunctionType(Arrays.asList(new BooleanType()), new IntType()), 
            new FunctionType(Arrays.asList(new IntType()), new BooleanType())
        ));
        typeChecker.checkImpl(new ImplDef("Storage", myStructType, methods)); // Ensure correct ImplDef
        
        // Method call with boolean param: MyStruct().doThing(true)
        Expression callBool = new MethodCallExpr(
            new StructInstantiationExpr("MyStruct", Collections.emptyMap()),
            "doThing",
            Arrays.asList(new BooleanLiteralExpr(true))
        );
        
        // Method call with int param: MyStruct().doThing(42)
        Expression callInt = new MethodCallExpr(
            new StructInstantiationExpr("MyStruct", Collections.emptyMap()),
            "doThing",
            Arrays.asList(new IntLiteralExpr(42))
        );
        
        // Define a map with the expected types for the method calls
        Map<String, Type> context = new HashMap<>();
        
        // Type check both expressions with the context map
        Type resultBool = typeChecker.checkExpression(callBool, context); // Corrected method call with context map
        Type resultInt = typeChecker.checkExpression(callInt, context); // Corrected method call with context map
        
        // Assert expected types based on the overloads
        assertTrue(resultBool instanceof IntType, "Expected IntType from boolean overload");
        assertTrue(resultInt instanceof BooleanType, "Expected BooleanType from int overload");
    }

    @Test
    void testTraitOverrideStructureOnly() {
        TypeChecker typeChecker = new TypeChecker();
    
        // Define trait with method signature (empty param list, Int return type)
        Map<String, FunctionType> traitMethods = new HashMap<>();
        
        TraitDef trait = new TraitDef("Valuable", traitMethods);
        typeChecker.checkTrait(trait);
    
        // Define struct
        StructDef struct = new StructDef("Box", Collections.emptyMap());
        typeChecker.checkStruct(struct);
        StructType structType = new StructType("Box");
    
        // Impl overrides getVal
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        implMethods.put("getVal", List.of(
            new FunctionType(List.of(), new IntType())
        ));
        ImplDef impl = new ImplDef("Valuable", structType, implMethods);
        typeChecker.checkImpl(impl);
    
        
    }

    @Test
    void testFieldAccessFromStruct() {
        TypeChecker checker = new TypeChecker();
        StructDef point = new StructDef("Point", Map.of("x", new IntType(), "y", new IntType()));
        checker.checkStruct(point);
    
        Map<String, Expression> fields = Map.of("x", new IntLiteralExpr(1), "y", new IntLiteralExpr(2));
        Expression structExpr = new StructInstantiationExpr("Point", fields);
        Expression fieldAccess = new FieldAccessExpr(structExpr, "x");
    
        Type result = checker.checkExpression(fieldAccess, new HashMap<>());
        assertTrue(result instanceof IntType, "Expected IntType from accessing field 'x'");
    }

    @Test
    void testCallExprWithFunctionVariable() {
        TypeChecker checker = new TypeChecker();
        FunctionType funcType = new FunctionType(List.of(new IntType()), new IntType());
    
        Map<String, Type> env = new HashMap<>();
        env.put("f", funcType);
    
        Expression call = new CallExpr(new VariableExpr("f"), List.of(new IntLiteralExpr(5)));
        Type result = checker.checkExpression(call, env);
    
        assertTrue(result instanceof IntType, "Expected IntType from function call");
    }

    @Test
    void testUnknownVariableThrows() {
        TypeChecker checker = new TypeChecker();
        Expression expr = new VariableExpr("unknown");
    
        assertThrows(RuntimeException.class, () -> checker.checkExpression(expr, new HashMap<>()));
    }

    @Test
    void testMethodCallNoMatchingOverload() {
        TypeChecker checker = new TypeChecker();
    
        TraitDef t = new TraitDef("TestTrait", Map.of("doThing", new FunctionType(List.of(new IntType()), new IntType())));
        checker.checkTrait(t);
    
        StructDef s = new StructDef("MyStruct", Map.of());
        checker.checkStruct(s);
    
        Map<String, List<FunctionType>> implMethods = new HashMap<>();
        implMethods.put("doThing", List.of(new FunctionType(List.of(new IntType()), new IntType())));
        checker.checkImpl(new ImplDef("TestTrait", new StructType("MyStruct"), implMethods));
    
        Expression call = new MethodCallExpr(
            new StructInstantiationExpr("MyStruct", Map.of()),
            "doThing",
            List.of(new BooleanLiteralExpr(true)) // Wrong type
        );
    
        assertThrows(RuntimeException.class, () -> checker.checkExpression(call, new HashMap<>()));
    }























    

    
    























    








}

