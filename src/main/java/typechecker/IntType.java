package typechecker;
public class IntType implements Type {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntType;  // Only compare if it's the same class
    }
}