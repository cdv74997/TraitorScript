import java.util.*;




interface Type {}

class IntType implements Type {}
class VoidType implements Type {}
class BooleanType implements Type {}

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


interface Statement {}
interface Expression {}

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

class IntLiteralExpr implements Expression {
    int value;
    IntLiteralExpr(int value) { this.value = value; }
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



class TypeEnvironment {
    Map<String, Type> variables = new HashMap<>();
    Map<String, StructDef> structs = new HashMap<>();
    Map<String, TraitDef> traits = new HashMap<>();
    Map<String, List<ImplDef>> impls = new HashMap<>();
}


public class TypeChecker {

    TypeEnvironment env = new TypeEnvironment();

    public void checkStruct(StructDef struct) {
        env.structs.put(struct.name, struct);
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
        }
        throw new RuntimeException("Unsupported expression");
    }
}
