package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class FunctionDef extends Definition {
    public String name;
    public List<Param> params;
    public Type returnType;
    public List<Statement> body;

    FunctionDef(String name, List<Param> params, Type returnType, List<Statement> body) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}