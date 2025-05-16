package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class CallExpr implements Expression {
    public VariableExpr callee;
    public List<Expression> arguments;

    CallExpr(VariableExpr callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
}
