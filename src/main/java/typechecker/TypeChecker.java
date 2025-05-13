package typechecker;
import java.util.*;







class VoidType implements Type {}
class BooleanType implements Type {}

class MethodDef {
    String name;
    List<Param> params;
    Type returnType;
    List<Statement> body;

    MethodDef(String name, List<Param> params, Type returnType, List<Statement> body) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}

class FieldAccessExpr implements Expression {
    public final Expression receiver;
    public final String field;

    public FieldAccessExpr(Expression receiver, String field) {
        this.receiver = receiver;
        this.field = field;
    }
}

class MethodCallExpr implements Expression {
    Expression receiver;
    String methodName;
    List<Expression> arguments;

    MethodCallExpr(Expression receiver, String methodName, List<Expression> arguments) {
        this.receiver = receiver;
        this.methodName = methodName;
        this.arguments = arguments;
    }
}


class CallExpr implements Expression {
    VariableExpr callee;
    List<Expression> arguments;

    CallExpr(VariableExpr callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
}


class StructType implements Type {
    String name;
    StructType(String name) { this.name = name; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructType)) return false;
        StructType other = (StructType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "StructType(" + name + ")";
    }
}

class FunctionType implements Type {
    List<Type> paramTypes;
    Type returnType;

    FunctionType(List<Type> paramTypes, Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }
}

class BooleanLiteralExpr implements Expression {
    boolean value;

    BooleanLiteralExpr(boolean value) {
        this.value = value;
    }
}



class StructDef {
    String name;
    Map<String, Type> fields;

    StructDef(String name, Map<String, Type> fields) {
        this.name = name;
        this.fields = fields;
    }
}

class TraitDef {
    String name;
    Map<String, FunctionType> methods;

    TraitDef(String name, Map<String, FunctionType> methods) {
        this.name = name;
        this.methods = methods;
    }
}
 
class ImplDef {
    String traitName;
    Type forType;
    Map<String, List<FunctionType>> methods;

    //Map<String, FunctionType> methods;

    ImplDef(String traitName, Type forType, Map<String, List<FunctionType>> methods) {
        this.traitName = traitName;
        this.forType = forType;
        this.methods = methods;
    }
}

class FunctionDef {
    String name;
    List<Param> params;
    Type returnType;
    List<Statement> body;

    FunctionDef(String name, List<Param> params, Type returnType, List<Statement> body) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}

class Param {
    String name;
    Type type;

    Param(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}





class LetStatement implements Statement {
    Param param;
    Expression expr;

    LetStatement(Param param, Expression expr) {
        this.param = param;
        this.expr = expr;
    }
}

class AssignStatement implements Statement {
    String var;
    Expression expr;

    AssignStatement(String var, Expression expr) {
        this.var = var;
        this.expr = expr;
    }
}

class ReturnStatement implements Statement {
    Expression expr;

    ReturnStatement(Expression expr) {
        this.expr = expr;
    }
}

class VariableExpr implements Expression {
    String name;
    VariableExpr(String name) { this.name = name; }
}


class BinaryExpr implements Expression {
    String op;
    Expression left, right;

    BinaryExpr(String op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}

class StructInstantiationExpr implements Expression {
    String structName;
    Map<String, Expression> fieldValues;

    StructInstantiationExpr(String structName, Map<String, Expression> fieldValues) {
        this.structName = structName;
        this.fieldValues = fieldValues;
    }
}



class TypeEnvironment {
    Map<String, Type> variables = new HashMap<>();
    Map<String, StructDef> structs = new HashMap<>();
    Map<String, TraitDef> traits = new HashMap<>();
    Map<String, List<ImplDef>> impls = new HashMap<>();
}


public class TypeChecker {
    // Store ImplDefs by trait name
    public Map<String, ImplDef> implDefs = new HashMap<>();

    //private final Map<Type, Map<String, FunctionType>> methodsForType = new HashMap<>();
    //private final Map<String, List<FunctionType>> methodsForType = new HashMap<>();
    private final Map<Type, Map<String, List<FunctionType>>> methodsForType = new HashMap<>();



    private final Map<Type, List<ImplDef>> implsForType = new HashMap<>();

    
    TypeEnvironment env = new TypeEnvironment();

    public void checkStruct(StructDef struct) {
        env.structs.put(struct.name, struct);
    }

    
    public FunctionType getTraitMethodType(String traitName, String methodName, Type receiverType) {
        List<ImplDef> impls = env.impls.get(traitName);
        if (impls == null) return null;
        for (ImplDef impl : impls) {
            if (impl.forType.equals(receiverType)) {
                List<FunctionType> overloads = impl.methods.get(methodName);
                if (overloads != null && !overloads.isEmpty()) {
                    return overloads.get(0); // or apply overload resolution here
                }
            }
        }
        return null;
    }



    public void checkTrait(TraitDef trait) {
        env.traits.put(trait.name, trait);
    }

    public void checkImpl(ImplDef impl) {
        TraitDef trait = env.traits.get(impl.traitName);
        if (trait == null) {
            throw new RuntimeException("Unknown trait: " + impl.traitName);
        }
        for (String method : trait.methods.keySet()) {
            if (!impl.methods.containsKey(method)) {
                throw new RuntimeException("Method " + method + " not implemented for trait " + impl.traitName);
            }
        }
    
        env.impls.computeIfAbsent(impl.traitName, k -> new ArrayList<>()).add(impl);
    
        implsForType.computeIfAbsent(impl.forType, k -> new ArrayList<>()).add(impl);
    
        // Populate the methodsForType mapping for overloading
        for (Map.Entry<String, List<FunctionType>> entry : impl.methods.entrySet()) {
            String methodName = entry.getKey();
            List<FunctionType> overloads = entry.getValue();
    
            // Ensure that overloads for the same method name are added to the list
            Map<String, List<FunctionType>> methodMap = methodsForType.computeIfAbsent(impl.forType, k -> new HashMap<>());
            methodMap.computeIfAbsent(methodName, k -> new ArrayList<>()).addAll(overloads);
        }
    }


    public void checkFunction(FunctionDef func) {
        Map<String, Type> localEnv = new HashMap<>();
        for (Param p : func.params) {
            localEnv.put(p.name, p.type);
        }
        for (Statement stmt : func.body) {
            checkStatement(stmt, localEnv);
        }
    }

    public void checkStatement(Statement stmt, Map<String, Type> localEnv) {
        if (stmt instanceof LetStatement) {
            LetStatement let = (LetStatement) stmt;
            Type exprType = checkExpression(let.expr, localEnv);
            if (!exprType.getClass().equals(let.param.type.getClass())) {
                throw new RuntimeException("Type mismatch in let statement: expected " + let.param.type + ", got " + exprType);
            }
            localEnv.put(let.param.name, let.param.type);
        } else if (stmt instanceof AssignStatement) {
            AssignStatement assign = (AssignStatement) stmt;
            if (!localEnv.containsKey(assign.var)) {
                throw new RuntimeException("Unknown variable: " + assign.var);
            }
            Type exprType = checkExpression(assign.expr, localEnv);
            if (!exprType.getClass().equals(localEnv.get(assign.var).getClass())) {
                throw new RuntimeException("Type mismatch in assignment");
            }
        } else if (stmt instanceof ReturnStatement) {
            ReturnStatement ret = (ReturnStatement) stmt;
            checkExpression(ret.expr, localEnv);
        }
    }

    public Type checkExpression(Expression expr, Map<String, Type> localEnv) {
        if (expr instanceof IntLiteralExpr) {
            return new IntType();
        } else if (expr instanceof VariableExpr) {
            VariableExpr v = (VariableExpr) expr;
            if (!localEnv.containsKey(v.name)) {
                throw new RuntimeException("Unknown variable: " + v.name);
            }
            return localEnv.get(v.name);
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr bin = (BinaryExpr) expr;
            Type left = checkExpression(bin.left, localEnv);
            Type right = checkExpression(bin.right, localEnv);
            if (!(left instanceof IntType && right instanceof IntType)) {
                throw new RuntimeException("Operands must be Int");
            }
            return new IntType();
        } else if (expr instanceof BooleanLiteralExpr) {
            return new BooleanType();
        } else if (expr instanceof StructInstantiationExpr) {
            StructInstantiationExpr structExpr = (StructInstantiationExpr) expr;
            StructDef def = env.structs.get(structExpr.structName);

            if (def == null) {
                throw new RuntimeException("Unknown struct: " + structExpr.structName);
            }

            // Check all required fields are present
            for (String fieldName : def.fields.keySet()) {
                if (!structExpr.fieldValues.containsKey(fieldName)) {
                    throw new IllegalArgumentException("Missing field: " + fieldName + " in struct instantiation of " + structExpr.structName);
                }
            }

            // Check no extra fields are given
            for (String givenField : structExpr.fieldValues.keySet()) {
                if (!def.fields.containsKey(givenField)) {
                    throw new RuntimeException("Unexpected field: " + givenField + " in struct instantiation of " + structExpr.structName);
                }
            }

            // Type-check each field
            for (Map.Entry<String, Expression> entry : structExpr.fieldValues.entrySet()) {
                String field = entry.getKey();
                Expression valueExpr = entry.getValue();
                Type expected = def.fields.get(field);
                Type actual = checkExpression(valueExpr, localEnv);

                if (!actual.getClass().equals(expected.getClass())) {
                    throw new RuntimeException("Type mismatch in field '" + field + "' of struct " + structExpr.structName +
                        ": expected " + expected.getClass().getSimpleName() + ", got " + actual.getClass().getSimpleName());
                }
            }

            return new StructType(structExpr.structName);
        } else if (expr instanceof CallExpr) {
        CallExpr call = (CallExpr) expr;
        Type calleeType = checkExpression(call.callee, localEnv);

        // Handle function call
        if (calleeType instanceof FunctionType) {
            FunctionType funcType = (FunctionType) calleeType;
            if (call.arguments.size() != funcType.paramTypes.size()) {
                throw new RuntimeException("Argument count mismatch in function call");
            }

            for (int i = 0; i < call.arguments.size(); i++) {
                Type argType = checkExpression(call.arguments.get(i), localEnv);
                Type expectedType = funcType.paramTypes.get(i);
                if (!argType.getClass().equals(expectedType.getClass())) {
                    throw new RuntimeException("Argument type mismatch at position " + i +
                        ": expected " + expectedType.getClass().getSimpleName() +
                        ", got " + argType.getClass().getSimpleName());
                }
            }
            return funcType.returnType;
        }
        // Case 2: Struct method treated like a funtion (rare, but structurally demanded)
        Map<String, List<FunctionType>> methods = methodsForType.get(calleeType);
        if (methods != null && methods.containsKey(call.callee.toString())) {
            List<FunctionType> overloads = methods.get(call.callee.toString());
        
            for (FunctionType methodType : overloads) {
                // Check argument count
                if (call.arguments.size() != methodType.paramTypes.size()) {
                    continue;
                }
        
                // Check argument types
                boolean match = true;
                for (int i = 0; i < call.arguments.size(); i++) {
                    Type argType = checkExpression(call.arguments.get(i), localEnv);
                    Type expectedType = methodType.paramTypes.get(i);
                    if (!argType.getClass().equals(expectedType.getClass())) {
                        match = false;
                        break;
                    }
                }
        
                if (match) {
                    return methodType.returnType;
                }
            }
        
            throw new RuntimeException("No matching overload found for method-like call to " + call.callee);


        }

        throw new RuntimeException("Trying to call a non-function or non-method");
    } else if (expr instanceof MethodCallExpr) {
        MethodCallExpr mcall = (MethodCallExpr) expr;
    
    // Check the type of the receiver
    Type receiverType = checkExpression(mcall.receiver, localEnv);
    
    // Ensure the receiver is a StructType
    if (!(receiverType instanceof StructType)) {
        throw new RuntimeException("Method call on non-struct type");
    }
    
    StructType structType = (StructType) receiverType;

    // Try to find the method in the struct's method map
    Map<String, List<FunctionType>> methods = methodsForType.get(structType);
    if (methods != null && methods.containsKey(mcall.methodName)) {
        List<FunctionType> overloads = methods.get(mcall.methodName);
        
        // Match overloads based on argument types
        for (FunctionType candidate : overloads) {
            if (candidate.paramTypes.size() != mcall.arguments.size()) {
                continue;
            }
            
            boolean match = true;
            for (int i = 0; i < mcall.arguments.size(); i++) {
                Type argType = checkExpression(mcall.arguments.get(i), localEnv);
                Type expectedType = candidate.paramTypes.get(i);
                if (!argType.getClass().equals(expectedType.getClass())) {
                    match = false;
                    break;
                }
            }

            // Return the matching function's return type
            if (match) {
                return candidate.returnType;
            }
        }
        
        throw new RuntimeException("No matching overload for method " + mcall.methodName + " with given argument types.");
    }
    
    // Fallback to check for methods in implementation blocks
    List<ImplDef> impls = env.impls.get(structType);
    if (impls != null) {
        for (ImplDef impl : impls) {
            Map<String, List<FunctionType>> methodMap = impl.methods;
            
            if (methodMap != null && methodMap.containsKey(mcall.methodName)) {
                List<FunctionType> overloads = methodMap.get(mcall.methodName);
                
                // Match overloads in implementation blocks
                for (FunctionType functionType : overloads) {
                    if (mcall.arguments.size() != functionType.paramTypes.size()) {
                        continue;
                    }

                    boolean match = true;
                    for (int k = 0; k < mcall.arguments.size(); k++) {
                        Type argType = checkExpression(mcall.arguments.get(k), localEnv);
                        Type expectedType = functionType.paramTypes.get(k);
                        if (!argType.getClass().equals(expectedType.getClass())) {
                            match = false;
                            break;
                        }
                    }

                    // Return the matching function's return type
                    if (match) {
                        return functionType.returnType;
                    }
                }
                
                throw new RuntimeException("No matching overload for method " + mcall.methodName + " in impl block.");
            }
        }
    }

    // If no matching method found, throw an exception
    throw new RuntimeException("Method " + mcall.methodName + " not found for struct " + structType.name);
    } else if (expr instanceof FieldAccessExpr f) {
        Type receiverType = checkExpression(f.receiver, localEnv);
    
        if (!(receiverType instanceof StructType)) {
            throw new RuntimeException("Field access on non-struct type: " + receiverType);
        }
    
        StructType st = (StructType) receiverType;
        StructDef def = env.structs.get(st.name);
    
        if (def == null) {
            throw new RuntimeException("Unknown struct type: " + st.name);
        }
    
        Type fieldType = def.fields.get(f.field);
        if (fieldType == null) {
            throw new RuntimeException("Field '" + f.field + "' not found in struct " + st.name);
        }
    
        return fieldType;
    }





        throw new RuntimeException("Unsupported expression");
    }
}
