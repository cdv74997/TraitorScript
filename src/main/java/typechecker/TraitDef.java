package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class TraitDef extends Definition {
    public String name;
    public Map<String, FunctionType> methods;

    public TraitDef(String name, Map<String, FunctionType> methods) {
        this.name = name;
        this.methods = methods;
    }
}