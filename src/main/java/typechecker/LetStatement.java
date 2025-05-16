package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class LetStatement implements Statement {
    Param param;
    Expression expr;

    LetStatement(Param param, Expression expr) {
        this.param = param;
        this.expr = expr;
    }
}