package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class FunctionType implements Type {
    public List<Type> paramTypes;
    public Type returnType;

    FunctionType(List<Type> paramTypes, Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }
    
}