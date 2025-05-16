package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class MethodDef {
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