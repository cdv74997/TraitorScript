package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class BlockStatement implements Statement {
    public final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }
}