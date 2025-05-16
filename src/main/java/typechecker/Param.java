package typechecker;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class Param {
    public String name;
    public Type type;

    public Param(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}