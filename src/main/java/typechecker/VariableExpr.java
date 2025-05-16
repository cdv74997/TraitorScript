package typechecker;
public class VariableExpr implements Expression {
    public String name;
    public VariableExpr(String name) { this.name = name; }
}
