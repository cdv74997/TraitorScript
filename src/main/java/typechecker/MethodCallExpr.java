package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class MethodCallExpr implements Expression {
    public Expression receiver;
    public String methodName;
    public List<Expression> arguments;

    MethodCallExpr(Expression receiver, String methodName, List<Expression> arguments) {
        this.receiver = receiver;
        this.methodName = methodName;
        this.arguments = arguments;
    }
}