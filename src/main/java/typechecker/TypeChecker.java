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
    Expression callee;
    List<Expression> arguments;

    CallExpr(Expression callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
}


class StructType implements Type {
    String name;
    StructType(String name) { this.name = name; }
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
    Map<String, FunctionType> methods;

    ImplDef(String traitName, Type forType, Map<String, FunctionType> methods) {
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

    
    TypeEnvironment env = new TypeEnvironment();

    public void checkStruct(StructDef struct) {
        env.structs.put(struct.name, struct);
    }

    public FunctionType getTraitMethodType(String traitName, String methodName, Type receiverType) {
        List<ImplDef> impls = env.impls.get(traitName);
        if (impls == null) return null;
        for (ImplDef impl : impls) {
            if (impl.forType.equals(receiverType)) {
                return impl.methods.get(methodName);
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

        // Handle method call for struct implementations
        if (calleeType instanceof StructType) {
            StructType structType = (StructType) calleeType;
            List<ImplDef> implsForStruct = env.impls.getOrDefault(structType.name, new ArrayList<>());

            for (List<ImplDef> implList : env.impls.values()) {
                for (ImplDef impl : implList) {
                    if (impl.forType instanceof StructType) {
                        StructType implType = (StructType) impl.forType;
                        if (implType.name.equals(structType.name)) {
                            implsForStruct.add(impl);
                        }
                    }
                }
            }
            throw new RuntimeException("Method " + call.callee + " not found for struct " + structType.name);
        }

        throw new RuntimeException("Trying to call a non-function or non-method");
    } else if (expr instanceof MethodCallExpr) {
        MethodCallExpr mcall = (MethodCallExpr) expr;
        Type receiverType = checkExpression(mcall.receiver, localEnv);
    
        if (!(receiverType instanceof StructType)) {
            throw new RuntimeException("Method call on non-struct type");
        }
    
        StructType structType = (StructType) receiverType;
    
        for (List<ImplDef> implList : env.impls.values()) {
            for (ImplDef impl : implList) {
                if (!(impl.forType instanceof StructType)) continue;
                StructType forStruct = (StructType) impl.forType;
                if (!forStruct.name.equals(structType.name)) continue;
    
                FunctionType methodType = impl.methods.get(mcall.methodName);
                if (methodType == null) continue;
    
                if (mcall.arguments.size() != methodType.paramTypes.size()) {
                    throw new RuntimeException("Argument count mismatch in method call");
                }
    
                for (int i = 0; i < mcall.arguments.size(); i++) {
                    Type argType = checkExpression(mcall.arguments.get(i), localEnv);
                    Type expectedType = methodType.paramTypes.get(i);
                    if (!argType.getClass().equals(expectedType.getClass())) {
                        throw new RuntimeException("Argument type mismatch at position " + i);
                    }
                }
    
                return methodType.returnType;
            }
        }
    
        throw new RuntimeException("Method " + mcall.methodName + " not found for struct " + structType.name);
    }


        throw new RuntimeException("Unsupported expression");
    }
}
