package parser;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionType(List<Type> paramTypes, Type returnType) implements Type {
    @Override
    public String toString() {
        String params = paramTypes.stream()
                                  .map(Type::toString)
                                  .collect(Collectors.joining(", "));
        return "(" + params + ") => " + returnType;
    }
}
