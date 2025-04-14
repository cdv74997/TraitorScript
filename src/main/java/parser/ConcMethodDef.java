package parser;

import java.util.List;

public record ConcMethodDef(String name, List<Param> params, Type returnType, List<Stmt> body) {}
