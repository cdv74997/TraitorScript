package parser;

import java.util.List;

public record AbsMethodDef(String name, List<Param> params, Type returnType) {}