package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class ImplDef extends Definition {
    public String traitName;
    public Type forType;
    public Map<String, List<FunctionType>> methods;

    //Map<String, FunctionType> methods;

    public ImplDef(String traitName, Type forType, Map<String, List<FunctionType>> methods) {
        this.traitName = traitName;
        this.forType = forType;
        this.methods = methods;
    }
}