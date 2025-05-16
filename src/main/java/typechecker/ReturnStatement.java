package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class ReturnStatement implements Statement {
    Expression expr;

    ReturnStatement(Expression expr) {
        this.expr = expr;
    }
}