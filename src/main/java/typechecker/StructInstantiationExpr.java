package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class StructInstantiationExpr implements Expression {
    String structName;
    Map<String, Expression> fieldValues;

    StructInstantiationExpr(String structName, Map<String, Expression> fieldValues) {
        this.structName = structName;
        this.fieldValues = fieldValues;
    }
}