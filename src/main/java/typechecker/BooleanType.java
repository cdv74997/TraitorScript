package typechecker;

public class BooleanType implements Type {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BooleanType;  // Only compare if it's the same class
    }
}