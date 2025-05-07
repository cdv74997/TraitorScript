package parser;

//import java.util.Map;

//public record NewExp(String structname, Map<String, Exp> fields) implements Exp{}
import java.util.List;

public record NewExp(String structName, List<StructActualParam> params) implements Exp {
    @Override
    public String toString() {
        return "new " + structName + " {" + params + "}";
    }
}