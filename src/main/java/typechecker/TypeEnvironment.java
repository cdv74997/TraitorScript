package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class TypeEnvironment {
    Map<String, Type> variables = new HashMap<>();
    Map<String, StructDef> structs = new HashMap<>();
    Map<String, TraitDef> traits = new HashMap<>();
    Map<String, List<ImplDef>> impls = new HashMap<>();
}