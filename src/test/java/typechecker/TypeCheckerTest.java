package typechecker;

import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test; 

@Test 
public void testIntegerLiteralType(){ }

@Test 
public void testBooleanLiteralType(){ }

@Test 
public void testSelfReferenceInTrait(){ }

@Test 
public void testStructInstantiationWithCorrectFields(){ }

@Test 
public void testStructMissingFieldFails(){ }

@Test 
public void testStructFieldAccessTypeCorrectness(){ }

@Test 
public void testTraitImplementationForStruct(){}

@Test 
public void testTraitMethodTypeResolution(){}

@Test 
public void testTraitMethodCallOnStructInstance(){ }

@Test 
public void testFunctionOverloadResolvesCorrectly() { }

@Test 
public void testAmbiguousOverloadCausesError() { }

@Test 
public void testHigherOrderFunctionParameterType() { }

@Test 
public void testFunctionReturningFunctionType() { }

@Test 
public void testArithmeticExpressionType() {  }

@Test 
public void testComparisonExpressionReturnsBoolean() {  }

@Test 
public void testMethodCallExpressionTypeCorrectness() { }

@Test 
public void testIfStatementTypeCheck() { }

@Test 
public void testReturnTypeMatchesFunction() { }

@Test 
public void testVoidFunctionDoesNotReturnValue() {  }

@Test 
public void testFullProgramTypeCheckPasses() { }

@Test 
public void testProgramWithTypeErrorFails() { }
