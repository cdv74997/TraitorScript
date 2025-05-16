package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class StructDef extends Definition {
    public String name;
    public Map<String, Type> fields;

    public StructDef(String name, Map<String, Type> fields) {
        this.name = name;
        this.fields = fields;
    }
}