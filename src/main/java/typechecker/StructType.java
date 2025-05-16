package typechecker;
public class StructType implements Type {
    public String name;
    public StructType(String name) { this.name = name; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructType)) return false;
        StructType other = (StructType) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "StructType(" + name + ")";
    }
}